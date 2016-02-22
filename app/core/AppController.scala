package core

import core.config.AuthConfigImpl
import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

abstract class AppController extends Controller with AuthConfigImpl with AuthElement {
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

}

abstract class AppControllerWithOptionalUser extends Controller with AuthConfigImpl with OptionalAuthElement {
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

}