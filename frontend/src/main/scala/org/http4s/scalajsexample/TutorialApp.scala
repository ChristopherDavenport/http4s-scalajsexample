package org.http4s.scalajsexample

import org.scalajs.dom
import scala.scalajs.js.annotation.JSExportTopLevel

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

}
