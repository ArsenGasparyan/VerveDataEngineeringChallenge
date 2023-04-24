package verve.data.engineering.challenge.utils

import better.files.File

import scala.util.{Failure, Success, Try}
import play.api.libs.json.{JsArray, JsError, JsSuccess, Json, Reads}
import verve.data.engineering.challenge.common.ApplicationLogger

object ApplicationUtils extends ApplicationLogger{

  def readFile(fileName: String): String = {
    File(fileName).contentAsString
  }

  def parseJson[A: Reads](jsonString: String): List[A] = {

    val jsonArray = Json.parse(jsonString).as[JsArray]

    jsonArray.value.flatMap { json =>
      json.validate[A] match {
        case JsSuccess(obj, _) => Some(obj)
        case JsError(errors) => None
      }
    }.toList

  }

  def saveFileToPath(path: String, file: String): Unit = {
    Try {
      File(path).write(file)
    } match {
      case Success(_) =>
        logger.info(s"File successfully saved to $path")
      case Failure(exception) =>
        logger.error(s"An unexpected error occurred while saving the file to $path: ${exception.getMessage}")
    }
  }
}
