package org.http4s.scalajsexample

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSExportTopLevel
import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag

/**
 * Tuturial WebApp entry point
 */
object TutorialApp extends IOApp {

  val Router: Map[String, TypedTag[dom.html.Div]] = Map(
    "/" -> index,
    "/button" -> buttonTag,
    "/ajax" -> ajaxTag
  )

  override def run(args: List[String]): IO[ExitCode] =
    program.as(ExitCode.Success)

  def program: IO[Unit] = for {
    documentRef <- getDocumentRef()
    path <- documentRef.get.map(_.location.pathname)
    page = Router.get(path).getOrElse(index)
    _ <- mountPage(page, documentRef)
  } yield ()
  
  def getDocumentRef(): IO[Ref[IO, HTMLDocument]] =
    Ref.of[IO, HTMLDocument](dom.document)

  def mountPage(
    page: TypedTag[dom.html.Div],
    documentRef: Ref[IO, HTMLDocument]): IO[Unit] =
    for {
      d <- documentRef.get
      _ <- IO(d.body.appendChild(page.render))
    } yield ()

  lazy val index: TypedTag[dom.html.Div] =
    div(id:="indexTag",
      h1(
        style:= "align: center;",
        "Http4s Scala-js Example App"
      ),
      a(href:="/button", h4("Button Example")),
      a(href:="/ajax", h4("Ajax Example"))
    )
  
  lazy val buttonTag: TypedTag[dom.html.Div] = {
    div(id:="buttonTag",
      h1("Push The Button"),
      a(href:="/", h4("Home")),
      button(
        id := "click-me-button",
        `type` := "button",
        onclick := "addClickedMessage()",
        style := "background-color: #4CAF50; /* Green */ " +
          "border: none; " +
          "border-radius: 12px; " +
          "color: white; " +
          "padding: 15px 32px; " +
          "text-align: center; " +
          "text-decoration: none; " +
          "display: inline-block; " +
          "font-size: 16px;",
        "Click Me"
      )
    )
  }
  
  lazy val ajaxTag: TypedTag[dom.html.Div] = {
    div(id:="ajaxTag",
      h1("Push The Button"),
      a(href:="/", h4("Home")),
      button(
        id := "click-me-button",
        `type` := "button",
        onclick := "addAjaxCall()",
        style := "background-color: #4CAF50; /* Green */ " +
          "border: none; " +
          "border-radius: 12px; " +
          "color: white; " +
          "padding: 15px 32px; " +
          "text-align: center; " +
          "text-decoration: none; " +
          "display: inline-block; " +
          "font-size: 16px;",
        "Click Me"
      )
    )
  }

  def appendPar(targetNode: dom.Node, text: String): IO[Unit] =
    for {
      parNode <- IO(dom.document.createElement("p"))
      textNode <- IO(dom.document.createTextNode(text))
      _ <- IO(parNode.appendChild(textNode))
      _ <- IO(targetNode.appendChild(parNode))
    } yield ()

  @JSExportTopLevel("addClickedMessage")
  def addClickedMessage(): Unit =
    appendPar(dom.document.body, "You Clicked The Button").unsafeRunSync

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
