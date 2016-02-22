package core.config

import jp.t2v.lab.play2.auth._
import play.api.mvc.{RequestHeader, Result}

class TokenAccessorImpl extends TokenAccessor {
  val regex = """Bearer ([A-Za-z0-9]+)""".r //TODO check sign range
  override def extract(request: RequestHeader): Option[AuthenticityToken] = {
    request.headers.get("Authorization").map {
      case regex(token) => token
    }
  }

  // token isn't modified on each request
  override def put(token: AuthenticityToken)(result: Result)(implicit request: RequestHeader): Result =
    result

  override def delete(result: Result)(implicit request: RequestHeader): Result = ???
}
