package auth.repositories

import auth.model.{ User, UserEntity, UserId }
import com.google.inject.{ ImplementedBy, Singleton }
import org.mindrot.jbcrypt.BCrypt
import play.api.Play.current
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[UsersRepositoryImpl])
trait UsersRepository {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  def insert(user: UserEntity): Future[UserEntity]

  def update(user: UserEntity): Future[UserEntity]

  def get(id: UserId): Future[Option[UserEntity]]

  def getExisting(id: UserId): Future[UserEntity]

  def getAll(): Future[Seq[UserEntity]]

  def findByEmail(email: String): Future[Option[UserEntity]]

  def findByFacebookId(facebookId: String): Future[Option[UserEntity]]

  def getByConfirmationToken(token: String): Future[Option[UserEntity]]

  def authenticate(email: String, password: String): Future[Option[UserEntity]]

}

@Singleton
class UsersRepositoryImpl extends UsersRepository {

  def collection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("authorize.users")
  private def idSelector(id: UserId) = Json.obj("_id" -> id)

  override def insert(user: UserEntity): Future[UserEntity] =
    collection.insert(user).map { lastError =>
      user
    }

  override def update(user: UserEntity): Future[UserEntity] = {
    val selector = Json.obj("_id" -> user._id)
    collection.update(selector, user).map { lastError =>
      user
    }
  }

  override def get(id: UserId): Future[Option[UserEntity]] = {
    val cursor: Cursor[UserEntity] = collection.find(idSelector(id)).cursor[UserEntity]()
    cursor.collect[Seq]().map(seq =>
      seq.headOption)
  }

  override def getExisting(id: UserId): Future[UserEntity] = {
    val cursor: Cursor[UserEntity] = collection.find(idSelector(id)).cursor[UserEntity]()
    cursor.collect[Seq]().map { seq =>
      seq.headOption.getOrElse(throw new RuntimeException(s"User with id:${id.id} not Found"))
    }
  }

  override def getAll(): Future[Seq[UserEntity]] = {
    val cursor: Cursor[UserEntity] = collection.find(Json.obj()).sort(Json.obj()).cursor[UserEntity]()
    cursor.collect[Seq]()
  }

  def findByEmail(email: String): Future[Option[UserEntity]] = {
    val cursor: Cursor[UserEntity] = collection.find(Json.obj("email" -> email)).sort(Json.obj()).cursor[UserEntity]()
    cursor.collect[Seq]().map(seq =>
      seq.headOption)
  }

  def findByFacebookId(facebookId: String): Future[Option[UserEntity]] = {
    val cursor: Cursor[UserEntity] = collection.find(Json.obj("facebookId" -> facebookId)).sort(Json.obj()).cursor[UserEntity]()
    cursor.collect[Seq]().map(seq =>
      seq.headOption)
  }

  def getByConfirmationToken(token: String): Future[Option[UserEntity]] = {
    val cursor: Cursor[UserEntity] = collection.find(Json.obj("confirmationToken" -> token)).sort(Json.obj()).cursor[UserEntity]()
    cursor.collect[Seq]().map(seq =>
      seq.headOption)
  }

  def authenticate(email: String, password: String): Future[Option[UserEntity]] = {
    findActiveByEmail(email).map(userOpt => userOpt.filter(user => user.password.map { pass => BCrypt.checkpw(password, pass) }.getOrElse(false)))
  }

  private def findActiveByEmail(login: String): Future[Option[UserEntity]] = {
    //FIXME add isActive filter
    val cursor: Cursor[UserEntity] = collection.find(Json.obj("email" -> login)).sort(Json.obj()).cursor[UserEntity]()
    cursor.collect[Seq]().map(seq =>
      seq.headOption)
  }

}
