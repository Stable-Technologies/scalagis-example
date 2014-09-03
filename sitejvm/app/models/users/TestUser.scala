package models.users

import com.vividsolutions.jts.geom.{PrecisionModel, GeometryFactory, Coordinate, Point}
import play.api.db.slick._
import shared.{Latitude, Longitude, LonLat, TestUser}


trait TestUserComponent {
    self: Profile =>

  import models.PostGISDriver.simple._

  val wgs84 = new GeometryFactory(new PrecisionModel(),4326)

  implicit val LatLonColumn = MappedColumnType.base[LonLat,Point](
    ll => wgs84.createPoint(new Coordinate(ll.lon.v,ll.lat.v)),
    p => LonLat(Longitude(p.getX),Latitude(p.getY))
  )

  class TestUserTable(tag: Tag) extends Table[TestUser](tag,"scalagis_testusers") {
    def uid = column[Long]("user_id",O.PrimaryKey,O.AutoInc)
    def name = column[String]("name",O.NotNull)
    def location = column[LonLat]("location")
    def * = (uid,name,location) <> (TestUser.tupled,TestUser.unapply)
  }

  object testusers {
    val query = TableQuery[TestUserTable]

    def createIfAvailable(user: TestUser)(implicit session: Session): Option[Long] = {
      Option((query returning query.map(_.uid)) += user)
    }

    def all()(implicit session: Session): List[TestUser] = {
      query.run.toList
    }
  }
}
