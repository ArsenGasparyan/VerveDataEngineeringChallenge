package verve.data.engineering.challenge.events

import org.apache.spark.sql.types.{StringType, StructField, StructType}
import play.api.libs.json._

case class ImpressionEvent(id: String, app_id: Int, country_code: String, advertiser_id: Int)

object ImpressionEvent {
  implicit val reads: Reads[ImpressionEvent] = Json.reads[ImpressionEvent]
  implicit val writes: Writes[ImpressionEvent] = Json.writes[ImpressionEvent]
  val impressionSchema: StructType = StructType(Array(
    StructField("app_id", StringType),
    StructField("advertiser_id", StringType),
    StructField("country_code", StringType),
    StructField("id", StringType)
  ))
}