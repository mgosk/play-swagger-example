package auth.model

import com.pellucid.sealerate
import play.api.libs.json._

sealed abstract class TokenKind(val value: String)

object TokenKind {

  case object PasswordReset extends TokenKind("passwordReset")
  case object AccountConfirmation extends TokenKind("accountConfirmation")

  def values: Set[TokenKind] = sealerate.values[TokenKind]

  def apply(value: String): TokenKind = values.find(_.value == value).getOrElse(throw new RuntimeException(s"Can't construct TokenKind from: $value"))

  implicit def roleWrites = new Writes[TokenKind] {
    override def writes(o: TokenKind): JsValue = JsString(o.value)
  }

  implicit def roleReads = new Reads[TokenKind] {
    override def reads(json: JsValue): JsResult[TokenKind] = json match {
      case JsString(string) => JsSuccess(TokenKind(string))
      case _ => JsError("validate.error.invalidTokenKind")
    }
  }

}