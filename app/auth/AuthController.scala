package auth

import javax.inject.Inject

import auth.model._
import auth.services.AuthService
import core.config.AuthorityImpl
import core.model.ErrorWrapper
import core.{AppController, AppControllerWithOptionalUser}
import play.api.Play
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.BodyParsers

import scala.concurrent.Future

class AnonymousAuthController @Inject() (override val authService: AuthService) extends AppControllerWithOptionalUser {

  def login() = AsyncStack(BodyParsers.parse.json) { implicit request =>
    request.body.validate[LoginRequest] match {
      case s: JsSuccess[LoginRequest] =>
        authService.login(s.get.email, s.get.password).flatMap {
          case Right(user) =>
            idContainer.startNewSession(user.id, sessionTimeoutInSeconds).map { token =>
              Ok(Json.toJson(TokenResponse(token)))
            }
          case Left(error) => Future.successful(Unauthorized(Json.toJson(error)))
        }
      case e: JsError =>
        Future.successful(BadRequest(Json.toJson(ErrorWrapper("invalidJson", JsError.toJson(e).toString))))
    }
  }

  def register() = AsyncStack(BodyParsers.parse.json) { implicit request =>
    request.body.validate[SignupRequest] match {
      case s: JsSuccess[SignupRequest] =>
        authService.register(s.get.email, s.get.password, s.get.acceptTerms).map { user =>
          Ok("")
        }
      case e: JsError =>
        Future.successful(BadRequest(Json.toJson(ErrorWrapper("invalidJson", JsError.toJson(e).toString))))
    }
  }

  def confirmAccount(token: String) = AsyncStack() { implicit request =>
    authService.confirmAccount(token).map {
      case Some(user) => Ok("")
      case None => BadRequest(Json.toJson(ErrorWrapper("auth.confirmAccount.tokenNotFound", "Invalid confirmation token")))
    }
  }

  def facebook() = AsyncStack(BodyParsers.parse.json) { implicit request =>
    request.body.validate[FacebookRequest] match {
      case s: JsSuccess[FacebookRequest] =>
        authService.facebook(s.get.clientId, s.get.code, s.get.redirectUri).flatMap {
          case Right(user) =>
            idContainer.startNewSession(user.id, sessionTimeoutInSeconds).map { token =>
              Ok(Json.toJson(TokenResponse(token)))
            }
          case Left(error) => Future.successful(Unauthorized(Json.toJson(error)))
        }
      case e: JsError =>
        Future.successful(BadRequest(Json.toJson(ErrorWrapper("invalidJson", JsError.toJson(e).toString))))
    }
  }

  def resetPasswordRequest() = AsyncStack(BodyParsers.parse.json) { implicit request =>
    def url(confirmationToken: String) = Play.current.configuration.getString(path = "application.url").get + s"/auth/reset-password-confirmation?token=$confirmationToken"
    request.body.validate[ResetPasswordRequest] match {
      case s: JsSuccess[ResetPasswordRequest] =>
        authService.resetPasswordRequest(s.get.email).map { userOpt =>
          Ok("ok")
        }
      case e: JsError =>
        Future.successful(BadRequest(Json.toJson(ErrorWrapper("invalidJson", JsError.toJson(e).toString))))
    }
  }

  def resetPasswordConfirmation() = AsyncStack(BodyParsers.parse.json) { implicit request =>
    request.body.validate[ResetPasswordConfirmation] match {
      case s: JsSuccess[ResetPasswordConfirmation] =>
        authService.changePasswordByToken(s.get.token, s.get.newPassword).map {
          case Right(user) =>
            Ok("ok")
          case Left(error) =>
            BadRequest(Json.toJson(error))
        }
      case e: JsError =>
        Future.successful(BadRequest(Json.toJson(ErrorWrapper("invalidJson", JsError.toJson(e).toString))))
    }
  }

}

class AuthedAuthController @Inject() (override val authService: AuthService) extends AppController {

  def getAccount() = AsyncStack(AuthorityKey -> AuthorityImpl()) { implicit request =>
    authService.getUserById(loggedIn.id).map {
      case Some(user) => Ok(Json.toJson(AccountResponse.fromUser(user)))
      case None => BadRequest(Json.toJson(ErrorWrapper("internalError", "Internal server error occurred")))
    }
  }

  def deleteToken() = AsyncStack(AuthorityKey -> AuthorityImpl()) { implicit request =>
    val token = tokenAccessor.extract(request).get
    idContainer.remove(token).map { a =>
      Ok("")
    }
  }

}
