package core.config

import java.security.SecureRandom

import jp.t2v.lab.play2.auth.{ AuthenticityToken, IdContainer }
import play.api.Play.current
import play.api.cache.Cache

import scala.annotation.tailrec
import scala.reflect.ClassTag
import scala.util.Random

// this is stateless cache implementation
class IdContainerImpl[Id: ClassTag] extends IdContainer[Id] {

  private val tokenSuffix = ":token"
  private val userIdSuffix = ":userId"
  private val random = new Random(new SecureRandom())

  override def startNewSession(userId: Id, timeoutInSeconds: Int): AuthenticityToken = {
    val token = generate
    store(token, userId, timeoutInSeconds)
    token
  }

  override def get(token: AuthenticityToken): Option[Id] = Cache.get(token + tokenSuffix).map(_.asInstanceOf[Id])

  override def remove(token: AuthenticityToken): Unit = {
    unsetToken(token)
  }

  override def prolongTimeout(token: AuthenticityToken, timeoutInSeconds: Int): Unit = {
    get(token).foreach(store(token, _, timeoutInSeconds))
  }

  private def unsetToken(token: AuthenticityToken) {
    Cache.remove(token + tokenSuffix)
  }

  @tailrec
  private final def generate: AuthenticityToken = {
    val table = "abcdefghijklmnopqrstuvwxyz1234567890"
    val token = Iterator.continually(random.nextInt(table.size)).map(table).take(64).mkString
    if (get(token).isDefined) generate else token
  }

  private def store(token: AuthenticityToken, userId: Id, timeoutInSeconds: Int) {
    Cache.set(token + tokenSuffix, userId, timeoutInSeconds)
  }
}
