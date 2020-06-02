// Allows To Continuously Reload Applications
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

// Allows Scala.js Compilation
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.1.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")

// Extract metadata from sbt and make it available to the code
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

// Best Practices for Production Code
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.12")

addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")