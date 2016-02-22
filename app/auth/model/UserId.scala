package auth.model

import java.util.UUID

import play.api.libs.json._
import play.api.mvc.PathBindable


case class UserId(id: UUID) extends AnyVal

object UserId {
  implicit def userIdBindable(implicit uuidBinder: PathBindable[UUID]) = new PathBindable[UserId] {
    override def bind(key: String, value: String): Either[String, UserId] = {
      for {
        id <- uuidBinder.bind(key, value).right
      } yield UserId(id)
    }

    override def unbind(key: String, userId: UserId): String = ???
  }

  def random = UserId(UUID.randomUUID)

  implicit val userIdWrites = new Writes[UserId] {
    override def writes(id: UserId): JsValue = JsString(id.id.toString)
  }

  implicit val userIdReads = new Reads[UserId] {
    override def reads(json: JsValue): JsResult[UserId] = json match {
      case JsString(string) => JsSuccess(UserId(UUID.fromString(string)))
      case _ => JsError("validate.error.invalidUserId")
    }
  }
}