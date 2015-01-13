package controllers

import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

import scala.collection.JavaConversions._

import models._
import menusDownloader._

import play.api.libs.json._
import play.api.mvc._
import java.sql.Timestamp
import java.time._
import play.api.Play.current

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

    val menuDL = new MenuDownloader(user, password, url, domain)

    // menuDL.downloadDocx(menusDir + "/menu0.docx", 0)
    // menuDL.downloadDocx(menusDir + "/menu1.docx", 1)

    val menus = MenuParser.parseMenusDocx(menusDir + "/menu0.docx")
/*
    menus.map {
      day => day.map {
        _ => {
          println(_)
          _
        }
      }
    } */

    println(menus)

    Ok("OK")
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
          seq => (seq._2, seq._3)
        })
      }

      val json = menus map {
        menu => Json.obj(
          "id" -> menu._1.id,
          "date" -> menu._1.date,
          "aliments" -> menu._2.map {
            t => Json.obj(
              "id" -> t._1.id,
              "name" -> t._1.name,
              "type" -> t._2
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
