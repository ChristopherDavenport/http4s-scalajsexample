package org.http4s.scalajsexample

import scala.util.Properties.envOrNone
import cats.implicits._
import cats.effect._
import org.http4s.server.blaze.BlazeServerBuilder

object Server extends IOApp {

  val ip: String = "0.0.0.0"

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO(args) // discard unused args
      port <- IO(envOrNone("HTTP_PORT").map(_.toInt).getOrElse(8080))
      exitCode <- BlazeServerBuilder[IO]
        .bindHttp(port, "localhost")
        .withHttpApp(JSApplication.service)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    } yield exitCode

}
