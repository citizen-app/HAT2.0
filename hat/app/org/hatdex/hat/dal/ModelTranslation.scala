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
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>
 * 2 / 2017
 */

package org.hatdex.hat.dal

import org.hatdex.hat.api.json.HatJsonFormats
import org.hatdex.hat.dal.Tables._
import org.hatdex.hat.api.models.{ UserRole, _ }
import org.hatdex.hat.dal.Tables.{ UserRole => UserRoleDb }
import org.hatdex.hat.authentication.models.{ HatAccessLog, HatUser }
import org.hatdex.hat.phata.models.MailTokenUser

import scala.annotation.tailrec

object ModelTranslation {
  def fromDbModel(user: UserUserRow): HatUser = {
    HatUser(user.userId, user.email, user.pass, user.name,
      Seq(), user.enabled)
  }

  def fromDbModel(userInfo: (UserUserRow, Seq[UserRoleRow])): HatUser = {
    val roles = userInfo._2.map(r => UserRole.userRoleDeserialize(r.role, r.extra))
    val user = userInfo._1
    HatUser(user.userId, user.email, user.pass, user.name, roles, user.enabled)
  }

  def fromInternalModel(user: HatUser): User = {
    User(user.userId, user.email, user.pass, user.name, user.roles.headOption.map(_.title).getOrElse(""), user.roles)
  }
  def fromExternalModel(user: User, enabled: Boolean): HatUser = {
    HatUser(user.userId, user.email, user.pass, user.name, user.roles, enabled).withRoles(UserRole.userRoleDeserialize(user.role, None))
  }

  def fromDbModel(field: DataFieldRow): ApiDataField = {
    ApiDataField(
      Some(field.id), Some(field.dateCreated), Some(field.lastUpdated),
      Some(field.tableIdFk), field.name, None)
  }

  def fromDbModel(record: DataRecordRow, tables: Option[Seq[ApiDataTable]]): ApiDataRecord = {
    new ApiDataRecord(
      Some(record.id), Some(record.dateCreated), Some(record.lastUpdated),
      record.name, tables)
  }

  def fromDbModel(table: DataTableRow, fields: Option[Seq[ApiDataField]], subTables: Option[Seq[ApiDataTable]]): ApiDataTable = {
    new ApiDataTable(
      Some(table.id),
      Some(table.dateCreated),
      Some(table.lastUpdated),
      table.name,
      table.sourceName,
      fields,
      subTables)
  }

  def fromDbModel(table: DataTableTreeRow, fields: Option[Seq[ApiDataField]], subTables: Option[Seq[ApiDataTable]]): ApiDataTable = {
    new ApiDataTable(
      table.id,
      table.dateCreated,
      table.lastUpdated,
      table.name.getOrElse(""),
      table.sourceName.getOrElse(""),
      fields,
      subTables)
  }

  def fromDbModel(value: DataValueRow): ApiDataValue = {
    ApiDataValue(
      Some(value.id), Some(value.dateCreated), Some(value.lastUpdated),
      value.value, None, None)
  }

  def fromDbModel(value: DataValueRow, field: DataFieldRow, record: DataRecordRow): ApiDataValue = {
    val apiRecord = fromDbModel(record, None)
    ApiDataValue(Some(value.id), Some(value.dateCreated), None,
      value.value, Some(fromDbModel(field)), Some(apiRecord))
  }

  def fromDbModel(value: DataValueRow, maybeApiField: Option[ApiDataField], maybeApiRecord: Option[ApiDataRecord]): ApiDataValue = {
    ApiDataValue(Some(value.id), Some(value.dateCreated), None,
      value.value, maybeApiField, maybeApiRecord)
  }

  def fromDbModel(value: DataStatsLogRow): DataStats = {
    import org.hatdex.hat.api.json.DataStatsFormat.dataStatsFormat
    value.stats.as[DataStats]
  }

  def fromDbModel(bundleContextless: BundleContextlessRow): ApiBundleContextless = {
    ApiBundleContextless(
      Some(bundleContextless.id),
      Some(bundleContextless.dateCreated), Some(bundleContextless.lastUpdated),
      bundleContextless.name, None)
  }

  def fromDbModel(bundleContextless: BundleContextlessRow, sources: Option[Seq[ApiBundleDataSourceStructure]]): ApiBundleContextless = {
    ApiBundleContextless(
      Some(bundleContextless.id),
      Some(bundleContextless.dateCreated), Some(bundleContextless.lastUpdated),
      bundleContextless.name, sources)
  }

