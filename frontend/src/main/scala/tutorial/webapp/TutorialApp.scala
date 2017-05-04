package tutorial.webapp

import scala.scalajs.js.JSApp
import org.scalajs.dom
import dom.document

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
 * Tuturial WebApp entry point
 */
@JSExportTopLevel("TutorialApp")
object TutorialApp extends JSApp {

  def main(): Unit =
    appendPar(document.body, "Hello World")

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  @JSExportTopLevel("addClickedMessage")
  def addClickedMessage(): Unit =
    appendPar(document.body, "You Clicked The Button")

}
