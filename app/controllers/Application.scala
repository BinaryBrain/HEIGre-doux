package controllers

import models._

import play.api.libs.json._
import play.api.mvc._
import java.sql.Timestamp
import java.time._

object Application extends Controller {
  def index = Action {
    Ok(views.html.index())
  }

  def getTodayMenu: Action[AnyContent] = Action {
    val today: LocalDate = LocalDate.now
    Ok(getMenu(new Timestamp(today.toEpochDay * TimeConstant.MS_PER_DAY)))
  }

  def getMenuByDate(dateStr: String): Action[AnyContent] = Action {
    val date: LocalDate = LocalDate.parse(dateStr)
    Ok(getMenu(new Timestamp(date.toEpochDay * TimeConstant.MS_PER_DAY)))
  }

  def getMenuById(id: Int) = TODO

  def getMenu(time: Timestamp): JsValue = {
    DB.withSession { implicit session =>
      val menusAliments = Menus.get(time)
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
