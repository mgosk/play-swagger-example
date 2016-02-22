package auth.model

import play.api.libs.json.Json

case class TokenResponse(token: String)

object TokenResponse {

  implicit val tokenWrites = Json.writes[TokenResponse]

}