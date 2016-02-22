package auth.services

import java.util.UUID
import javax.inject.Inject

import auth.managers.{TokensManager, UsersManager}
import auth.model._
import com.restfb.types.{User => FbUser}
import com.restfb.{DefaultFacebookClient, Parameter}
import com.typesafe.config.ConfigFactory
import core.MailSender
import core.model.ErrorWrapper
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import play.api.Play.current
import play.api.libs.ws.WS
import play.api.{Logger, Play}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex
import scala.util.{Success, Try}

class AuthService @Inject()(mailSender: MailSender, usersManager: UsersManager, tokensManager: TokensManager) {

  def confirmAccountUrl(confirmationToken: String) = Play.current.configuration.getString(path = "application.url").get + s"/auth/confirm-account?token=$confirmationToken"
  val passwordResetUrl = Play.current.configuration.getString(path = "application.url").get + "/auth/reset-password"
  val conf = ConfigFactory.load();
  val clientId = conf.getString("facebook.appId")
  val clientSecret = conf.getString("facebook.secret")
  val accessTokenUrl = "https://graph.facebook.com/oauth/access_token"
  val graphApiUrl = "https://graph.facebook.com/me"

  def login(email: String, password: String): Future[Either[ErrorWrapper, User]] = {
    usersManager.authenticate(email, password).map {
      case Some(user) if user.state == UserState.Active =>
        Right(user)
      case Some(user) if user.state == UserState.New =>
        Left(ErrorWrapper("auth.login.inactive", "Confirm account before login"))
      case None =>
        Left(ErrorWrapper("auth.login.password-incorrect", "Your password is incorrect"))
    }
  }

  def register(email: String, password: String, acceptTerms: Boolean, newsletter: Boolean = false): Future[User] = {
    usersManager.getByEmail(email).flatMap {
      case Some(user) =>
        mailSender.sendEmailAsync("Rejestracja w XXX", user.email.get, auth.mailTemplates.html.signupExist.apply(passwordResetUrl).toString)
        Future.successful(user)
      case None =>
        val userId = UserId(UUID.randomUUID)
        val newUser = User(userId, Some(email), Some(BCrypt.hashpw(password, BCrypt.gensalt())),
          UserState.New, isAdmin = false, createdAt = DateTime.now, hasOrganizationData = false,
          acceptTerms = acceptTerms, newsletter = newsletter)
        usersManager.create(newUser).flatMap { user =>
          Logger.info(s"User saved in system with id ${userId}")
          val confirmationToken = Token.accountConfirmation(userId)
          tokensManager.create(confirmationToken).map { token =>
            mailSender.sendEmailAsync("Rejestracja w XXX", user.email.get, auth.mailTemplates.html.signupNew.apply(confirmAccountUrl(confirmationToken.token)).toString)
            user
          }
        }
    }
  }

  def confirmAccount(tokenValue: String): Future[Option[User]] = {
    tokensManager.find(tokenValue, TokenKind.AccountConfirmation) flatMap {
      case Some(token) =>
        tokensManager.delete(token.token).flatMap { num =>
          usersManager.getExisting(token.userId) flatMap { user =>
            usersManager.update(user.copy(state = UserState.Active)).flatMap { updated =>
              configureUser()(updated).flatMap { a =>
                tokensManager.delete(tokenValue).map { num =>
                  Some(updated)
                }
              }
            }
          }
        }
      case None =>
        Future.successful(None)
    }
  }

  def facebook(clientId: String, code: String, redirectUri: String): Future[Either[ErrorWrapper, User]] = {
    val xx =
      WS.url(accessTokenUrl)
        .withQueryString(
          "redirect_uri" -> redirectUri,
          "code" -> code,
          "client_id" -> clientId,
          "client_secret" -> clientSecret).get
    xx.flatMap { response =>
      val regex = new Regex("access_token=(.*)&expires=(.*)")
      response.body match {
        case regex(accessToken, expires) => {
          getFbUserDetails(accessToken) match {
            case Success(fbUser) =>
              println(fbUser)
              usersManager.getByFacebookId(fbUser.getId) flatMap {
                case Some(user) =>
                  Future.successful(Right(user))
                case None =>
                  val emailOpt = if (fbUser.getEmail != null) Some(fbUser.getEmail) else None
                  emailOpt match {
                    case Some(email) =>
                      usersManager.getByEmail(email).flatMap {
                        case Some(user) =>
                          usersManager.update(user.copy(facebookId = Some(fbUser.getId))).map { updated =>
                            Right(updated)
                          }
                        case None =>
                          createUser(fbUser, emailOpt).map { user => Right(user) }
                      }
                    case None =>
                      createUser(fbUser, emailOpt).map { user => Right(user) }
                  }
              }
            case _ => Future.successful(Left(ErrorWrapper("externalError", "Błąd usługi zawnętrznej")))
          }
        }
      }
    }
  }

  private def createUser(fbUser: FbUser, emailOpt: Option[String]): Future[User] = {
    val newUser = User(UserId(UUID.randomUUID), emailOpt, None, UserState.Active, isAdmin = false,
      facebookId = Some(fbUser.getId), createdAt = DateTime.now, hasOrganizationData = false, acceptTerms = false, newsletter = false)
    usersManager.create(newUser).flatMap { user =>
      configureUser()(newUser).map { x =>
        newUser
      }
    }
  }

  def resetPasswordRequest(email: String): Future[Option[User]] = {
    def url(confirmationToken: String) = Play.current.configuration.getString(path = "application.url").get + s"/auth/reset-password-confirmation?token=$confirmationToken"
    usersManager.getByEmail(email).flatMap {
      case Some(user) =>
        val passwordResetToken = Token.passwordReset(user.id)
        tokensManager.create(passwordResetToken).map { token =>
          mailSender.sendEmailAsync("Reset hasła w systemie XXX", user.email.get, auth.mailTemplates.html.resetPassword.apply(url(token.token)).toString)
          Some(user)
        }
      case None =>
        Future.successful(None)
    }
  }

  def changePasswordByToken(token: String, newPassword: String): Future[Either[ErrorWrapper, User]] = {
    tokensManager.find(token, TokenKind.PasswordReset) flatMap {
      case Some(token) =>
        tokensManager.delete(token.token).flatMap { num =>
          usersManager.getExisting(token.userId) flatMap { user =>
            usersManager.update(user.updatePassword(newPassword)).map { updated =>
              Right(updated)
            }
          }
        }
      case None =>
        Future.successful(Left(ErrorWrapper("notFound", "Token nie znaleziony")))
    }
  }

  def getUserById(id: UserId) = usersManager.get(id)

  def getAllUser() = usersManager.getAll()

  private def configureUser()(implicit user: User): Future[User] = Future.successful(user)

  private def getFbUserDetails(accessToken: String): Try[FbUser] = {
    Try {
      val client = new DefaultFacebookClient(accessToken, clientSecret)
      client.fetchObject("me", classOf[FbUser],Parameter.`with`("fields","email,first_name,last_name"))
    }
  }

}
