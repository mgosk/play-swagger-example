package init

import java.util.{ ArrayList, List }
import javax.inject.Inject

import auth.model._
import auth.repositories.UsersRepository
import it.innove.play.pdf.PdfGenerator
import org.joda.time.DateTime
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.{ ArrayList => JavaArrayList, List => JavaList }

class Initializer @Inject() (usersRepository: UsersRepository, pdfGenerator: PdfGenerator) {
  val fonts: JavaList[String] = new JavaArrayList()
  fonts.add("fonts/FreeSans.ttf")
  fonts.add("fonts/Tahoma.ttf")
  pdfGenerator.loadLocalFonts(fonts)

  //admin init
  usersRepository.findByEmail("admin@admin.com").map {
    case Some(user) => //do nothing
    case None =>
      val admin = UserEntity(_id = UserId.random,
        email = Some("admin@admin.com"),
        password = Some("XXXXX"),
        state = UserState.Active,
        isAdmin = true,
        createdAt = DateTime.now,
        hasOrganizationData = false,
        acceptTerms = true,
        newsletter = false)
      usersRepository.insert(admin).map { saved =>
        Logger.info("Admin account successfully created")
      }
  }

}
