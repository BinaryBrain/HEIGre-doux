package controllers

import java.io.{File, FileInputStream}
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import java.time._
import java.sql.Timestamp
import javax.activation.MimetypesFileTypeMap

import play.api.http.MimeTypes

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
  // Admin view
  def admin = Action {
    val content = new String(Files.readAllBytes(Paths.get("phonegap/www/admin.html")))
    Ok(content).as(HTML)
  }

  // PhoneGap view
  def phonegap = Action {
    val content = new String(Files.readAllBytes(Paths.get("phonegap/www/index.html")))
    Ok(content).as(HTML)
  }

  // -- MENUS

  // Download weekly menus
  def downloadMenus = Action {
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
        try {
          menuDL.downloadDocx(file, 0) // Orangeraie
          // menuDL.downloadDocx(menusDir + "/menu1.docx", 1) // Palmeraie
        } catch {
          case e: Exception => println("Error while downloading menus")
        }

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

  // Get any file from server
  def get(file: String) = Action {
    val source = Paths.get("phonegap/www/" + file)
    val mime = Files.probeContentType(source)

    val content = new String(Files.readAllBytes(source))

    Ok(content).as(withCharset(mime))
  }

  // Get daily menu
  def getTodayMenu: Action[AnyContent] = Action {
    val today: LocalDate = LocalDate.now
    Ok(getMenu(new Timestamp(today.toEpochDay * TimeConstant.MS_PER_DAY)))
  }

  // Get menu at a given date
  def getMenuByDate(dateStr: String): Action[AnyContent] = Action {
    val date: LocalDate = LocalDate.parse(dateStr)
    Ok(getMenu(new Timestamp(date.toEpochDay * TimeConstant.MS_PER_DAY)))
  }

  // Get menus in a range of dates
  def getMenuRange(startStr: String, endStr: String) = Action {
    val startDate: LocalDate = LocalDate.parse(startStr)
    val endDate: LocalDate = LocalDate.parse(endStr)

    val start = new Timestamp(startDate.toEpochDay * TimeConstant.MS_PER_DAY)
    val end = new Timestamp(endDate.toEpochDay * TimeConstant.MS_PER_DAY)

    Ok(getMenu(start, Option(end)))
  }

  // Menu getter interface
  def getMenu(start: Timestamp, end: Option[Timestamp] = None): JsValue = {
    DB.withSession { implicit session =>
      val menusAliments = Menus.get(start, end)
      val menusMap = menusAliments.groupBy(_._1)

      val menus = menusMap map {
        menu => (menu._1, menu._2.map {
          seq => (seq._2, seq._3, seq._4, seq._5)
        })
      }

      val json = menus map {
        menu => Json.obj(
          "id" -> menu._1.id,
          "date" -> menu._1.date,
          "upvote" -> menu._1.upvote,
          "downvote" -> menu._1.downvote,
          "aliments" -> menu._2.map {
            t => Json.obj(
              "id" -> t._1.id,
              "name" -> t._1.name,
              "occurrence" -> t._2._3,
              "last" -> t._2._4,
              "type" -> t._3,
              "nutriments" -> Json.obj(
                "id" -> t._4._1,
                "name" -> t._4._2
              )
            )
          }
        )
      }

      Json.obj("menus" -> json)
    }
  }

  // Upvote a menu
  def upvote(id: Int) = Action {
    DB.withSession { implicit session =>
      Menus.upvote(id)
      Ok("OK")
    }
  }

  // Downvote a menu
  def downvote(id: Int) = Action {
    DB.withSession { implicit session =>
      Menus.downvote(id)
      Ok("OK")
    }
  }

  // -- NUTRIMENTS

  // Get nutriements for a specific aliment
  def getNutrimentsForId(id: Int) = Action {
    DB.withSession { implicit session =>
      val nutriments = MenusAliments.getNutriments(id).groupBy(_._1)

      val json = nutriments map {
        aliment => Json.obj(
          "name" -> aliment._1.name_F,
          "values" -> aliment._2.map {
            n => Json.obj(
              "name" -> n._2.name,
              "value" -> n._3.value,
              "unit" -> n._3.unit,
              "matrix-unit" -> n._3.matrix,
              "value-type" -> n._3.valueType
            )
          }
        )
      }

      Ok(Json.obj("nutriments" -> json))
    }
  }

  // Get a list of every aliments in the nutriments' table
  def getNutriments = Action {
    DB.withSession { implicit session =>
      val nutriments = NutrimentsAliments.list.map {
        n => Json.obj(
          "id" -> n.id,
          "name" -> n.name_F
        )
      }

      Ok(Json.obj("nutriments" -> nutriments))
    }
  }

  // Get a list of every aliments in the nutriments' table matching %name%
  def getNutrimentsFromName(name: String) = Action {
    DB.withSession { implicit session =>
      val nutriments = NutrimentsAliments.filter(_.name like "%"+name+"%").list.map {
        n => Json.obj(
          "id" -> n.id,
          "name" -> n.name_F
        )
      }

      Ok(Json.obj("nutriments" -> nutriments))
    }
  }

  // Link an aliment from the menus to a nutriment's aliment
  def setNutriments(aid: Int, nid: Int) = Action {
    DB.withSession { implicit session =>
      MenusAliments.filter(_.id === aid).map(_.nutriment).update(Option(nid))
    }

    Ok("OK")
  }
}

object TimeConstant {
  final val MS_PER_DAY = 24*60*60*1000
}
