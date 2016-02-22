package core.config

import auth.model
import auth.model.UserId
import auth.repositories.UsersRepository
import auth.services.AuthService
import jp.t2v.lab.play2.auth.{AsyncIdContainer, AuthConfig}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._

trait AuthConfigImpl extends AuthConfig {

  val authService: AuthService

  type Id = UserId

  type User = model.User

  type Authority = AuthorityImpl

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds: Int = 2592000 //30days

  //FIXME
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = authService.getUserById(id).map { u => u }

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = Future.successful {
    Ok("ok")
  }

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = Future.successful {
    Ok("ok")
  }

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = Future.successful {
    Unauthorized("Unauthorized")
  }

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = Future.successful {
    Forbidden("Forbidden")
  }

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    user.state == authority.state &&
      (!authority.isAdmin || user.isAdmin)
  }

  override lazy val tokenAccessor = new TokenAccessorImpl

  override lazy val idContainer: AsyncIdContainer[Id] = AsyncIdContainer(new IdContainerImpl[Id])

}