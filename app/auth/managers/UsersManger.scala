package auth.managers

import javax.inject.Inject

import auth.model.{ User, UserId }
import auth.repositories.UsersRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UsersManager @Inject() (usersRepository: UsersRepository) {

  def create(user: User): Future[User] =
    usersRepository.insert(user).map(u => u)

  def update(user: User): Future[User] =
    usersRepository.update(user).map(u => u)

  def get(userId: UserId): Future[Option[User]] =
    usersRepository.get(userId).map(u => u)

  def getExisting(userId: UserId): Future[User] =
    usersRepository.getExisting(userId).map(u => u)

  def getByEmail(email: String): Future[Option[User]] =
    usersRepository.findByEmail(email).map(u => u)

  def getByFacebookId(fbId: String): Future[Option[User]] =
    usersRepository.findByFacebookId(fbId).map(u => u)

  def getAll(): Future[Seq[User]] =
    usersRepository.getAll().map(u => u)

  def authenticate(email: String, password: String): Future[Option[User]] =
    usersRepository.authenticate(email, password).map(u => u)

}
