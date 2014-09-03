package controllers

import com.vividsolutions.jts.geom.{PrecisionModel, Coordinate, GeometryFactory}
import play.api._
import play.api.db.slick.DBAction
import play.api.db.slick.Session
import play.api.mvc._
import shared._
import upickle._
import autowire._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

object ApplicationAPIImpl extends ApplicationAPI {

  def Session[T](f: Session => T) = {
    import play.api.Play.current
    play.api.db.slick.DB.withSession(f)
  }

  def Transaction[T](f: Session => T) = {
    import play.api.Play.current
    play.api.db.slick.DB.withTransaction(f)
  }

  def all() = Session { implicit session =>
    models.current.dao.testusers.all()
  }
}

object Application extends Controller with autowire.Server[String,upickle.Reader,upickle.Writer] {

  def write[Result: Writer](r: Result) = upickle.write(r)
  def read[Result: Reader](p: String) = upickle.read[Result](p)

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def testCreate = DBAction { implicit rs =>
    implicit val zzz = rs.dbSession
    Random.nextFloat()
    val omg  = TestUser(-1,"TestUser",LonLat(Longitude(-114.8 + Random.nextFloat()),Latitude(35.9 + Random.nextFloat())))
    val result = models.current.dao.testusers.createIfAvailable(omg).toString
    Ok(result)
  }

  def testShow = DBAction { implicit rs =>
    val users = models.current.dao.testusers.all()(rs.dbSession)
    Ok(views.html.gmap())
  }

  def autoroute(segment: String) = Action.async(parse.text) { implicit request =>
    Application.route[ApplicationAPI](ApplicationAPIImpl)(
      autowire.Core.Request(segment.split('/'),upickle.read[Map[String,String]](request.body))
    ).map { r =>
      Ok(r).withHeaders("Access-Control-Allow-Origin"->"*")
    }
  }

}
