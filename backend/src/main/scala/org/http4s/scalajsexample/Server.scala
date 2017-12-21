package org.http4s.scalajsexample

import scala.util.Properties.envOrNone
import cats.effect._
import fs2._
import org.http4s.server.blaze.BlazeBuilder

object Server extends StreamApp[IO] {

  val ip: String = "0.0.0.0"

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] =
    for {
      port <- Stream.eval(IO(envOrNone("HTTP_PORT").map(_.toInt).getOrElse(8080)))
      exitCode <- BlazeBuilder[IO]
        .bindHttp(port, ip)
        .mountService(JSApplication.service)
        .serve
    } yield exitCode

}
