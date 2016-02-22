package auth.model

import com.pellucid.sealerate
import play.api.libs.json._

sealed abstract class Role(val value: String)

object Role {

  case object Admin extends Role("admin")

  case object Artist extends Role("artist")

  case object Fan extends Role("fan")

  def values: Set[Role] = sealerate.values[Role]

  def apply(value: String): Role = values.find(_.value == value).getOrElse(throw new RuntimeException(s"Can't construct Role from: $value"))

  implicit def roleWrites = new Writes[Role] {
    override def writes(role: Role): JsValue = JsString(role.value)
  }

  implicit def roleReads = new Reads[Role] {
    override def reads(json: JsValue): JsResult[Role] = json match {
      case JsString(string) => JsSuccess(Role(string))
      case _ => JsError("validate.error.invalidRole")
    }
  }

}