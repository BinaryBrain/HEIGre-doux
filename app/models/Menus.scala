package models

import java.sql.Timestamp
import play.api.db.slick.Config.driver.simple._

case class Menu(id: Int, date: Timestamp) {}
case class MenusAliment(idMenu: Int, idAliment: Int) {}
case class Aliment(id: Int, name: String, `type`: Int) {}
case class Type(id: Int, name: String) {}

class Menus(tag: Tag) extends Table[Menu](tag, "menus") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[Timestamp]("date")

  def * = (id, date) <> (Menu.tupled, Menu.unapply)
}

class MenusAliments(tag: Tag) extends Table[MenusAliment](tag, "menus_aliments") {
  def idMenu = column[Int]("id_menu")
  def idAliment = column[Int]("id_aliment")

  def * = (idMenu, idAliment) <> (MenusAliment.tupled, MenusAliment.unapply)

  def menuFK = foreignKey("menusaliments_menu_fk", idMenu, Menus)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)
  def alimentsFK = foreignKey("menusaliments_aliments_fk", idMenu, Aliments)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)
}

class Aliments(tag: Tag) extends Table[Aliment](tag, "aliments") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def `type` = column[Int]("type")

  def * = (id, name, `type`) <> (Aliment.tupled, Aliment.unapply)

  def typeFK = foreignKey("aliments_type_fk", `type`, Types)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)
}

class Types(tag: Tag) extends Table[Type](tag, "types") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = (id, name) <> (Type.tupled, Type.unapply)
}

object Menus extends TableQuery(new Menus(_)) {
  def get(start: Timestamp, endOpt: Option[Timestamp] = None)(implicit s: Session) = {
    val end = endOpt.getOrElse(start)
    (Menus.filter(m => m.date >= start && m.date <= end)
      join MenusAliments on (_.id === _.idMenu)
      join Aliments on (_._2.idAliment === _.id)
      join Types on (_._2.`type` === _.id)
    ).map (t => (t._1._1._1, t._1._2, t._2)).run
  }
}

object MenusAliments extends TableQuery(new MenusAliments(_))
object Aliments extends TableQuery(new Aliments(_))
object Types extends TableQuery(new Types(_))