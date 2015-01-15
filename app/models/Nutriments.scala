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


  /*
  def ID = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def ID_V_4_0 = column[Option[Int]]("ID_V_4_0")
  def ID_SwissFIR = column[Option[Int]]("ID_SwissFIR")
  def name_D = column[Option[String]]("name_D")
  def synonyms_D = column[Option[String]]("synonyms_D")
  def name_F = column[Option[String]]("name_F")
  def synonyms_F = column[Option[String]]("synonyms_F")
  def name_I = column[Option[String]]("name_I")
  def synonyms_I = column[Option[String]]("synonyms_I")
  def name_E = column[Option[String]]("name_E")
  def synonyms_E = column[Option[String]]("synonyms_E")
  def category_D = column[Option[String]]("category_D")
  def category_F = column[Option[String]]("category_F")
  def category_I = column[Option[String]]("category_I")
  def category_E = column[Option[String]]("category_E")
  def specific_gravity = column[Option[String]]("specific_gravity")
  def protein = column[Option[Float]]("protein")
  def protein_unit = column[Option[String]]("protein_unit")
  def protein_matrix_unit = column[Option[String]]("protein_matrix_unit")
  def protein_value_type = column[Option[String]]("protein_value_type")
  def charbohydrate_total = column[Option[Float]]("charbohydrate_total")
  def charbohydrate_total_unit = column[Option[String]]("charbohydrate_total_unit")
  def charbohydrate_total_matrix_unit = column[Option[String]]("charbohydrate_total_matrix_unit")
  def charbohydrate_total_value_type = column[Option[String]]("charbohydrate_total_value_type")
  def charbohydrate_available = column[Option[Float]]("charbohydrate_available")
  def charbohydrate_available_unit = column[Option[String]]("charbohydrate_available_unit")
  def charbohydrate_available_matrix_unit = column[Option[String]]("charbohydrate_available_matrix_unit")
  def charbohydrate_available_value_type = column[Option[String]]("charbohydrate_available_value_type")
  def fat_total = column[Option[Float]]("fat_total")
  def fat_total_unit = column[Option[String]]("fat_total_unit")
  def fat_total_matrix_unit = column[Option[String]]("fat_total_matrix_unit")
  def fat_total_value_type = column[Option[String]]("fat_total_value_type")
  def alcohol = column[Option[Float]]("alcohol")
  def alcohol_unit = column[Option[String]]("alcohol_unit")
  def alcohol_matrix_unit = column[Option[String]]("alcohol_matrix_unit")
  def alcohol_value_type = column[Option[String]]("alcohol_value_type")
  def energy_kJ = column[Option[Float]]("energy_kJ")
  def energy_kJ_unit = column[Option[String]]("energy_kJ_unit")
  def energy_kJ_matrix_unit = column[Option[String]]("energy_kJ_matrix_unit")
  def energy_kJ_value_type = column[Option[String]]("energy_kJ_value_type")
  def energy_kcal = column[Option[Float]]("energy_kcal")
  def energy_kcal_unit = column[Option[String]]("energy_kcal_unit")
  def energy_kcal_matrix_unit = column[Option[String]]("energy_kcal_matrix_unit")
  def energy_kcal_value_type = column[Option[String]]("energy_kcal_value_type")
  def water = column[Option[Float]]("water")
  def water_unit = column[Option[String]]("water_unit")
  def water_matrix_unit = column[Option[String]]("water_matrix_unit")
  def water_value_type = column[Option[String]]("water_value_type")
  def fatty_acids_total_saturated = column[Option[Float]]("fatty_acids_total_saturated")
  def fatty_acids_total_saturated_unit = column[Option[String]]("fatty_acids_total_saturated_unit")
  def fatty_acids_total_saturated_matrix_unit = column[Option[String]]("fatty_acids_total_saturated_matrix_unit")
  def fatty_acids_total_saturated_value_type = column[Option[String]]("fatty_acids_total_saturated_value_type")
  def fatty_acids_total_mono_unsaturated = column[Option[Float]]("fatty_acids_total_mono_unsaturated")
  def fatty_acids_total_mono_unsaturated_unit = column[Option[String]]("fatty_acids_total_mono_unsaturated_unit")
  def fatty_acids_total_mono_unsaturated_matrix_unit = column[Option[String]]("fatty_acids_total_mono_unsaturated_matrix_unit")
  def fatty_acids_total_mono_unsaturated_value_type = column[Option[String]]("fatty_acids_total_mono_unsaturated_value_type")
  def fatty_acids_total_poly_unsaturated = column[Option[Float]]("fatty_acids_total_poly_unsaturated")
  def fatty_acids_total_poly_unsaturated_unit = column[Option[String]]("fatty_acids_total_poly_unsaturated_unit")
  def fatty_acids_total_poly_unsaturated_matrix_unit = column[Option[String]]("fatty_acids_total_poly_unsaturated_matrix_unit")
  def fatty_acids_total_poly_unsaturated_value_type = column[Option[String]]("fatty_acids_total_poly_unsaturated_value_type")
  def cholesterol = column[Option[Float]]("cholesterol")
  def cholesterol_unit = column[Option[String]]("cholesterol_unit")
  def cholesterol_matrix_unit = column[Option[String]]("cholesterol_matrix_unit")
  def cholesterol_value_type = column[Option[String]]("cholesterol_value_type")
  def starch_total = column[Option[Float]]("starch_total")
  def starch_total_unit = column[Option[String]]("starch_total_unit")
  def starch_total_matrix_unit = column[Option[String]]("starch_total_matrix_unit")
  def starch_total_value_type = column[Option[String]]("starch_total_value_type")
  def sugar_total = column[Option[Float]]("sugar_total")
  def sugar_total_unit = column[Option[String]]("sugar_total_unit")
  def sugar_total_matrix_unit = column[Option[String]]("sugar_total_matrix_unit")
  def sugar_total_value_type = column[Option[String]]("sugar_total_value_type")
  def dietary_fibre_total = column[Option[Float]]("dietary_fibre_total")
  def dietary_fibre_total_unit = column[Option[String]]("dietary_fibre_total_unit")
  def dietary_fibre_total_matrix_unit = column[Option[String]]("dietary_fibre_total_matrix_unit")
  def dietary_fibre_total_value_type = column[Option[String]]("dietary_fibre_total_value_type")
  def sodium = column[Option[Float]]("sodium")
  def sodium_unit = column[Option[String]]("sodium_unit")
  def sodium_matrix_unit = column[Option[String]]("sodium_matrix_unit")
  def sodium_value_type = column[Option[String]]("sodium_value_type")
  def potassium = column[Option[Float]]("potassium")
  def potassium_unit = column[Option[String]]("potassium_unit")
  def potassium_matrix_unit = column[Option[String]]("potassium_matrix_unit")
  def potassium_value_type = column[Option[String]]("potassium_value_type")
  def chlorid = column[Option[Float]]("chlorid")
  def chlorid_unit = column[Option[String]]("chlorid_unit")
  def chlorid_matrix_unit = column[Option[String]]("chlorid_matrix_unit")
  def chlorid_value_type = column[Option[String]]("chlorid_value_type")
  def calcium = column[Option[Float]]("calcium")
  def calcium_unit = column[Option[String]]("calcium_unit")
  def calcium_matrix_unit = column[Option[String]]("calcium_matrix_unit")
  def calcium_value_type = column[Option[String]]("calcium_value_type")
  def magnesium = column[Option[Float]]("magnesium")
  def magnesium_unit = column[Option[String]]("magnesium_unit")
  def magnesium_matrix_unit = column[Option[String]]("magnesium_matrix_unit")
  def magnesium_value_type = column[Option[String]]("magnesium_value_type")
  def phosphor = column[Option[Float]]("phosphor")
  def phosphor_unit = column[Option[String]]("phosphor_unit")
  def phosphor_matrix_unit = column[Option[String]]("phosphor_matrix_unit")
  def phosphor_value_type = column[Option[String]]("phosphor_value_type")
  def iron_total = column[Option[Float]]("iron_total")
  def iron_total_unit = column[Option[String]]("iron_total_unit")
  def iron_total_matrix_unit = column[Option[String]]("iron_total_matrix_unit")
  def iron_total_value_type = column[Option[String]]("iron_total_value_type")
  def zinc = column[Option[Float]]("zinc")
  def zinc_unit = column[Option[String]]("zinc_unit")
  def zinc_matrix_unit = column[Option[String]]("zinc_matrix_unit")
  def zinc_value_type = column[Option[String]]("zinc_value_type")
  def iodide = column[Option[Float]]("iodide")
  def iodide_unit = column[Option[String]]("iodide_unit")
  def iodide_matrix_unit = column[Option[String]]("iodide_matrix_unit")
  def iodide_value_type = column[Option[String]]("iodide_value_type")
  def vit_A = column[Option[Float]]("vit_A")
  def vit_A_unit = column[Option[String]]("vit_A_unit")
  def vit_A_matrix_unit = column[Option[String]]("vit_A_matrix_unit")
  def vit_A_value_type = column[Option[String]]("vit_A_value_type")
  def all_trans_retinol_equivalents = column[Option[Float]]("all_trans_retinol_equivalents")
  def all_trans_retinol_equivalents_unit = column[Option[String]]("all_trans_retinol_equivalents_unit")
  def all_trans_retinol_equivalents_matrix_unit = column[Option[String]]("all_trans_retinol_equivalents_matrix_unit")
  def all_trans_retinol_equivalents_value_type = column[Option[String]]("all_trans_retinol_equivalents_value_type")
  def beta_carotene_equivalents = column[Option[Float]]("beta_carotene_equivalents")
  def beta_carotene_equivalents_unit = column[Option[String]]("beta_carotene_equivalents_unit")
  def beta_carotene_equivalents_matrix_unit = column[Option[String]]("beta_carotene_equivalents_matrix_unit")
  def beta_carotene_equivalents_value_type = column[Option[String]]("beta_carotene_equivalents_value_type")
  def beta_carotene = column[Option[Float]]("beta_carotene")
  def beta_carotene_unit = column[Option[String]]("beta_carotene_unit")
  def beta_carotene_matrix_unit = column[Option[String]]("beta_carotene_matrix_unit")
  def beta_carotene_value_type = column[Option[String]]("beta_carotene_value_type")
  def B1 = column[Option[Float]]("B1")
  def B1_unit = column[Option[String]]("B1_unit")
  def B1_matrix_unit = column[Option[String]]("B1_matrix_unit")
  def B1_value_type = column[Option[String]]("B1_value_type")
  def B2 = column[Option[Float]]("B2")
  def B2_unit = column[Option[String]]("B2_unit")
  def B2_matrix_unit = column[Option[String]]("B2_matrix_unit")
  def B2_value_type = column[Option[String]]("B2_value_type")
  def B6 = column[Option[Float]]("B6")
  def B6_unit = column[Option[String]]("B6_unit")
  def B6_matrix_unit = column[Option[String]]("B6_matrix_unit")
  def B6_value_type = column[Option[String]]("B6_value_type")
  def B12 = column[Option[Float]]("B12")
  def B12_unit = column[Option[String]]("B12_unit")
  def B12_matrix_unit = column[Option[String]]("B12_matrix_unit")
  def B12_value_type = column[Option[String]]("B12_value_type")
  def C = column[Option[Float]]("C")
  def C_unit = column[Option[String]]("C_unit")
  def C_matrix_unit = column[Option[String]]("C_matrix_unit")
  def C_value_type = column[Option[String]]("C_value_type")
  def D = column[Option[Float]]("D")
  def D_unit = column[Option[String]]("D_unit")
  def D_matrix_unit = column[Option[String]]("D_matrix_unit")
  def D_value_type = column[Option[String]]("D_value_type")
  def E = column[Option[Float]]("E")
  def E_unit = column[Option[String]]("E_unit")
  def E_matrix_unit = column[Option[String]]("E_matrix_unit")
  def E_value_type = column[Option[String]]("E_value_type")
  def niacine = column[Option[Float]]("niacine")
  def niacine_unit = column[Option[String]]("niacine_unit")
  def niacine_matrix_unit = column[Option[String]]("niacine_matrix_unit")
  def niacine_value_type = column[Option[String]]("niacine_value_type")
  def folate = column[Option[Float]]("folate")
  def folate_unit = column[Option[String]]("folate_unit")
  def folate_matrix_unit = column[Option[String]]("folate_matrix_unit")
  def folate_value_type = column[Option[String]]("folate_value_type")
  def pantothenic_acid = column[Option[Float]]("pantothenic_acid")
  def pantothenic_acid_unit = column[Option[String]]("pantothenic_acid_unit")
  def pantothenic_acid_matrix_unit = column[Option[String]]("pantothenic_acid_matrix_unit")
  def pantothenic_acid_value_type = column[Option[String]]("pantothenic_acid_value_type")
  def record_has_changed = column[Option[String]]("record_has_changed")

*/
  /*ID :: ID_V_4_0 :: ID_SwissFIR ::
    name_D :: synonyms_D :: name_F :: synonyms_F :: name_I :: synonyms_I :: name_E :: synonyms_E :: category_D :: category_F :: category_I :: category_E :: specific_gravity ::
    protein :: protein_unit :: protein_matrix_unit :: protein_value_type ::
    charbohydrate_total :: charbohydrate_total_unit :: charbohydrate_total_matrix_unit :: charbohydrate_total_value_type ::
    charbohydrate_available :: charbohydrate_available_unit :: charbohydrate_available_matrix_unit :: charbohydrate_available_value_type ::
    fat_total :: fat_total_unit :: fat_total_matrix_unit :: fat_total_value_type ::
    alcohol :: alcohol_unit :: alcohol_matrix_unit :: alcohol_value_type ::
    energy_kJ :: energy_kJ_unit :: energy_kJ_matrix_unit :: energy_kJ_value_type ::
    energy_kcal :: energy_kcal_unit :: energy_kcal_matrix_unit :: energy_kcal_value_type ::
    water :: water_unit :: water_matrix_unit :: water_value_type ::
    fatty_acids_total_saturated :: fatty_acids_total_saturated_unit :: fatty_acids_total_saturated_matrix_unit :: fatty_acids_total_saturated_value_type ::
    fatty_acids_total_mono_unsaturated :: fatty_acids_total_mono_unsaturated_unit :: fatty_acids_total_mono_unsaturated_matrix_unit :: fatty_acids_total_mono_unsaturated_value_type ::
    fatty_acids_total_poly_unsaturated :: fatty_acids_total_poly_unsaturated_unit :: fatty_acids_total_poly_unsaturated_matrix_unit :: fatty_acids_total_poly_unsaturated_value_type ::
    cholesterol :: cholesterol_unit :: cholesterol_matrix_unit :: cholesterol_value_type ::
    starch_total :: starch_total_unit :: starch_total_matrix_unit :: starch_total_value_type ::
    sugar_total :: sugar_total_unit :: sugar_total_matrix_unit :: sugar_total_value_type ::
    dietary_fibre_total :: dietary_fibre_total_unit :: dietary_fibre_total_matrix_unit :: dietary_fibre_total_value_type ::
    sodium :: sodium_unit :: sodium_matrix_unit :: sodium_value_type ::
    potassium :: potassium_unit :: potassium_matrix_unit :: potassium_value_type ::
    chlorid :: chlorid_unit :: chlorid_matrix_unit :: chlorid_value_type ::
    calcium :: calcium_unit :: calcium_matrix_unit :: calcium_value_type ::
    magnesium :: magnesium_unit :: magnesium_matrix_unit :: magnesium_value_type ::
    phosphor :: phosphor_unit :: phosphor_matrix_unit :: phosphor_value_type ::
    iron_total :: iron_total_unit :: iron_total_matrix_unit :: iron_total_value_type ::
    zinc :: zinc_unit :: zinc_matrix_unit :: zinc_value_type ::
    iodide :: iodide_unit :: iodide_matrix_unit :: iodide_value_type ::
    vit_A :: vit_A_unit :: vit_A_matrix_unit :: vit_A_value_type ::
    all_trans_retinol_equivalents :: all_trans_retinol_equivalents_unit :: all_trans_retinol_equivalents_matrix_unit :: all_trans_retinol_equivalents_value_type ::
    beta_carotene_equivalents :: beta_carotene_equivalents_unit :: beta_carotene_equivalents_matrix_unit :: beta_carotene_equivalents_value_type ::
    beta_carotene :: beta_carotene_unit :: beta_carotene_matrix_unit :: beta_carotene_value_type ::
    B1 :: B1_unit :: B1_matrix_unit :: B1_value_type ::
    B2 :: B2_unit :: B2_matrix_unit :: B2_value_type ::
    B6 :: B6_unit :: B6_matrix_unit :: B6_value_type ::
    B12 :: B12_unit :: B12_matrix_unit :: B12_value_type ::
    C :: C_unit :: C_matrix_unit :: C_value_type ::
    D :: D_unit :: D_matrix_unit :: D_value_type ::
    E :: E_unit :: E_matrix_unit :: E_value_type ::
    niacine :: niacine_unit :: niacine_matrix_unit :: niacine_value_type ::
    folate :: folate_unit :: folate_matrix_unit :: folate_value_type ::
    pantothenic_acid :: pantothenic_acid_unit :: pantothenic_acid_matrix_unit :: pantothenic_acid_value_type ::
    record_has_changed :: HNil
  */

