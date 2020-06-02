package org.http4s.scalajsexample

import cats.implicits._
import cats.data._
import cats.effect._
import io.circe.syntax._
import org.http4s._
import org.http4s.CacheDirective._
import org.http4s.MediaType
import org.http4s.dsl.Http4sDsl
import org.http4s.headers._
import org.http4s.circe._
import scala.concurrent.ExecutionContext.global
import scalatags.Text.TypedTag
import scalatags.Text.all._

object JSApplication {

  val jsScript = "http4s-scalajsexample-frontend-fastopt.js"
  val jsDeps = "http4s-scalajsexample-frontend-jsdeps.js"
  val jsScripts: Seq[Modifier] = {
    import scalatags.Text.all._
    List(
      script(src := jsScript),
      script(src := jsDeps)
    )
  }

  def template(
      headContent: Seq[Modifier],
      bodyContent: Seq[Modifier],
      scripts: Seq[Modifier],
      cssComps: Seq[Modifier]): TypedTag[String] = {
    import scalatags.Text.all._

    html(
      head(
        headContent,
        cssComps,
        link(rel:="shortcut icon", media:="image/png", href:="/assets/images/favicon.png")
      ),
      body(
        bodyContent,
        scripts
      )
    )

  }

  val supportedStaticExtensions =
    List(".html", ".js", ".map", ".css", ".png", ".ico")

  def service[F[_]](implicit F: Effect[F], cs: ContextShift[F]) = {
    def getResource(pathInfo: String) = F.delay(getClass.getResource(pathInfo))

    object dsl extends Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {

      case GET -> Root =>
        Ok(template(Seq(), Seq(), jsScripts, Seq()).render)
          .map(
            _.withContentType(`Content-Type`(MediaType.text.html, Charset.`UTF-8`))
              .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))
          )
      
      case GET -> Root / "button" =>
        Ok(template(Seq(), Seq(), jsScripts, Seq()).render)
          .map(
            _.withContentType(`Content-Type`(MediaType.text.html, Charset.`UTF-8`))
              .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))
          )
      
      case GET -> Root / "ajax" =>
        Ok(template(Seq(), Seq(), jsScripts, Seq()).render)
          .map(
            _.withContentType(`Content-Type`(MediaType.text.html, Charset.`UTF-8`))
              .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))
          )
      
      case GET -> Root / "json" / name =>
        Ok(MyData(name).asJson)

      case req if supportedStaticExtensions.exists(req.pathInfo.endsWith) =>
        StaticFile.fromResource[F](req.pathInfo, global, req.some)
          .orElse(OptionT.liftF(getResource(req.pathInfo)).flatMap(StaticFile.fromURL[F](_, global, req.some)))
          .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
          .fold(NotFound())(_.pure[F])
          .flatten

    }.orNotFound
  }

}
