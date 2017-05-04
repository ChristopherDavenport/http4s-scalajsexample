package edu.eckerd.formtest

import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.twirl._

object HelloWorld {
  val service = HttpService {
    case req @ GET -> Root =>
      Ok(html.submissionForm("Form"))

    case GET -> Root / "ping" =>
      Ok("pong")
    case GET -> Root / "pong" =>
      Ok("ping")


    case req @ GET -> Root / "form-encoded" =>
      Ok(html.formEncoded())

    case req @ POST -> Root / "form-encoded" =>
      // EntityDecoders return a Task[A] which is easy to sequence
      req.decode[UrlForm] { m =>
        val s = m.values.mkString("\n")
        Ok(s"Form Encoded Data\n$s")
      }

    case GET -> Root / "hello" / name =>
      Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}")))

    case req @ GET -> Root / "sum" =>
      Ok(html.submissionForm("sum"))


  }
}
