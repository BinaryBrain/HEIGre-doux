package controllers

import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import java.time._
import java.sql.Timestamp

import scala.collection.JavaConversions._
import scala.slick.jdbc.StaticQuery.interpolation
import scala.collection.JavaConversions._

import play.api.libs.json._
import play.api.mvc._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._

import models._
import menusDownloader._

object Application extends Controller {
  def admin = Action {
    Ok(views.html.index())
  }

  def phonegap = Action {
    val content = new String(Files.readAllBytes(Paths.get("phonegap/www/index.html")))
    Ok(content).as(HTML)
  }

  def getMenus = Action {
    val properties = new Properties()
    val path = "conf/config.properties"
    val inputStream = new FileInputStream(path)
    properties.load(inputStream)
 
    val url = properties.getProperty("url")
    val user = properties.getProperty("user")
    val password = properties.getProperty("password")
    val domain = properties.getProperty("domain")
    val menusDir = properties.getProperty("directory")

    val today: LocalDate = LocalDate.now
    val shift = today.getDayOfWeek.getValue - 1
    val monday = today.toEpochDay - shift

    val menuDL = new MenuDownloader(user, password, url, domain)

    val file = menusDir + "/menu-" + today.minusDays(shift) + ".docx"

    DB.withSession { implicit session =>
      if (models.Menus.filter(_.date === new Timestamp(monday * TimeConstant.MS_PER_DAY)).exists.run) {
        Ok("Nothing done.")
      } else {
        menuDL.downloadDocx(file, 0) // Orangeraie
        // menuDL.downloadDocx(menusDir + "/menu1.docx", 1) // Palmeraie

        val menus = MenuParser.parseMenusDocx(file)

        val menusByDay = menus.transpose.map(m => m.toList).toList

        menusByDay.zipWithIndex foreach {
          case (dailyMenus, index) =>
            val date = new Timestamp((monday + index) * TimeConstant.MS_PER_DAY)

            dailyMenus foreach {
              m => Menus.add(date, m)
            }
        }

        Ok("OK")
      }
    }
  }

  def get(file: String) = Action {
    val content = new String(Files.readAllBytes(Paths.get("phonegap/www/" + file)))
    Ok(content).as(HTML)
  }

  def getTodayMenu: Action[AnyContent] = Action {
    val today: LocalDate = LocalDate.now
    Ok(getMenu(new Timestamp(today.toEpochDay * TimeConstant.MS_PER_DAY)))
  }

  def getMenuByDate(dateStr: String): Action[AnyContent] = Action {
    val date: LocalDate = LocalDate.parse(dateStr)
    Ok(getMenu(new Timestamp(date.toEpochDay * TimeConstant.MS_PER_DAY)))
  }

  def getMenuRange(startStr: String, endStr: String) = Action {
    val startDate: LocalDate = LocalDate.parse(startStr)
    val endDate: LocalDate = LocalDate.parse(endStr)

    val start = new Timestamp(startDate.toEpochDay * TimeConstant.MS_PER_DAY)
    val end = new Timestamp(endDate.toEpochDay * TimeConstant.MS_PER_DAY)

    Ok(getMenu(start, Option(end)))
  }

  def getMenuById(id: Int) = TODO

  def getMenu(start: Timestamp, end: Option[Timestamp] = None): JsValue = {
    DB.withSession { implicit session =>
      val menusAliments = Menus.get(start, end)
      val menusMap = menusAliments.groupBy(_._1)

      val menus = menusMap map {
        menu => (menu._1, menu._2.map {
          seq => (seq._2, seq._3, seq._4)
        })
      }

      val json = menus map {
        menu => Json.obj(
          "id" -> menu._1.id,
          "date" -> menu._1.date,
          "aliments" -> menu._2.map {
            t => Json.obj(
              "id" -> t._2.id,
              "name" -> t._1.name,
              "occurrence" -> t._2.occurrence,
              "last" -> t._2.last,
              "type" -> t._3
            )
          }
        )
      }

      Json.obj("menus" -> json)
    }
  }
}

object TimeConstant {
  final val MS_PER_DAY = 24*60*60*1000
}
