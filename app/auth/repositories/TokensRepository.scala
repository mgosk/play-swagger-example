package auth.repositories

import auth.model.{TokenEntity, TokenKind}
import com.google.inject.{ImplementedBy, Singleton}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[TokensRepositoryImpl])
trait TokensRepository {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  def insert(token: TokenEntity): Future[TokenEntity]

  def getByToken(value: String, tokenKind: TokenKind): Future[Option[TokenEntity]]

  def delete(value: String): Future[Int]

}

@Singleton
class TokensRepositoryImpl extends TokensRepository {

  def collection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("authorize.tokens")

  override def insert(token: TokenEntity): Future[TokenEntity] =
    collection.insert(token).map { lastError =>
      token
    }

  override def getByToken(value: String, tokenKind: TokenKind): Future[Option[TokenEntity]] = {
    val selector = Json.obj("_id" -> value, "validTo" -> Json.obj("$gt" -> DateTime.now), "kind" -> tokenKind)
    val cursor = collection.find(selector).cursor[TokenEntity]()
    cursor.collect[Seq]().map { seq =>
      seq.headOption
    }
  }

  override def delete(value: String): Future[Int] = {
    val selector = Json.obj("_id" -> value)
    collection.remove(selector).map { writeResult =>
      writeResult.n
    }
  }

}
