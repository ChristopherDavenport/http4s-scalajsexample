package edu.eckerd.formtest

import cats.data._
import cats.implicits._
import io.circe._
import org.http4s._
import org.http4s.CacheDirective._
import org.http4s.circe._
import org.http4s.{Charset, Request, Response, StaticFile}
import org.http4s.dsl._
import org.http4s.MediaType._
import org.http4s.headers._
import fs2._
import scalatags.Text.TypedTag
import scalatags.Text.all.Modifier

object JSApplication {

  val jsScript = "formtest-frontend-fastopt.js"
  val jsDeps = "formtest-frontend-jsdeps.js"
  val jsScripts: Seq[Modifier] = {
    import scalatags.Text.all._
    List(
      script(src := jsScript),
      script(src := jsDeps),
      script("tutorial.webapp.TutorialApp().main()")
    )
  }

  def parseJs(jsFiles: List[String]): Seq[Modifier] = {
    import scalatags.Text.all._
    jsFiles.map { file =>
      script(src := file)
    }
  }

  def index(
      headContent: Seq[Modifier],
      bodyContent: Seq[Modifier],
      scripts: Seq[Modifier]): TypedTag[String] = {
    import scalatags.Text.all._

    html(
      head(
        headContent
      ),
      body(
        bodyContent,
        scripts
      )
    )

  }

  val service = HttpService {

    case req @ GET -> Root =>
      Ok(index(Seq(), Seq(), jsScripts).render)
        .withContentType(Some(`Content-Type`(`text/html`, Charset.`UTF-8`)))
        .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))

    case req if req.pathInfo.endsWith(".js") =>
      StaticFile
        .fromResource(req.pathInfo, Some(req))
        .map(_.putHeaders())
        .orElse(Option(getClass.getResource(req.pathInfo)).flatMap(
          StaticFile.fromURL(_, Some(req))))
        .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
        .map(Task.now)
        .getOrElse(NotFound())

  }

}
