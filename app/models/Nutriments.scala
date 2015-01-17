package models

import play.api.db.slick.Config.driver.simple._

case class NutrimentsAliment(id: Int, name_F: String, cateory_F: Option[String]) {}
case class NutrimentsName(id: Int, name: String) {}
case class NutrimentsValue(idAliment: Int, idName: Int, value: Double, unit: String, matrix: String, valueType: String) {}

class NutrimentsAliments(tag: Tag) extends Table[NutrimentsAliment](tag, "nutriments_aliments") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name_F")
  def category = column[Option[String]]("category_F")

  override def * = (id, name, category) <> (NutrimentsAliment.tupled, NutrimentsAliment.unapply)
}

class NutrimentsNames(tag: Tag) extends Table[NutrimentsName](tag, "nutriments_names") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  override def * = (id, name) <> (NutrimentsName.tupled, NutrimentsName.unapply)
}

class NutrimentsValues(tag: Tag) extends Table[NutrimentsValue](tag, "nutriments_values") {
  def idAliment = column[Int]("id_nutriments_aliment")
  def idName = column[Int]("id_nutriments_name")

  def value = column[Double]("value")
  def unit = column[String]("protein_unit")
  def matrix = column[String]("matrix_unit")
  def valueType = column[String]("value_type")

  override def * = (idAliment, idName, value, unit, matrix, valueType) <> (NutrimentsValue.tupled, NutrimentsValue.unapply)

  def alimentFK = foreignKey("nutrimentsvalues_nutrimentsaliments_fk", idAliment, NutrimentsAliments)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  def nameFK = foreignKey("nutrimentsvalues_nutrimentsnames_fk", idName, NutrimentsNames)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
}

object NutrimentsAliments extends TableQuery(new NutrimentsAliments(_))
object NutrimentsNames extends TableQuery(new NutrimentsNames(_))
object NutrimentsValues extends TableQuery(new NutrimentsValues(_))
