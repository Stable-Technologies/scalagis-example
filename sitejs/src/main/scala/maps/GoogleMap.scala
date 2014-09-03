package maps

import maps.API.PostApp
import org.scalajs.dom
import shared.{TestUser, ApplicationAPI}
import scalatags.JsDom.all._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js
import js.Dynamic.{ global => g }
import autowire._
import upickle._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExport
object GoogleMap {

  val centerCoords = js.Dynamic.newInstance(g.google.maps.LatLng)(36.194168, -115.222060)
  val markerCoords = js.Dynamic.newInstance(g.google.maps.LatLng)(36.138464, -115.156650)
  val defaultZoom = 8

  val infoContent: HtmlTag = {
    div(
      "cloud city las vegas nv"
    )
  }

  def userMarker(user: TestUser, map: js.Dynamic): js.Dynamic = {
    val userCoords = js.Dynamic.newInstance(g.google.maps.LatLng)(
      user.location.lat.v, user.location.lon.v
    )
    val marker = js.Dynamic.newInstance(g.google.maps.Marker)(
      js.Dynamic.literal(
        "position" -> userCoords,
        "map" -> map,
        "title" -> "Cloud City, Las Vegas NV"
      )
    )
    marker
  }
  @JSExport
  def render(elem: String): Unit = {
    val container = dom.document.getElementById(elem)

    val mapOptions = js.Dynamic.literal(
      "center" -> centerCoords,
      "zoom" -> defaultZoom
    )
    val newMap = js.Dynamic.newInstance(g.google.maps.Map)(container, mapOptions)

    val marker = js.Dynamic.newInstance(g.google.maps.Marker)(
      js.Dynamic.literal(
        "position" -> markerCoords,
        "map" -> newMap,
        "title" -> "Cloud City, Las Vegas NV"
      )
    )

    val infoWindow = js.Dynamic.newInstance(g.google.maps.InfoWindow)(
      "content", "Cloud City, Las Vegas NV"
    )

    g.google.maps.event.addListener(marker, "click", { () =>
      infoWindow.open(newMap, marker)
    })

    PostApp[ApplicationAPI].all().call().foreach { users =>
      users.foreach { u => userMarker(u,newMap)}
    }
  }
}