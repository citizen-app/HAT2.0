import dal.Tables._
//import Tables._
//import Tables.profile.simple._
import autodal.SlickPostgresDriver.simple._
import org.joda.time.LocalDateTime
import org.specs2.mutable.Specification
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global

class ModelSpecThings extends Specification {
  val db = Database.forConfig("devdb")
  implicit val session: Session = db.createSession()

  "Core Tables" should {
    "be created" in {

      val getTables = MTable.getTables(None, Some("public"), None, None).map { ts =>
        ts.map { t =>
          t.name.name
        }
      }

      val requiredTables: Seq[String] = Seq(
        "data_table",
        "things_thing",
        "events_event",
        "people_person",
        "locations_location",
        "data_field",
        "system_properties"
      )

      val tables = db.run(getTables)
      tables must containAllOf[String](requiredTables).await
    }
  }

  sequential

  "Things tables" should {
    "be empty" in {
      val result = ThingsThing.run
      result must have size(0)
    }
    "accept data" in {
      
      val thingsthingRow = new ThingsThingRow(1, LocalDateTime.now(), LocalDateTime.now(), "Test")
      val thingId = (ThingsThing returning ThingsThing.map(_.id)) += thingsthingRow

      val relationshiptype = Some("Relationship description")

      val thingsthingtothingcrossrefRow = new ThingsThingtothingcrossrefRow(1, LocalDateTime.now(), LocalDateTime.now(), 1, 1, relationshiptype)
      val thingsthingtothingcrossrefId = (ThingsThingtothingcrossref returning ThingsThingtothingcrossref.map(_.id)) += thingsthingtothingcrossrefRow

      val description = Some("An example SystemUnitofmeasurement")

      val thingssystempropertystaticcrossrefRow = new ThingsSystempropertystaticcrossrefRow(1, LocalDateTime.now(), LocalDateTime.now(), 1, 2, 1, 1, relationshiptype, true)
      val thingssystempropertystaticcrossrefId = (ThingsSystempropertystaticcrossref returning ThingsSystempropertystaticcrossref.map(_.id)) += thingssystempropertystaticcrossrefRow

      val thingssystempropertydynamiccrossrefRow = new ThingsSystempropertydynamiccrossrefRow(1, LocalDateTime.now(), LocalDateTime.now(), 1, 2, 1, relationshiptype, true)
      val thingssystempropertydynamiccrossrefId = (ThingsSystempropertydynamiccrossref returning ThingsSystempropertydynamiccrossref.map(_.id)) +=  thingssystempropertydynamiccrossrefRow

      ThingsThing += thingsthingRow

      val result = ThingsThing.run
      result must have size(1)
    }
    "allow data to be removed" in {
      ThingsThing.delete
      ThingsThing.run must have size(0)

      ThingsThingtothingcrossref.delete
      ThingsThingtothingcrossref.run must have size(0)

      ThingsSystempropertydynamiccrossref.delete
      ThingsSystempropertydynamiccrossref.run must have size(0)

      ThingsSystempropertystaticcrossref.delete
      ThingsSystempropertystaticcrossref.run must have size(0)

    }
  }

  "Facebook things structures" should {
    
    "have things created" in {
      val localdatetime = Some(LocalDateTime.now())
      
      val thingsthingRows = Seq(
        new ThingsThingRow(1, LocalDateTime.now(), LocalDateTime.now(), "Cover")
        )

      ThingsThing ++= thingsthingRows

      val result = ThingsThing.run
      result must have size(1)
    }

    "have thingssystempropertystaticcrossref created" in {
      val relationshipdescription = Some("Property Cross Reference for a Facebook Cover")

      val findthingId = ThingsThing.filter(_.name === "Cover").map(_.id).run.head
      val findpropertyId = SystemProperty.filter(_.name === "cover").map(_.id).run.head
      val findfieldId = DataField.filter(_.name === "cover").map(_.id).run.head
      val findrecordId = DataRecord.filter(_.name === "cover").map(_.id).run.head

      val thingssystempropertystaticcrossrefRows = Seq(
        new ThingsSystempropertystaticcrossrefRow(0, LocalDateTime.now(), LocalDateTime.now(), findthingId, findpropertyId, findfieldId, findrecordId, relationshipdescription, true)
      )
      
      ThingsSystempropertystaticcrossref ++= thingssystempropertystaticcrossrefRows

      val result = ThingsSystempropertystaticcrossref.run
      result must have size(1)
    }
  }
}