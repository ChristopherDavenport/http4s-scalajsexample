package org.http4s.scalajsexample

import cats.data._
import org.http4s._
import org.http4s.CacheDirective._
import org.http4s.dsl._
import org.http4s.MediaType._
import org.http4s.headers._
import fs2._
import scalatags.Text.TypedTag
import scalatags.Text.all.Modifier

object JSApplication {

  val jsScript = "http4s-scalajsexample-frontend-fastopt.js"
  val jsDeps = "http4s-scalajsexample-frontend-jsdeps.js"
  val jsScripts: Seq[Modifier] = {
    import scalatags.Text.all._
    List(
      script(src := jsScript),
      script(src := jsDeps),
      script("org.http4s.scalajsexample.TutorialApp().main()")
    )
  }

  val buttonTag: Seq[Modifier] = {
    import scalatags.Text.all._
    Seq(
      button(
        id := "click-me-button",
        `type` := "button",
        onclick := "addClickedMessage()"
      )
    )
  }

  def index(
      headContent: Seq[Modifier],
      bodyContent: Seq[Modifier],
      scripts: Seq[Modifier],
      css: Seq[Modifier]): TypedTag[String] = {
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

  val supportedStaticExtensions =
    List(".html", ".js", ".map", ".css", ".png", ".ico")

  val service = HttpService {

    case req @ GET -> Root =>
      Ok(index(Seq(), Seq(), jsScripts, Seq()).render)
        .withContentType(Some(`Content-Type`(`text/html`, Charset.`UTF-8`)))
        .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))

    case req @ GET -> Root / "button" =>
      Ok(index(Seq(), buttonTag, jsScripts, Seq()).render)
        .withContentType(Some(`Content-Type`(`text/html`, Charset.`UTF-8`)))
        .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))

    case req if supportedStaticExtensions.exists(req.pathInfo.endsWith) =>
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
