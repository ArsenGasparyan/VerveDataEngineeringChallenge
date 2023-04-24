package verve.data.engineering.challenge.common

import org.apache.log4j.Logger

trait ApplicationLogger {
  @transient lazy val logger: Logger = Logger.getLogger(getClass.getName)
}
