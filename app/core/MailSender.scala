package core

import javax.inject.Inject

import akka.event.slf4j.Logger
import play.api.libs.mailer._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MailSender @Inject()(mailerClient: MailerClient) {

  //sends email asynchronously
  def sendEmailAsync(title: String, to: String, content: String): Future[String] = {
    val email = Email(
      title,
      "Zespół XXXX <mailing@XXX.pl>",
      Seq(to),
      bodyHtml = Some(content))
    Logger("mail").error(s"sending mail to: $to about:$title")
    Future.successful {
      mailerClient.send(email)
    }
  }
}