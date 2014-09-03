package shared

case class Latitude(v: Double)

case class Longitude(v: Double)

case class LonLat(lon: Longitude, lat: Latitude)

case class TestUser(
  uid: Long,
  name: String,
  location: LonLat
)

trait ApplicationAPI {
  def all(): List[TestUser]
}
