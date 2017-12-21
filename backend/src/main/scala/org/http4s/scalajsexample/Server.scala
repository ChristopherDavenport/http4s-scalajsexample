package org.http4s.scalajsexample

import scala.util.Properties.envOrNone
import cats.effect._
import fs2._
import org.http4s.server.blaze.BlazeBuilder

object Server extends StreamApp[IO] {

  val port: Int = envOrNone("HTTP_PORT") map (_.toInt) getOrElse 8080
  val ip: String = "0.0.0.0"

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(port, ip)
      .mountService(JSApplication.service)
      .serve
}
