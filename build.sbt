val Http4sVersion = "0.17.0-M2"
val upickleV = "0.4.4"
val utestV = "0.4.5"
val scalaJsDomV = "0.9.1"
val scalajsReactV = "0.11.3"

// This function allows triggered compilation to run only when scala files changes
// It lets change static files freely
def includeInTrigger(f: java.io.File): Boolean =
  f.isFile && {
    val name = f.getName.toLowerCase
    name.endsWith(".scala") || name.endsWith(".js")
  }

lazy val commonSettings = {
  organization := "edu.eckerd"
  version := "0.0.1-SNAPSHOT"
  scalaVersion := "2.12.2"
}

lazy val shared =
  (crossProject.crossType(CrossType.Pure) in file("shared"))
    .settings(commonSettings)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val backend = (project in file("backend"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"                   %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"                   %% "http4s-circe"        % Http4sVersion,
      "org.http4s"                   %% "http4s-dsl"          % Http4sVersion,
      "org.http4s"                   %% "http4s-twirl"        % Http4sVersion,
      "ch.qos.logback"               % "logback-classic"      % "1.2.1",
      "com.vmunier"                  %% "scalajs-scripts"     % "1.1.0",
      "com.lihaoyi"                  %% "scalatags"           % "0.6.5",
      "com.github.japgolly.scalacss" %% "ext-scalatags"       % "0.5.1"
    ),
    // Allows to read the generated JS on client
    resources in Compile += (fastOptJS in (frontend, Compile)).value.data,
    // Lets the backend to read the .map file for js
    resources in Compile += (fastOptJS in (frontend, Compile)).value
      .map((x: sbt.File) => new File(x.getAbsolutePath + ".map"))
      .data,
    (managedResources in Compile) += (fastOptJS in (frontend, Compile)).value.data,
    // Lets the server read the jsdeps file
    (managedResources in Compile) += (artifactPath in (frontend, Compile, packageJSDependencies)).value,
    // Support stopping the running server
    mainClass in reStart := Some("edu.eckerd.formtest.Server"),
    // do a fastOptJS on reStart
    reStart := (reStart dependsOn (fastOptJS in (frontend, Compile))).evaluated,
    // This settings makes reStart to rebuild if a scala.js file changes on the client
    watchSources ++= (watchSources in frontend).value,
    // On recompilation only consider changes to .scala and .js files
    watchSources ~= { t: Seq[java.io.File] =>
      { t.filter(includeInTrigger) }
    }
  )
  .settings(
    name := "formtest-backend"
  )
  .dependsOn(sharedJvm)

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(
    // This is a not very nice trick to remove js files that exist on the scala tools
    // library and that conflict with the requested on jsDependencies, in particular
    // with jquery.js
    // See http://stackoverflow.com/questions/35374131/scala-js-missing-js-library, UPDATE #1
    (scalaJSNativeLibraries in Test) := (scalaJSNativeLibraries in Test).map {
      l =>
        l.map(virtualFiles =>
          virtualFiles.filter(vf => {
            val f = vf.toURI.toString
            !(f.endsWith(".js") && f.contains("scala/tools"))
          }))
    }.value,
    // Requires the DOM
    jsDependencies += RuntimeDOM,
    // Build a js dependencies file
    skip in packageJSDependencies := false,
    // Put the jsdeps file on a place reachable for the server
    crossTarget in (Compile, packageJSDependencies) := (resourceManaged in Compile).value,
//    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJsDomV,
      "com.lihaoyi" %%% "utest"        % utestV % Test
    )
  )
  .settings(
    name := "formtest-frontend"
  )
  .dependsOn(sharedJs)
