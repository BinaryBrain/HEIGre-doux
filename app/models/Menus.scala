package models

import java.sql.Timestamp
import java.time.{ZoneOffset, ZoneId, LocalDate}
import controllers.TimeConstant

import scala.collection.JavaConversions._
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.JdbcDriver.backend.Database
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import scala.util.Try

case class Menu(id: Int, date: Timestamp, upvote: Int, downvote: Int) {}
case class MenusAliment(id: Int, idMenu: Int, idAliment: Option[Int], name: String, `type`: Int, nutriment: Option[Int]) {}
case class Aliment(id: Int, name: String, occurrence: Int, last: Timestamp) {}
case class Type(id: Int, name: String) {}

class Menus(tag: Tag) extends Table[Menu](tag, "menus") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[Timestamp]("date")
  def upvote = column[Int]("upvote")
  def downvote = column[Int]("downvote")

  def * = (id, date, upvote, downvote) <> (Menu.tupled, Menu.unapply)
}

class MenusAliments(tag: Tag) extends Table[MenusAliment](tag, "menus_aliments") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def idMenu = column[Int]("id_menu")
  def idAliment = column[Option[Int]]("id_aliment")
  def name = column[String]("name")
  def `type` = column[Int]("type")
  def nutriment = column[Option[Int]]("nutriment")

  def * = (id, idMenu, idAliment, name, `type`, nutriment) <> (MenusAliment.tupled, MenusAliment.unapply)

  def menuFK = foreignKey("menusaliments_menu_fk", idMenu, Menus)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  def alimentFK = foreignKey("menusaliments_aliments_fk", idAliment, Aliments)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  def nutrimentFK = foreignKey("menusaliments_nutriments_fk", nutriment, NutrimentsAliments)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  def typeFK = foreignKey("menusaliments_type_fk", `type`, Types)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
}

class Aliments(tag: Tag) extends Table[Aliment](tag, "aliments") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def occurrence = column[Int]("occurrence", O.Default(0))
  def last = column[Timestamp]("last")

  def * = (id, name, occurrence, last) <> (Aliment.tupled, Aliment.unapply)

  def nameIndex = index("name_index", name, unique = true)
}

class Types(tag: Tag) extends Table[Type](tag, "types") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")

  def * = (id, name) <> (Type.tupled, Type.unapply)
}

object Menus extends TableQuery(new Menus(_)) {
  // Get Menus and their contents
  def get(start: Timestamp, endOpt: Option[Timestamp] = None)(implicit s: Session) = {

    val today: LocalDate = LocalDate.now
    val zone: ZoneId = ZoneId.of("Europe/Paris")
    val endDefault = new Timestamp(today.atTime(23, 59).atZone(zone).toEpochSecond() * 1000)

    val end = endOpt.getOrElse(endDefault)
    (Menus.filter(m => m.date >= start && m.date <= end)
      join MenusAliments on (_.id === _.idMenu)
      join Aliments on (_._2.idAliment === _.id)
      join Types on (_._1._2.`type` === _.id)
      leftJoin NutrimentsAliments on (_._1._1._2.nutriment === _.id)
    ).map (t => ( // flatten tuples and transform nullable to options
      t._1._1._1._1,
      t._1._1._1._2,
      (t._1._1._2.id.?, t._1._1._2.name.?, t._1._1._2.occurrence.?, t._1._1._2.last.?),
      t._1._2,
      (t._2.id.?, t._2.name.?))
    ).run
  }

  // Add a menu in the DB, trying to find which kind of food it is
  def add(date: Timestamp, menu: menusDownloader.Menu)(implicit s: Session) {
    val mid = (Menus returning Menus.map(_.id)) += Menu(0, date, 0, 0)

    val alimentQuery = Q.query[String, (Int, String)]("call findFood(?)")

    menu.getAliments foreach {
      a => {
        val t = Types !+= Type(a.getType.ordinal, a.getType.name)

        val aliment = alimentQuery(a.getName).firstOption.map(_._1)

        println("MenusAliment: " + a.getName)
        println("Aliment: " + aliment)

        MenusAliments += MenusAliment(0, mid, aliment, a.getName, t.id, None)
      }
    }
  }

  def upvote(id: Int)(implicit s: Session) = {
    val (up, date) = Menus.filter(_.id === id).map(m => (m.upvote, m.date)).first
    Menus.filter(_.id === id).map(m => (m.upvote, m.date)).update(up + 1, date).run
  }

  def downvote(id: Int)(implicit s: Session) = {
    val (down, date) = Menus.filter(_.id === id).map(m => (m.downvote, m.date)).first
    Menus.filter(_.id === id).map(m => (m.downvote, m.date)).update(down + 1, date).run
  }
}

object MenusAliments extends TableQuery(new MenusAliments(_)) {
  // Get nutrimentive values for a given aliment
  def getNutriments(id: Int)(implicit s: Session) = {
    (MenusAliments.filter(_.id === id)
      join NutrimentsAliments on (_.nutriment === _.id)
      join NutrimentsValues on (_._2.id === _.idAliment)
      join NutrimentsNames on (_._2.idName === _.id)
    ).map (t => (t._1._1._2, t._2, t._1._2)).run
  }
}

object Aliments extends TableQuery(new Aliments(_))
object Types extends TableQuery(new Types(_)) {
  // Get id or insert a new type
  def !+=(t: Type)(implicit s: Session) = {
    val query = Types.filter(_.name === t.name)
    Try {
      query.first
    } getOrElse {
      Types += t
      t
    }
  }
}
