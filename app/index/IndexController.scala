package index

import javax.inject.Inject

import com.typesafe.config.ConfigFactory
import init.Initializer
import play.api.Play
import play.api.mvc.{ Action, Controller }

class IndexController @Inject() (initializer: Initializer) extends Controller {
  val conf = ConfigFactory.load();
  val clientId = conf.getString("facebook.appId")

  def getIndex = Action { request =>
    Ok(index.html.index())
  }

  def other(url: String) = Action { request =>
    Ok(index.html.index())
  }

  def invalidApiCall(url: String) = Action { request =>
    NotFound("")
  }

}
