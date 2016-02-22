package auth.model

import com.pellucid.sealerate
import play.api.libs.json._

sealed abstract class UserState(val value: String)

object UserState {

  case object New extends UserState("new")

  case object Active extends UserState("active")

  case object Blocked extends UserState("blocked")

  def values: Set[UserState] = sealerate.values[UserState]

  def apply(value: String): UserState = values.find(_.value == value).getOrElse(throw new RuntimeException(s"Can't construct Status from: $value"))

  implicit def statusWrites = new Writes[UserState] {
    override def writes(role: UserState): JsValue = JsString(role.value)
  }

  implicit def statusReads = new Reads[UserState] {
    override def reads(json: JsValue): JsResult[UserState] = json match {
      case JsString(string) => JsSuccess(UserState(string))
      case _ => JsError("validate.error.invalidStatus")
    }
  }
}