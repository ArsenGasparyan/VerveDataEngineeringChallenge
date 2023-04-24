package verve.data.engineering.challenge.enums

sealed abstract class ImpressionEnum(val key: String)

object ImpressionEnum extends Enum[ImpressionEnum]{

  case object IMPRESSIONS extends ImpressionEnum("impressions")
  case object IMPRESSION_ID extends ImpressionEnum("id")
  case object IMPRESSION_APP_ID extends ImpressionEnum("app_id")
  case object IMPRESSION_COUNTRY_CODE extends ImpressionEnum("country_code")
  case object IMPRESSION_ADVERTISER_ID extends ImpressionEnum("advertiser_id")

}