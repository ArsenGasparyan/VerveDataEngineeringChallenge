package verve.data.engineering.challenge.enums

sealed abstract class SparkEnum(val key: String)

object SparkEnum extends Enum[SparkEnum] {

  case object SPARK_CONFIG extends SparkEnum("spark")
  case object SPARK_APP_NAME extends SparkEnum("appName")
  case object SPARK_MASTER extends SparkEnum("master")

}