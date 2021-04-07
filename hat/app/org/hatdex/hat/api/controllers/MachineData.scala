/*
 * Copyright (C) 2017 HAT Data Exchange Ltd
 * SPDX-License-Identifier: AGPL-3.0
 *
 * This file is part of the Hub of All Things project (HAT).
 *
 * HAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation, version 3 of
 * the License.
 *
 * HAT is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General
 * Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Written by Tyler Weir <tyler.weir@dataswift.io>
 * 1 / 2021
 */

package org.hatdex.hat.api.controllers

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }
import scala.concurrent.duration._

import com.mohiva.play.silhouette.api.Silhouette
import dev.profunktor.auth.jwt.JwtSecretKey
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.collection.NonEmpty
import io.dataswift.adjudicator.ShortLivedTokenOps
import io.dataswift.adjudicator.Types.{ DeviceId, HatName, ShortLivedToken }
import io.dataswift.models.hat._
import io.dataswift.models.hat.applications.Application
import io.dataswift.models.hat.json.RichDataJsonFormats
import org.hatdex.hat.NamespaceUtils.NamespaceUtils
import org.hatdex.hat.api.controllers.RequestValidationFailure._
import org.hatdex.hat.api.service.UsersService
import org.hatdex.hat.api.service.applications.{ ApplicationsService, TrustedApplicationProvider }
import org.hatdex.hat.api.service.monitoring.HatDataEventDispatcher
import org.hatdex.hat.api.service.richData._
import org.hatdex.hat.authentication.models._
import org.hatdex.hat.authentication.{ HatApiAuthEnvironment, HatApiController }
import org.hatdex.hat.resourceManagement.HatServer
import org.hatdex.hat.utils.AuthServiceRequestTypes._
import org.hatdex.hat.utils.{ AuthServiceRequest, HatBodyParsers, LoggingProvider }
import org.hatdex.libs.dal.HATPostgresProfile
import pdi.jwt.JwtClaim
import play.api.Configuration
import play.api.libs.json.Reads._
import play.api.libs.json.{ JsArray, JsValue, Json, Reads }
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.libs.json.JsResult
import scala.concurrent.Await

