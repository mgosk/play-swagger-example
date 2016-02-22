package auth.model

import org.joda.time.DateTime
import play.api.libs.json.Json

case class UserEntity(_id: UserId,
  email: Option[String],
  password: Option[String],
  state: UserState,
  isAdmin: Boolean,
  facebookId: Option[String] = None,
  googleId: Option[String] = None,
  createdAt: DateTime,
  hasOrganizationData: Boolean, acceptTerms: Boolean,
  newsletter: Boolean)

object UserEntity {

  implicit val userEntityFormat = Json.format[UserEntity]

  implicit def entity(user: User): UserEntity = UserEntity(
    _id = user.id,
    email = user.email,
    password = user.password,
    state = user.state,
    isAdmin = user.isAdmin,
    facebookId = user.facebookId,
    googleId = user.googleId,
    createdAt = user.createdAt,
    hasOrganizationData = user.hasOrganizationData,
    acceptTerms = user.acceptTerms,
    newsletter = user.newsletter)
}

