package maps




import org.scalajs.dom
import upickle._

import scala.concurrent.{Promise, Future}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

object API {
  lazy val secure: Boolean = false

  def post(url: String, pickled: String, retry: Boolean = true): Future[String] = {
    dom.extensions.Ajax.post(
      url = url,
      data = pickled
    ).map(_.responseText)
  }

  class Post(hackUrl: String) extends autowire.Client[String,upickle.Reader,upickle.Writer] {

    override def write[Result: Writer](r: Result) = upickle.write(r)

    override def read[Result: Reader](p: String) = upickle.read[Result](p)

    override def doCall(req: Request): Future[String] = {
      val url = hackUrl+req.path.mkString("/")
      API.post(url,upickle.write(req.args))
    }
  }

  object PostApp extends Post("http://localhost:9000/test/auto/")

}