class MachineData @Inject() (
    components: ControllerComponents,
    parsers: HatBodyParsers,
    silhouette: Silhouette[HatApiAuthEnvironment],
    dataService: RichDataService,
    loggingProvider: LoggingProvider,
    configuration: Configuration,
    usersService: UsersService,
    trustedApplicationProvider: TrustedApplicationProvider,
    implicit val ec: ExecutionContext,
    implicit val applicationsService: ApplicationsService
  )(wsClient: WSClient)
    extends HatApiController(components, silhouette) {
  import RichDataJsonFormats._

  private val logger             = loggingProvider.logger(this.getClass)
  private val defaultRecordLimit = 1000

  // AuthService
  private val authServiceAddress =
    configuration.underlying.getString("authservice.address")
  private val authServiceScheme =
    configuration.underlying.getString("authservice.scheme")
  private val authServiceEndpoint =
    s"${authServiceScheme}${authServiceAddress}"
  private val authServiceSharedSecret =
    configuration.underlying.getString("authservice.sharedSecret")
  private val authServiceClient = new AuthServiceRequest(
    authServiceEndpoint,
    JwtSecretKey(authServiceSharedSecret),
    wsClient
  )

  // Types
  // the device id is encoded in the accesstoken
  case class DeviceDataInfoRefined(
      token: ShortLivedToken,
      hatName: HatName,
      deviceId: DeviceId)

  case class DeviceDataReadRequest(
      token: String,
      hatName: String,
      deviceId: String)
  implicit val deviceDataReadRequestReads: Reads[DeviceDataReadRequest] = Json.reads[DeviceDataReadRequest]

  case class DeviceDataCreateRequest(
      token: String,
      hatName: String,
      deviceId: String,
      body: Option[JsValue])
  implicit val deviceDataCreateRequestReads: Reads[DeviceDataCreateRequest] = Json.reads[DeviceDataCreateRequest]

  case class DeviceDataUpdateRequest(
      token: String,
      hatName: String,
      deviceId: String,
      body: Seq[EndpointData])
  implicit val machineDataUpdateRequestReads: Reads[DeviceDataUpdateRequest] = Json.reads[DeviceDataUpdateRequest]

  case class SLTokenBody(
      iss: String,
      exp: Long,
      deviceId: String)
  implicit val sltokenBodyReads: Reads[SLTokenBody] = Json.reads[SLTokenBody]

  // Errors
  sealed abstract class DeviceVerificationFailure
  object DeviceVerificationFailure {
    final case class ServiceRespondedWithFailure(failureDescription: String) extends DeviceVerificationFailure
    final case class InvalidTokenFailure(failureDescription: String) extends DeviceVerificationFailure
    final case class InvalidDeviceDataRequestFailure(
        failureDescription: String)
        extends DeviceVerificationFailure
  }
  sealed abstract class DeviceVerificationSuccess
  object DeviceVerificationSuccess {
    final case class JwtClaimVerified(jwtClaim: JwtClaim) extends DeviceVerificationSuccess
  }

  // -- Error Handler
  def handleFailedRequestAssessment(failure: RequestValidationFailure): Future[Result] =
    failure match {
      case HatNotFound(hatName)             => Future.successful(BadRequest(s"HatName not found: ${hatName}"))
      case MissingHatName(hatName)          => Future.successful(BadRequest(s"Missing HatName: ${hatName}"))
      case InaccessibleNamespace(namespace) => Future.successful(BadRequest(s"Namespace Inaccessible: ${namespace}"))
      case InvalidShortLivedToken(deviceId) => Future.successful(BadRequest(s"Invalid Token: ${deviceId}"))
      case GeneralError                     => Future.successful(BadRequest("Unknown Error"))
    }

  // -- Operations

  // -- Create
  def handleCreateDeviceData(
      hatUser: HatUser,
      machineDataCreate: DeviceDataCreateRequest,
      namespace: String,
      endpoint: String,
      skipErrors: Option[Boolean]
    )(implicit hatServer: HatServer): Future[Result] =
    machineDataCreate.body match {
      // Missing Json Body, do nothing, return error
      case None =>
        logger.error(s"saveDeviceData included no json body - ns:${namespace} - endpoint:${endpoint}")
        Future.successful(NotFound)
      // There is a Json body, process either an JsArray or a JsValue
      case Some(jsBody) =>
        val dataEndpoint = s"$namespace/$endpoint"
        jsBody match {
          case array: JsArray =>
            logger.info(s"saveDeviceData.body is JsArray: ${machineDataCreate.body}")
            handleJsArray(
              hatUser.userId,
              array,
              dataEndpoint,
              skipErrors
            )
          case value: JsValue =>
            logger.info(s"saveDeviceData.body is JsValue: ${machineDataCreate.body}")
            handleJsValue(
              hatUser.userId,
              value,
              dataEndpoint
            )
        }
    }

  // -- Update
  def handleUpdateDeviceData(
      hatUser: HatUser,
      machineDataUpdate: DeviceDataUpdateRequest,
      namespace: String
    )(implicit hatServer: HatServer): Future[Result] =
    machineDataUpdate.body.length match {
      // Missing Json Body, do nothing, return error
      case 0 =>
        logger.error(s"updateDeviceData included no json body - ns:${namespace}")
        Future.successful(NotFound)
      // There is a Json body, process either an JsArray or a JsValue
      case _ =>
        val dataSeq = machineDataUpdate.body.map(item => item.copy(endpoint = s"${namespace}/${item.endpoint}"))
        dataService.updateRecords(
          hatUser.userId,
          dataSeq
        ) map { saved =>
          Created(Json.toJson(saved))
        } recover {
          case RichDataMissingException(message, _) =>
            BadRequest(Json.toJson(ErrorsFromRichData.dataUpdateMissing(message)))
        }
    }

  // --- API Handlers ---

  def getData(
      endpoint: String,
      orderBy: Option[String],
      ordering: Option[String],
      skip: Option[Int],
      take: Option[Int]): Action[DeviceDataReadRequest] =
    UserAwareAction.async(parsers.json[DeviceDataReadRequest]) { implicit request =>
      val deviceDataRead      = request.body
      val maybeDeviceDataInfo = refineDeviceDataReadRequest(deviceDataRead)
      maybeDeviceDataInfo match {
        case Some(deviceDataInfo) =>
          deviceValid(deviceDataInfo, "NAMESPACE").flatMap { deviceOk =>
            deviceOk match {
              case (Some(_hatUser @ _), Right(RequestVerified(_ns @ _))) =>
                makeData("NAMESPACE", endpoint, orderBy, ordering, skip, take)
              case (_, Left(x)) => handleFailedRequestAssessment(x)
              case (None, Right(_)) =>
                logger.warn(s"ReadDevice: Hat not found for:  ${deviceDataRead}")
                handleFailedRequestAssessment(HatNotFound(deviceDataInfo.hatName.toString))
              case (_, _) =>
                logger.warn(s"ReadDevice: Fallback Error case for: ${deviceDataRead}")
                handleFailedRequestAssessment(GeneralError)
            }
          }
        case None => Future.successful(BadRequest("Missing Device Details."))
      }
    }

  def createData(
      endpoint: String,
      skipErrors: Option[Boolean]): Action[DeviceDataCreateRequest] =
    UserAwareAction.async(parsers.json[DeviceDataCreateRequest]) { implicit request =>
      val deviceDataCreate = request.body
      val xAuthToken       = request.headers.get("X-Auth-Token")
      val hatName          = "bobtheplumber"
      val hatUserE         = usersService.getUser(hatName)
      val hatUser          = Await.result(hatUserE, 30.seconds).get

      xAuthToken match {
        case Some(bearerToken) =>
          // Fragile
          val tokenOnly = bearerToken.split(" ")(1).trim
          println(s"token only     : ${tokenOnly}")
          val tokenBody = ShortLivedTokenOps.getBody(tokenOnly).getOrElse("")
          println(s"token body     : ${tokenBody}")

          val tokenBodyJson: Option[SLTokenBody] = Json.parse(tokenBody).validate[SLTokenBody].asOpt
          println(s"token body json: ${tokenBodyJson}")

          tokenBodyJson match {
            case Some(sltokenBody) =>
              val d = refineDeviceInfo(tokenOnly, hatName, sltokenBody.deviceId)
              d match {
                case Some(dd) =>
                  verifyDevice(dd).flatMap { r1 =>
                    handleCreateDeviceData(hatUser, deviceDataCreate, sltokenBody.deviceId, endpoint, skipErrors)
                  }
                case None =>
                  println("fail")
              }
            case None =>
          }

        case None =>
          println("fail")
      }

      Future.successful(BadRequest("Testint"))
    // val deviceDataCreate    = request.body
    // val maybeDeviceDataInfo = refineDeviceDataCreateRequest(deviceDataCreate)
    // maybeDeviceDataInfo match {
    //   case Some(deviceDataInfo) =>
    //     deviceValid(deviceDataInfo, "NAMESPACE").flatMap { deviceOk =>
    //       deviceOk match {
    //         case (Some(hatUser), Right(RequestVerified(_ns))) =>
    //           handleCreateDeviceData(hatUser, deviceDataCreate, "NAMESPACE", endpoint, skipErrors)
    //         case (_, Left(x)) => handleFailedRequestAssessment(x)
    //         case (None, Right(_)) =>
    //           logger.warn(s"CreateDevice: Hat not found for:  ${deviceDataCreate}")
    //           handleFailedRequestAssessment(HatNotFound(deviceDataInfo.hatName.toString))
    //         case (_, _) =>
    //           logger.warn(s"CreateDevice: Fallback Error case for: ${deviceDataCreate}")
    //           handleFailedRequestAssessment(GeneralError)
    //       }
    //     }
    //   case None => Future.successful(BadRequest("Missing Device Details."))
    // }
    }

  def updateData: Action[DeviceDataUpdateRequest] =
    UserAwareAction.async(parsers.json[DeviceDataUpdateRequest]) { implicit request =>
      val deviceDataUpdate    = request.body
      val maybeDeviceDataInfo = refineDeviceDataUpdateRequest(deviceDataUpdate)
      maybeDeviceDataInfo match {
        case Some(deviceDataInfo) =>
          deviceValid(deviceDataInfo, "NAMESPACE").flatMap { deviceOk =>
            deviceOk match {
              case (Some(hatUser), Right(RequestVerified(ns))) =>
                handleUpdateDeviceData(hatUser, deviceDataUpdate, ns)
              case (_, Left(x)) => handleFailedRequestAssessment(x)
              case (None, Right(_)) =>
                logger.warn(s"UpdateDevice: Hat not found for:  ${deviceDataUpdate}")
                handleFailedRequestAssessment(HatNotFound(deviceDataInfo.hatName.toString))
              case (_, _) =>
                logger.warn(s"UpdateDevice: Fallback Error case for:  ${deviceDataUpdate}")
                handleFailedRequestAssessment(GeneralError)
            }
          }
        case None => Future.successful(BadRequest("Missing Device Details."))
      }
    }

  private def makeData(
      namespace: String,
      endpoint: String,
      orderBy: Option[String],
      ordering: Option[String],
      skip: Option[Int],
      take: Option[Int]
    )(implicit db: HATPostgresProfile.api.Database): Future[Result] = {
    val dataEndpoint = s"$namespace/$endpoint"
    val query =
      Seq(EndpointQuery(dataEndpoint, None, None, None))
    val data = dataService.propertyData(
      query,
      orderBy,
      ordering.contains("descending"),
      skip.getOrElse(0),
      take.orElse(Some(defaultRecordLimit))
    )
    data.map(d => Ok(Json.toJson(d)))
  }

  trait DeviceRequest {
    def refineDevice: DeviceDataInfoRefined
  }

  def refineDeviceInfo(
      token: String,
      hatName: String,
      deviceId: String): Option[DeviceDataInfoRefined] =
    for {
      tokenR <- refineV[NonEmpty](token).toOption
      hatNameR <- refineV[NonEmpty](hatName).toOption
      deviceIdR <- refineV[NonEmpty](deviceId).toOption
      deviceDataInfoRefined = DeviceDataInfoRefined(
                                ShortLivedToken(tokenR),
                                HatName(hatNameR),
                                DeviceId(deviceIdR)
                              )
    } yield deviceDataInfoRefined

  def refineDeviceDataReadRequest(req: DeviceDataReadRequest): Option[DeviceDataInfoRefined] =
    refineDeviceInfo(req.token, req.hatName, req.deviceId)

  def refineDeviceDataCreateRequest(req: DeviceDataCreateRequest): Option[DeviceDataInfoRefined] =
    refineDeviceInfo(req.token, req.hatName, req.deviceId)

  def refineDeviceDataUpdateRequest(req: DeviceDataUpdateRequest): Option[DeviceDataInfoRefined] =
    refineDeviceInfo(req.token, req.hatName, req.deviceId)

  def deviceValid(
      device: DeviceDataInfoRefined,
      namespace: String
    )(implicit hatServer: HatServer): Future[(Option[HatUser], Either[RequestValidationFailure, RequestVerified])] =
    for {
      hatUser <- usersService.getUser(device.hatName.value)
      requestAssestment <- assessRequest(device, namespace)
    } yield (hatUser, requestAssestment)

  def handleJsArray(
      userId: UUID,
      array: JsArray,
      dataEndpoint: String,
      skipErrors: Option[Boolean]
    )(implicit hatServer: HatServer): Future[Result] = {
    val values =
      array.value.map(EndpointData(dataEndpoint, None, None, None, _, None))
    logger.info(s"handleJsArray: Values: ${values}")
    dataService
      .saveData(userId, values.toSeq, skipErrors.getOrElse(false))
      .map(saved => Created(Json.toJson(saved)))
  }

  def handleJsValue(
      userId: UUID,
      value: JsValue,
      dataEndpoint: String
    )(implicit hatServer: HatServer): Future[Result] = {
    val values = Seq(EndpointData(dataEndpoint, None, None, None, value, None))
    logger.info(s"handleJsArray: Values: ${values}")
    dataService
      .saveData(userId, values)
      .map(saved => Created(Json.toJson(saved.head)))
  }

  def verifyNamespace(
      app: Application,
      namespace: String): Boolean = {
    logger.info(s"verifyNamespace ${namespace} for app ${app}")

    val canReadNamespace  = verifyNamespaceRead(app, namespace)
    val canWriteNamespace = verifyNamespaceWrite(app, namespace)

    logger.info(
      s"def verifyNamespace read: ${canReadNamespace} - write: ${canWriteNamespace}"
    )

    (canReadNamespace || canWriteNamespace)
  }

  def verifyNamespaceRead(
      app: Application,
      namespace: String): Boolean = {
    val rolesOk = NamespaceUtils.testReadNamespacePermissions(app.permissions.rolesGranted, namespace)
    logger.info(
      s"NamespaceRead: AppPerms: ${app.permissions}, RolesOk: ${rolesOk}, namespace: ${namespace}, result: ${rolesOk}"
    )

    rolesOk
  }

  def verifyNamespaceWrite(
      app: Application,
      namespace: String): Boolean = {
    val rolesOk = NamespaceUtils.testWriteNamespacePermissions(app.permissions.rolesGranted, namespace)
    logger.info(
      s"NamespaceWrite: AppPerms: ${app.permissions}, RolesOk: ${rolesOk}, namespace: ${namespace}, result: ${rolesOk}"
    )

    rolesOk
  }

  private def requestKeyId(
      deviceDataInfo: DeviceDataInfoRefined): Option[String] =
    for {
      keyId <- ShortLivedTokenOps
                 .getKeyId(deviceDataInfo.token.value)
                 .toOption
    } yield keyId

  def verifyDevice(deviceDataInfo: DeviceDataInfoRefined): Future[
    Either[DeviceVerificationFailure, DeviceVerificationSuccess]
  ] = {
    import DeviceVerificationFailure._

    // This logic is tied up with special types
    val maybeDeviceDataRequestKeyId = for {
      keyId <- requestKeyId(deviceDataInfo)
    } yield keyId

    maybeDeviceDataRequestKeyId match {
      case Some(keyId) =>
        logger.info(s"DeviceData-keyId: ${keyId}")
        verifyTokenWithAdjudicator(deviceDataInfo, keyId)
      case _ =>
        Future.successful(
          Left(
            InvalidDeviceDataRequestFailure(
              "Device Data Request or KeyId missing"
            )
          )
        )
    }
  }

  def verifyJwtClaim(
      deviceRequestBodyRefined: DeviceDataInfoRefined,
      publicKeyAsByteArray: Array[Byte]): Either[DeviceVerificationFailure, DeviceVerificationSuccess] = {
    import DeviceVerificationFailure._
    import DeviceVerificationSuccess._

    logger.error(
      s"DeviceData.verifyJwtClaim.token: ${deviceRequestBodyRefined.token.toString}"
    )
    logger.error(
      s"DeviceData.verifyJwtClaim.pubKey: ${publicKeyAsByteArray}"
    )

    val tryJwtClaim = ShortLivedTokenOps.verifyToken(
      Some(deviceRequestBodyRefined.token.toString),
      publicKeyAsByteArray
    )
    tryJwtClaim match {
      case Success(jwtClaim) => Right(JwtClaimVerified(jwtClaim))
      case Failure(errorMsg) =>
        logger.error(
          s"DeviceData.verifyJwtClaim.failureMessage: ${errorMsg.getMessage}"
        )
        Left(
          InvalidTokenFailure(
            s"Token: ${deviceRequestBodyRefined.token.toString} was not verified."
          )
        )
    }
  }

  def verifyTokenWithAdjudicator(
      deviceRequestBodyRefined: DeviceDataInfoRefined,
      keyId: String): Future[
    Either[DeviceVerificationFailure, DeviceVerificationSuccess]
  ] =
    authServiceClient
      .getPublicKey(
        deviceRequestBodyRefined.hatName,
        deviceRequestBodyRefined.deviceId,
        keyId
      )
      .map { publicKeyResponse =>
        publicKeyResponse match {
          case Left(
                PublicKeyRequestFailure.ServiceRespondedWithFailure(
                  failureDescription
                )
              ) =>
            Left(
              DeviceVerificationFailure.ServiceRespondedWithFailure(
                s"The Adjudicator Service responded with an error: ${failureDescription}"
              )
            )
          case Left(
                PublicKeyRequestFailure.InvalidPublicKeyFailure(
                  failureDescription
                )
              ) =>
            Left(
              DeviceVerificationFailure.ServiceRespondedWithFailure(
                s"The Adjudicator Service responded with an error: ${failureDescription}"
              )
            )
          case Right(PublicKeyReceived(publicKey)) =>
            verifyJwtClaim(deviceRequestBodyRefined, publicKey)
        }
      }

  def assessRequest(
      deviceDataInfo: DeviceDataInfoRefined,
      namespace: String): Future[Either[RequestValidationFailure, RequestVerified]] = {
    val eventuallyMaybeDecision = verifyDevice(deviceDataInfo)
    val eventuallyMaybeApp =
      trustedApplicationProvider.application(deviceDataInfo.deviceId.value.toString())

    eventuallyMaybeDecision.flatMap { maybeDecision =>
      eventuallyMaybeApp.flatMap { maybeApp =>
        logger.info(s"AssessRequest: ${maybeDecision} - ${maybeApp} - ${namespace}")
        decide(maybeDecision, maybeApp, namespace) match {
          case Some(ns) =>
            logger.info(s"Found a namespace: ${ns}")
            Future.successful(
              Right(
                RequestVerified(s"Token: ${deviceDataInfo.deviceId}")
              )
            )
          case None =>
            logger.error(s"def assessRequest: decide returned None - ${deviceDataInfo} - ${namespace}")
            Future.successful(
              Left(
                RequestValidationFailure.InvalidShortLivedToken(
                  s"Token: ${deviceDataInfo.deviceId}"
                )
              )
            )
        }
      }
    } recover {
      case e =>
        logger.error(s"DeviceData.assessRequest:Failure ${e}")
        Left(
          RequestValidationFailure.InvalidShortLivedToken(
            s"Token: ${deviceDataInfo.deviceId}"
          )
        )
    }
  }

  def decide(
      eitherDecision: Either[
        DeviceVerificationFailure,
        DeviceVerificationSuccess
      ],
      maybeApp: Option[Application],
      namespace: String): Option[String] = {
    import DeviceVerificationSuccess._

    logger.info(s"def decide: decision: ${eitherDecision} - app: ${maybeApp} - ns: ${namespace}")

    (eitherDecision, maybeApp) match {
      case (Right(JwtClaimVerified(_jwtClaim @ _)), Some(app)) =>
        logger.info(s"def decide: JwtClaim verified for app ${app}")
        if (verifyNamespace(app, namespace))
          Some(namespace)
        else
          None
      case (Left(decision), _) =>
        logger.error(s"def decide: decision: ${decision}")
        None
      case (_, _) =>
        logger.error(s"def decide: decision is no.")
        None
    }
  }

  private object ErrorsFromRichData {
    def dataUpdateMissing(message: String): ErrorMessage =
      ErrorMessage("Data Missing", s"Could not update records: $message")
  }
}