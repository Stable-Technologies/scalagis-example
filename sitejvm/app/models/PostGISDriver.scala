package models

import slick.driver.PostgresDriver
import com.github.tminglei.slickpg._

trait PostGISDriver extends PostgresDriver
    with PgArraySupport
    with PgDateSupport
    with PgRangeSupport
    with PgHStoreSupport
    with PgSearchSupport
    with PgPostGISSupport {

  override lazy val Implicit = new ImplicitsPlus {}
  override val simple = new SimpleQLPlus{}

  trait ImplicitsPlus extends Implicits
  with ArrayImplicits
  with DateTimeImplicits
  with RangeImplicits
  with HStoreImplicits
  with SearchImplicits
  with PostGISImplicits

  trait SimpleQLPlus extends SimpleQL
  with ImplicitsPlus
  with SearchAssistants
  with PostGISAssistants
}

object PostGISDriver extends PostGISDriver