  def fromDbModel(dataDebitRow: DataDebitRow): ApiDataDebit = {
    ApiDataDebit(Some(dataDebitRow.dataDebitKey), Some(dataDebitRow.dateCreated), Some(dataDebitRow.lastUpdated),
      dataDebitRow.name, dataDebitRow.startDate, dataDebitRow.endDate, Some(dataDebitRow.enabled), dataDebitRow.rolling, dataDebitRow.sellRent,
      dataDebitRow.price, dataDebitRow.kind, None, None)
  }

  def fromDbModel(
    dataDebitRow: DataDebitRow,
    apiBundleContextlessData: Option[ApiBundleContextlessData],
    apiBundleContextualData: Option[Seq[ApiEntity]]): ApiDataDebitOut = {
    ApiDataDebitOut(Some(dataDebitRow.dataDebitKey), Some(dataDebitRow.dateCreated), Some(dataDebitRow.lastUpdated),
      dataDebitRow.name, dataDebitRow.startDate, dataDebitRow.endDate, Some(dataDebitRow.enabled), dataDebitRow.rolling, dataDebitRow.sellRent,
      dataDebitRow.price, dataDebitRow.kind, apiBundleContextlessData, apiBundleContextualData)
  }

  def fromDbModel(
    userMailTokensRow: UserMailTokensRow): MailTokenUser = {
    MailTokenUser(userMailTokensRow.id, userMailTokensRow.email, userMailTokensRow.expirationTime.toDateTime, userMailTokensRow.isSignup)
  }

  def fromDbModel(hatFileRow: HatFileRow): ApiHatFile = {
    import HatJsonFormats.apiHatFileStatusFormat
    ApiHatFile(Some(hatFileRow.id), hatFileRow.name, hatFileRow.source,
      Some(hatFileRow.dateCreated.toDateTime), Some(hatFileRow.lastUpdated.toDateTime),
      hatFileRow.tags, hatFileRow.title, hatFileRow.description, hatFileRow.sourceUrl,
      Some(hatFileRow.status.as[HatFileStatus.Status]), None, Some(hatFileRow.contentPublic), None)
  }

  def fromDbModel(hatFileAccessRow: HatFileAccessRow): ApiHatFilePermissions = {
    ApiHatFilePermissions(hatFileAccessRow.userId, hatFileAccessRow.content)
  }

  def fromDbModel(userAccessLogRow: UserAccessLogRow, user: HatUser): HatAccessLog = {
    HatAccessLog(userAccessLogRow.date.toDateTime, user, userAccessLogRow.`type`,
      userAccessLogRow.scope, userAccessLogRow.applicationName, userAccessLogRow.applicationResource)
  }

  def fromDbModel(dataJsonRow: DataJsonRow): EndpointData = {
    EndpointData(dataJsonRow.source, Some(dataJsonRow.recordId), dataJsonRow.data, None)
  }

  def fromDbModel(dataJsonRow: DataJsonRow, linkedDataJsonRows: Seq[DataJsonRow]): EndpointData = {
    EndpointData(dataJsonRow.source, Some(dataJsonRow.recordId), dataJsonRow.data, Some(linkedDataJsonRows.map(fromDbModel)))
  }

  def fromDbModel(dataBundleRow: DataBundlesRow): EndpointDataBundle = {
    import RichDataJsonFormats.propertyQueryFormat
    EndpointDataBundle(dataBundleRow.bundleId, dataBundleRow.bundle.as[Map[String, PropertyQuery]])
  }

  def fromDbModel(dataDebitBundle: DataDebitBundleRow, bundle: DataBundlesRow): DebitBundle = {
    DebitBundle(dataDebitBundle.dateCreated, dataDebitBundle.startDate, dataDebitBundle.endDate,
      dataDebitBundle.rolling, dataDebitBundle.enabled,
      ModelTranslation.fromDbModel(bundle))
  }

  def fromDbModel(dataDebit: DataDebitContractRow, client: UserUserRow, dataDebitBundle: Seq[(DataDebitBundleRow, DataBundlesRow)]): RichDataDebit = {
    RichDataDebit(dataDebit.dataDebitKey, dataDebit.dateCreated,
      userFromDbModel(client), dataDebitBundle.map(d => ModelTranslation.fromDbModel(d._1, d._2)))
  }

  def userFromDbModel(user: UserUserRow): User = {
    fromInternalModel(fromDbModel(user))
  }

  @tailrec
  def groupRecords[T, U](list: Seq[(T, Option[U])], groups: Seq[(T, Seq[U])] = Seq())(implicit equalIdentity: ((T, T) => Boolean)): Seq[(T, Seq[U])] = {
    if (list.isEmpty) {
      groups
    }
    else {
      groupRecords(
        list.dropWhile(v => equalIdentity(v._1, list.head._1)),
        groups :+ ((list.head._1, list.takeWhile(v => equalIdentity(v._1, list.head._1)).unzip._2.flatten)))
    }
  }
}