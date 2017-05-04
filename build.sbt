
val Http4sVersion = "0.17.0-M2"

lazy val commonSettings = {
  organization := "edu.eckerd"
  version := "0.0.1-SNAPSHOT"
  scalaVersion := "2.12.2"
}

lazy val shared =
  (crossProject.crossType(CrossType.Pure) in file ("shared"))
    .settings(commonSettings)
    .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm= shared.jvm
lazy val sharedJs= shared.js

lazy val backend = (project in file("backend"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"     %% "http4s-circe"        % Http4sVersion,
      "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
      "org.http4s"    %% "http4s-twirl"          % Http4sVersion,
      "ch.qos.logback" %  "logback-classic"     % "1.2.1",
      "com.vmunier" %% "scalajs-scripts" % "1.1.0"
    ),
    scalaJSProjects := Seq(frontend)

//    resourceGenerators in Compile += Def.task {
//      val f1 = (fastOptJS in Compile in frontend).value
//      Seq(f1.data)
//    }.taskValue
//
//    watchSources ++= (watchSources in frontend).value
)
  .settings(
    name := "formtest-backend"
  )
  .dependsOn(sharedJvm)
  .enablePlugins(SbtTwirl)

lazy val frontend = (project in file("frontend"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings {
      val upickleV = "0.4.4"
      val utestV = "0.4.5"
      val scalaJsDomV = "0.9.1"

      scalaJSUseMainModuleInitializer := true

      skip in packageJSDependencies := false
      jsDependencies +=
        "org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js"

      jsDependencies += RuntimeDOM

      testFrameworks += new TestFramework("utest.runner.Framework")
      libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % scalaJsDomV,
        "com.lihaoyi" %%% "upickle" % upickleV,
        "com.lihaoyi" %%% "utest" % utestV % "test",
        "be.doeraene" %%% "scalajs-jquery" % "0.9.1"
      )
    }
  .settings(
    name := "formtest-frontend"
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJs)


