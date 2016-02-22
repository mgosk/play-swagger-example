package auth.model

import java.math.BigInteger
import java.security.SecureRandom

import org.joda.time.DateTime
import play.api.libs.json.Json

case class Token(token: String, validTo: DateTime, userId: UserId, kind: TokenKind)

case class TokenEntity(_id: String, validTo: DateTime, userId: UserId, kind: TokenKind)

object Token {

  def passwordReset(userId: UserId) = Token(
    token = generateToken,
    validTo = DateTime.now.plusHours(6), //TODO get time offset from config
    userId = userId,
    kind = TokenKind.PasswordReset)

  def accountConfirmation(userId: UserId) = Token(
    token = generateToken,
    validTo = DateTime.now.plusHours(72), //TODO get time offset from config
    userId = userId,
    kind = TokenKind.AccountConfirmation)

  private val random = new SecureRandom()
  private def generateToken: String = {
    new BigInteger(255, random).toString(32)
  }

  implicit def fromEntity(token: TokenEntity) =
    Token(token = token._id,
      validTo = token.validTo,
      userId = token.userId,
      kind = token.kind)

  implicit def fromOption(option: Option[TokenEntity]): Option[Token] =
    option.map(fromEntity(_))

}

object TokenEntity {

  implicit  val tokenEntityFormat = Json.format[TokenEntity]

  implicit def fromEntity(token: Token) =
    TokenEntity(_id = token.token,
      validTo = token.validTo,
      userId = token.userId,
      kind = token.kind)

}