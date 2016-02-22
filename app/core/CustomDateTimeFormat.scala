package core

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._

object CustomDateTimeFormat {

  val dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  implicit val dateReads = new Reads[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsString(string) => JsSuccess(dtf.parseDateTime(string).withTimeAtStartOfDay())
      case _ => JsError("validate.error.invalidDate")
    }
  }

  implicit val dateWrites = new Writes[DateTime] {
    override def writes(date: DateTime): JsValue = JsString(dtf.print(date))
  }

}
