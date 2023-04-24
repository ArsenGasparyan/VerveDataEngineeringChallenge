package verve.data.engineering.challenge.job

import verve.data.engineering.challenge.common.ApplicationLogger
import verve.data.engineering.challenge.processor.DataProcessor

object MobileAdCampaign extends ApplicationLogger {
  def main(args: Array[String]): Unit = {
    logger.info("Starting data processing")
    try {
      val impressionsFile = args(0)
      val clicksFile = args(1)
      new DataProcessor(impressionsFile, clicksFile).process()
      logger.info("Data processing completed successfully")
    } catch {
      case e: Exception => logger.error("Error processing data: " + e.getMessage)
    }
  }
}