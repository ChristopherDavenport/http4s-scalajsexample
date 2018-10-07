package org.http4s.scalajsexample

import scala.util.Properties.envOrNone
import cats.implicits._
import cats.effect._
import fs2._
import org.http4s.server.blaze.BlazeBuilder

object Server extends IOApp {

  val ip: String = "0.0.0.0"

  override def run(args: List[String]): IO[ExitCode] =
    stream[IO](args).compile.drain.as(ExitCode.Success)

  def stream[F[_]: ConcurrentEffect: Timer: ContextShift](args: List[String]): Stream[F, ExitCode] =
    for {
      _ <- Stream(args) // discard unused args
      port <- Stream.eval(ConcurrentEffect[F].delay(envOrNone("HTTP_PORT").map(_.toInt).getOrElse(8080)))
      exitCode <- BlazeBuilder[F]
        .bindHttp(port, ip)
        .mountService(JSApplication.service, "/")
        .serve
    } yield exitCode

}
