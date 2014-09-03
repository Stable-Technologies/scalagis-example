package models

import models.users.TestUserComponent
import play.api.db.slick._
import scala.slick.driver.JdbcProfile


class DAO(override val profile: JdbcProfile)
    extends TestUserComponent
    with Profile { }

object current {
  val dao = new DAO(DB(play.api.Play.current).driver)

  object ForSlickTableScan {
    private val x1 = dao.testusers.query
  }
}
