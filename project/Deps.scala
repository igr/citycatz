import sbt._

object Deps {

  object Cats extends Dep("org.typelevel", "2.9.0") {
    val Core = module("cats-core") withJavadoc()
    val Effect = module("cats-effect", version = "3.4.1") withJavadoc()
  }

  object Circe extends Dep("io.circe", "0.14.1") {
    val Core = module("circe-core") withJavadoc()
    val Generic = module("circe-generic") withJavadoc()
    val Parser = module("circe-parser") withJavadoc()
  }

  val Logback = "ch.qos.logback" %  "logback-classic" % "1.4.4"
  val ScalaXml = "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
  val Enumeratum = "com.beachape" %% "enumeratum" % "1.7.0"

  val ScalaTest = "org.scalatest" %% "scalatest" % "3.2.14" % Test

  object Sttp extends Dep("com.softwaremill.sttp.client3", "3.8.3") {
    val Core = module("core")
    val Circe = module("circe")
    val Armeria = module("armeria-backend-cats")
  }

  abstract class Dep(val organization: String, val version: String) {

    protected def module(artifact: String,
                         organization: String = organization,
                         version: String = version,
                         crossVersion: Boolean = true): ModuleID =
      (if (crossVersion) organization %% artifact else organization % artifact) % version

  }

}
