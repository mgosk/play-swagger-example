package core.model

import play.api.libs.json.Json

case class ErrorWrapper(code: String, message: String)

object ErrorWrapper {

  implicit val errorWrapperWrites = Json.writes[ErrorWrapper]

}