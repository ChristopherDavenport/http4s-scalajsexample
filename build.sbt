import sbtcrossproject.{crossProject, CrossType}

lazy val commonSettings = {
  organization := "org.http4s"
  version := "0.0.1-SNAPSHOT"
  scalaVersion := "2.12.7"
  crossScalaVersions := Seq(scalaVersion.value, "2.11.12")
}

val Http4sVersion = "0.20.0-RC1"
val utestV = "0.6.7"
val scalaJsDomV = "0.9.6"
val scalaTagsV = "0.6.8"
val circeV = "0.11.1"
val catsEffectV = "1.0.0"

// This function allows triggered compilation to run only when scala files changes
// It lets change static files freely
def includeInTrigger(f: java.io.File): Boolean =
  f.isFile && {
    val name = f.getName.toLowerCase
    name.endsWith(".scala") || name.endsWith(".js")
  }

lazy val shared =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "scalatags" % scalaTagsV,
        "io.circe" %%% "circe-core" % circeV,
        "io.circe" %%% "circe-generic" % circeV,
        "io.circe" %%% "circe-parser" % circeV
//        "org.typelevel" %% "cats-effect" % catsEffectV
      )
    )

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val backend = (project in file("backend"))
  .settings(
    name := "http4s-scalajsexample--backend"
  )
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"     %% "http4s-circe"        % Http4sVersion,
      "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
      "ch.qos.logback" % "logback-classic"      % "1.2.3"
    ),
    // Allows to read the generated JS on client
    resources in Compile += (fastOptJS in (frontend, Compile)).value.data,
    // Lets the backend to read the .map file for js
    resources in Compile += (fastOptJS in (frontend, Compile)).value
      .map((x: sbt.File) => new File(x.getAbsolutePath + ".map"))
      .data,
    // Lets the server read the jsdeps file
    (managedResources in Compile) += (artifactPath in (frontend, Compile, packageJSDependencies)).value,
    // do a fastOptJS on reStart
    reStart := (reStart dependsOn (fastOptJS in (frontend, Compile))).evaluated,
    // This settings makes reStart to rebuild if a scala.js file changes on the client
    watchSources ++= (watchSources in frontend).value,
    // Support stopping the running server
    mainClass in reStart := Some("org.http4s.scalajsexample.Server")
  )
  .dependsOn(sharedJvm)

lazy val frontend = (project in file("frontend"))
  .settings(
    name := "http4s-scalajsexample-frontend"
  )
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(
    // Build a js dependencies file
    skip in packageJSDependencies := false,
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),

      // Put the jsdeps file on a place reachable for the server
    crossTarget in (Compile, packageJSDependencies) := (resourceManaged in Compile).value,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJsDomV,
      "com.lihaoyi" %%% "utest"        % utestV % Test
    )
  )
  .dependsOn(sharedJs)
