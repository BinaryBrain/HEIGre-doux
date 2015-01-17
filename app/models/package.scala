import play.api.Play.current

import models._

import java.sql.Timestamp
import java.text.SimpleDateFormat
import play.api.libs.json._

package object models {
  val DB = play.api.db.slick.DB
  val mysql = scala.slick.driver.MySQLDriver.simple
  val sql = scala.slick.jdbc.StaticQuery

  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val menuJsonFormat = Json.format[Menu]
  implicit val menusAlimentJsonFormat = Json.format[MenusAliment]
  implicit val alimentJsonFormat = Json.format[Aliment]
  implicit val typeJsonFormat = Json.format[Type]
  implicit val nutrimentsAlimentJsonFormat = Json.format[NutrimentsAliment]
  implicit val nutrimentsNameJsonFormat = Json.format[NutrimentsName]
  implicit val nutrimentsValueJsonFormat = Json.format[NutrimentsValue]
}
