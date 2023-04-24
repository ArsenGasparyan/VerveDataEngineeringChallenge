package verve.data.engineering.challenge.utils

import org.apache.spark.sql.SparkSession
import verve.data.engineering.challenge.common.ApplicationLogger

object SparkUtils extends ApplicationLogger{

  def createSparkSession(appName: String, master:String): SparkSession = {
    logger.info(s"Creating Spark session with name '$appName' and master '$master'")
    SparkSession.builder()
      .appName(appName)
      .master(master)
      .getOrCreate()
  }

  def stopSparkSession(spark: SparkSession): Unit = {
    logger.info("Stopping Spark session")
    spark.stop()
  }

}

