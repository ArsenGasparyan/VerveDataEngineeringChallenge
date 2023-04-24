package verve.data.engineering.challenge.processor

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import play.api.libs.json.{JsObject, JsValue, Json}
import verve.data.engineering.challenge.common.ApplicationLogger
import verve.data.engineering.challenge.enums.ClicksEnum._
import verve.data.engineering.challenge.enums.ImpressionEnum._
import verve.data.engineering.challenge.enums.SparkEnum.{SPARK_APP_NAME, SPARK_CONFIG, SPARK_MASTER}
import verve.data.engineering.challenge.events.ClickEvent.clicksSchema
import verve.data.engineering.challenge.events.ImpressionEvent.impressionSchema
import verve.data.engineering.challenge.events.{ClickEvent, ImpressionEvent}
import verve.data.engineering.challenge.utils.ApplicationUtils._
import verve.data.engineering.challenge.utils.SparkUtils

class DataProcessor(impressionsFile: String, clicksFile: String) extends ApplicationLogger {

  def process(): Unit = {

    val outputFilePaths = ConfigFactory.load().getConfig("output_file_path")

    val metrics = calculateMetrics()
    saveFileToPath(outputFilePaths.getString("calculated_metrics_file_path"), metrics.toString())

    val sparkConfig = ConfigFactory.load().getConfig(SPARK_CONFIG.key)
    val spark = SparkUtils.createSparkSession(sparkConfig.getString(SPARK_APP_NAME.key), sparkConfig.getString(SPARK_MASTER.key))
    val recommended_advertiser_ids = recommendedAdvertisers(spark)
    saveFileToPath(outputFilePaths.getString("recommended_advertiser_ids_file_path"), recommended_advertiser_ids.toString())
    SparkUtils.stopSparkSession(spark)

  }

  def calculateMetrics(): JsValue = {
    logger.info("Calculating metrics")
    val impressions = parseJson[ImpressionEvent](readFile(impressionsFile)).filter(_.id.nonEmpty).filter(_.country_code.nonEmpty)
    val clicks = parseJson[ClickEvent](readFile(clicksFile)).filter(_.impression_id.nonEmpty)

    val metrics = impressions
      .filter(_.country_code.nonEmpty)
      .groupBy(impression => (impression.app_id, impression.country_code))
      .map { case ((app_id, country_code), impressions) => val clicksForAppCountry = clicks.filter(click => impressions.exists(_.id == click.impression_id))
      val impressionCount = impressions.length
      val clickCount = clicksForAppCountry.length
      val revenueSum = clicksForAppCountry.map(_.revenue).sum
      Json.obj(
        IMPRESSION_APP_ID.key -> app_id,
        IMPRESSION_COUNTRY_CODE.key -> country_code,
        IMPRESSIONS.key -> impressionCount,
        CLICKS.key -> clickCount,
        CLICKS_REVENUE.key -> revenueSum)
    }.toList

    Json.toJson(metrics)
  }

  def recommendedAdvertisers(spark:SparkSession): JsValue = {
    logger.info("Calculating recommended advertisers")
    val clicksDF = spark.read.schema(clicksSchema)
      .option("multiline", "true")
      .json(clicksFile)
      .where(col(CLICKS_IMPRESSION_ID.key).isNotNull && col(CLICKS_REVENUE.key).isNotNull)

    val impressionDF = spark.read.schema(impressionSchema)
      .option("multiline", "true")
      .json(impressionsFile)
      .where(col(IMPRESSION_APP_ID.key).isNotNull && col(IMPRESSION_ADVERTISER_ID.key).isNotNull && col(IMPRESSION_ID.key).isNotNull && col(IMPRESSION_COUNTRY_CODE.key).isNotNull && col(IMPRESSION_COUNTRY_CODE.key) =!= "")

    val joinedDF = clicksDF.join(impressionDF, clicksDF(CLICKS_IMPRESSION_ID.key) === impressionDF(IMPRESSION_ID.key), "inner")

    val rateDF = joinedDF
      .groupBy(IMPRESSION_APP_ID.key, IMPRESSION_COUNTRY_CODE.key, IMPRESSION_ADVERTISER_ID.key)
      .agg((sum(CLICKS_REVENUE.key) / count(CLICKS_IMPRESSION_ID.key)).alias("rate"))

    val recDF = rateDF
      .withColumn("rn", row_number.over(Window.partitionBy(IMPRESSION_APP_ID.key, IMPRESSION_COUNTRY_CODE.key).orderBy(desc("rate"))))
      .where(col("rn") <= 5)
      .groupBy(IMPRESSION_APP_ID.key, IMPRESSION_COUNTRY_CODE.key)
      .agg(collect_list(IMPRESSION_ADVERTISER_ID.key) as "recommended_advertiser_ids")

    val objects: Array[JsObject] = recDF.collect().map(row => {
      val app_id = row.getAs[String](IMPRESSION_APP_ID.key).toInt
      val country_code = row.getAs[String](IMPRESSION_COUNTRY_CODE.key)
      val recommended_advertiser_ids = row.getAs[Seq[String]]("recommended_advertiser_ids").map(_.toInt)
      Json.obj(
        IMPRESSION_APP_ID.key -> app_id,
        IMPRESSION_COUNTRY_CODE.key -> country_code,
        "recommended_advertiser_ids" -> recommended_advertiser_ids
      )
    })
    Json.toJson(objects)
  }
}