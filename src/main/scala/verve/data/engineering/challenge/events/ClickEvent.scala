package verve.data.engineering.challenge.events

import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import play.api.libs.json._

case class ClickEvent(impression_id: String, revenue: Double)

object ClickEvent {
  implicit val reads: Reads[ClickEvent] = Json.reads[ClickEvent]
  implicit val writes: Writes[ClickEvent] = Json.writes[ClickEvent]
  val clicksSchema: StructType = StructType(Array(
    StructField("impression_id", StringType),
    StructField("revenue", DoubleType)
  ))
}
