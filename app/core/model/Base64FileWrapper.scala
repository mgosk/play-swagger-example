package core.model

import play.api.libs.json.Json

case class Base64FileWrapper(content: String, filename: String)

object Base64FileWrapper {
  implicit val fileWrapperWrites = Json.writes[Base64FileWrapper]

}

case class RawFileWrapper(content: Array[Byte], filename: String)

