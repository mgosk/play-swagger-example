package auth.managers

import javax.inject.Inject

import auth.model.{ TokenKind, Token }
import auth.repositories.TokensRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TokensManager @Inject() (tokensRepository: TokensRepository) {

  def create(token: Token): Future[Token] =
    tokensRepository.insert(token).map { t => t }

  def find(token: String, tokenKind: TokenKind): Future[Option[Token]] =
    tokensRepository.getByToken(token, tokenKind).map { t => t }

  def delete(value: String): Future[Int] =
    tokensRepository.delete(value)

}
