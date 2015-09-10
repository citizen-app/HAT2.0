package dalapi.models

import dal.Tables._
import dalapi.models.ComparisonOperators.ComparisonOperator
import org.joda.time.LocalDateTime


object ComparisonOperators {
  sealed trait ComparisonOperator
  case object equal extends ComparisonOperator
  case object notEqual extends ComparisonOperator
  case object greaterThan extends ComparisonOperator
  case object lessThan extends ComparisonOperator
  case object like extends ComparisonOperator
  case object dateGreaterThan extends ComparisonOperator
  case object dateLessThan extends ComparisonOperator
  case object dateWeekdayGreaterThan extends ComparisonOperator
  case object dateWeekdayLessThan extends ComparisonOperator
  case object dateHourGreaterThan extends ComparisonOperator
  case object dateHourLessThan extends ComparisonOperator

  def fromString(value: String): ComparisonOperator = {
    Vector(equal, notEqual, greaterThan, lessThan, like,
      dateGreaterThan, dateLessThan,
      dateWeekdayGreaterThan, dateWeekdayLessThan,
      dateHourGreaterThan, dateHourLessThan).find(_.toString == value).get
  }

  val comparisonOperators: Set[ComparisonOperator] = Set(equal, notEqual, greaterThan, lessThan, like, dateGreaterThan,
    dateLessThan, dateWeekdayGreaterThan, dateWeekdayLessThan, dateHourGreaterThan, dateHourLessThan)
}

//object ComparisonOperator extends Enumeration {
//  type ComparisonOperator = Value
//  val equal, notEqual, greaterThan, lessThan, like,
//    dateGreaterThan, dateLessThan,
//    dateWeekdayGreaterThan, dateWeekdayLessThan,
//    dateHourGreaterThan, dateHourLessThan = Value
//}



case class ApiBundleTableCondition(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    field: ApiDataField,
    value: String,
    operator: ComparisonOperator)

object ApiBundleTableCondition {
  def fromBundleTableSliceCondition(condition: BundleTablesliceconditionRow)(field: ApiDataField) : ApiBundleTableCondition = {
    new ApiBundleTableCondition(Some(condition.id),
      Some(condition.dateCreated), Some(condition.lastUpdated),
      field, condition.value,
      ComparisonOperators.fromString(condition.operator))
  }
}

case class ApiBundleTableSlice(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    table: ApiDataTable,
    conditions: Seq[ApiBundleTableCondition])

object ApiBundleTableSlice {
  def fromBundleTableSlice(slice: BundleTablesliceRow)(table: ApiDataTable) : ApiBundleTableSlice = {
    new ApiBundleTableSlice(Some(slice.id),
      Some(slice.dateCreated), Some(slice.lastUpdated),
      table, Seq())
  }

  def fromBundleTableSliceConditions(slice: BundleTablesliceRow)(table: ApiDataTable)(conditions: Seq[ApiBundleTableCondition]) : ApiBundleTableSlice = {
    new ApiBundleTableSlice(Some(slice.id),
      Some(slice.dateCreated), Some(slice.lastUpdated),
      table, conditions)
  }
}


case class ApiBundleTable(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    name: String,
    table: ApiDataTable,
    slices: Option[Seq[ApiBundleTableSlice]])

object ApiBundleTable {
  def fromBundleTable(bundleTable: BundleTableRow)(table: ApiDataTable) : ApiBundleTable = {
    new ApiBundleTable(Some(bundleTable.id),
      Some(bundleTable.dateCreated), Some(bundleTable.lastUpdated),
      bundleTable.name, table, None)
  }
}

/*
  EXAMPLE:
  combination1: {
    name: "Weekend events at home",
    bundleTable: {
      id: 1,
      name: "Weekend events at home"
    },
  }

  EXAMPLE:
  combination2: {
    name: "Electricity in the kitchen",
    bundleTable: {
      id: 2,
      name: "Electricity in the kitchen"
    },
    bundleJoinField: {
      id: 2,
      tableId: 1,
      name: "startTime"
    },
    bundleTableField: {
      id: 3,
      tableId: 3,
      name: "timestamp"
    },
    operator: "likeTime"
  }
 */

case class ApiBundleCombination(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    name: String,
    bundleTable: ApiBundleTable,
    bundleJoinField: Option[ApiDataField],
    bundleTableField: Option[ApiDataField],
    operator: Option[ComparisonOperator])


/*
  EXAMPLE:
  bundle: {
    name: "Kitchen electricity on weekend parties",
    tables: [
      combination1,
      combination2
    ]
  }
 */
case class ApiBundleContextless(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    name: String,
    tables: Seq[ApiBundleCombination])