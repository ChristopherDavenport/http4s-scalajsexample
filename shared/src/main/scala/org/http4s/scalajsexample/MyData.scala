package org.http4s.scalajsexample

import cats.Show
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class MyData(
                 name: String
                 )

object MyData {
  implicit val myDataEnc : Encoder[MyData] = deriveEncoder[MyData]
  implicit val myDataDec : Decoder[MyData] = deriveDecoder[MyData]
  implicit val myDataShow : Show[MyData] = Show.fromToString[MyData]
}
