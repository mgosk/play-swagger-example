package core.model

import com.pellucid.sealerate
import play.api.libs.json._

sealed abstract class State(val code: String)

object State {

  case object Active extends State("active")

  case object Archived extends State("archived")

  def values: Set[State] = sealerate.values[State]

  def apply(code: String): State = values.find(_.code == code).getOrElse(throw new RuntimeException(s"Can't construct State from: $code"))

  implicit val stateWrites = new Writes[State] {
    override def writes(o: State): JsValue = JsString(o.code)
  }

  implicit val stateReads = new Reads[State] {
    override def reads(json: JsValue): JsResult[State] = json match {
      case JsString(string) => JsSuccess(State(string))
      case _ => JsError("validate.error.invalidState")
    }
  }

}