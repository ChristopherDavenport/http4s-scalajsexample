package tutorial.webapp

import scala.scalajs.js.JSApp

import org.scalajs.dom
import dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.jquery.jQuery

object TutorialApp extends JSApp {


  def main(): Unit = {
    jQuery(() => setupUI())
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  @JSExportTopLevel("addClickedMessage")
  def addClickedMessage(): Unit = {
    jQuery("body").append("<p>[message]</p>")
  }

  def setupUI(): Unit = {
    jQuery("#click-me-button").click(() => addClickedMessage())
    jQuery("body").append("<p>Hello World</p>")
  }

}
