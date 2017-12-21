package org.http4s.scalajsexample

import org.scalajs.dom
import scala.scalajs.js.annotation.JSExportTopLevel
import dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._

/**
 * Tuturial WebApp entry point
 */
@JSExportTopLevel("TutorialApp")
object TutorialApp {

  def main(): Unit = ()

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = dom.document.createElement("p")
    val textNode = dom.document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
    ()
  }

  @JSExportTopLevel("addClickedMessage")
  def addClickedMessage(): Unit =
    appendPar(dom.document.body, "You Clicked The Button")


  @JSExportTopLevel("addAjaxCall")
  def appendResponse(): Unit = {
      Ajax.get("/json/chris")
        .map(_.responseText)
        .map(json =>
          io.circe.parser.parse(json).flatMap(MyData.myDataDec.decodeJson) match {
            case Left(e) => e.getMessage
            case Right(d) => show"Decoded: $d Raw: $json"
          }
        )
        .map(appendPar(dom.document.body, _))
        .onComplete(_ => ())
  }

}
