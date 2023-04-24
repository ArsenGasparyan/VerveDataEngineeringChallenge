package verve.data.engineering.challenge.enums

sealed abstract class ClicksEnum(val key: String)

object ClicksEnum extends Enum[ClicksEnum]{

  case object CLICKS extends ClicksEnum("clicks")
  case object CLICKS_IMPRESSION_ID extends ClicksEnum("impression_id")
  case object CLICKS_REVENUE extends ClicksEnum("revenue")

}