package auth.model

import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

case class User(id: UserId,
  email: Option[String],
  password: Option[String],
  state: UserState,
  isAdmin: Boolean = false,
  facebookId: Option[String] = None,
  googleId: Option[String] = None,
  createdAt: DateTime,
  hasOrganizationData: Boolean,
  acceptTerms: Boolean,
  newsletter: Boolean) {

  def updatePassword(newPassword: String): User = {
    this.copy(password = Some(BCrypt.hashpw(newPassword, BCrypt.gensalt())))
  }

}

object User {
  implicit def fromEntity(user: UserEntity): User = User(
    id = user._id,
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

  implicit def fromOption(user: Option[UserEntity]): Option[User] = user.map(entity => fromEntity(entity))

  implicit def fromSeq(user: Seq[UserEntity]): Seq[User] = user.map(entity => fromEntity(entity))

}
