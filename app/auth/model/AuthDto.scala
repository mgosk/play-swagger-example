package auth.model

import play.api.libs.json.Json

case class LoginRequest(email: String, password: String)

object LoginRequest {

  implicit val loginRequestReads = Json.reads[LoginRequest]

}

case class SignupRequest(email: String, password: String, acceptTerms: Boolean)

object SignupRequest {

  implicit val loginRequestReads = Json.reads[SignupRequest]

}

case class AccountResponse(id: UserId,
  email: Option[String],
  isAdmin: Boolean = false,
  hasOrganizationData: Boolean)

object AccountResponse {

  implicit val accountResponseWrites = Json.writes[AccountResponse]

  def fromUser(user: User): AccountResponse = {
    AccountResponse(
      id = user.id,
      email = user.email,
      isAdmin = user.isAdmin,
      hasOrganizationData = user.hasOrganizationData)
  }

}

case class FacebookRequest(clientId: String, code: String, redirectUri: String)

object FacebookRequest {

  implicit val facebookRequestReads = Json.reads[FacebookRequest]

}

case class ResetPasswordRequest(email: String)

object ResetPasswordRequest {
  implicit val resetPasswordRequestReads = Json.reads[ResetPasswordRequest]
}

case class ResetPasswordConfirmation(token: String, newPassword: String)

object ResetPasswordConfirmation {

  implicit val resetPasswordConfirmationReads = Json.reads[ResetPasswordConfirmation]

}
