import sbt._

val projectId = "cats"

name := projectId

Settings.global(projectId)

val tagged =
  project.in(file("tagged"))
    .settings(Settings.common)
    .settings(
      name := "tagged",
      libraryDependencies ++= Seq()
    )

val parser =
  project.in(file("parser"))
    .dependsOn(tagged)
    .settings(Settings.common)
    .settings(
      name := "parser",
      libraryDependencies ++= Seq(
        Deps.ScalaXml,
        Deps.Enumeratum
      )
    )

val app =
  project.in(file("app"))
    .enablePlugins(JavaAppPackaging, UniversalPlugin)
    .dependsOn(parser)
    .settings(Settings.common)
    .settings(
      name := "app",
      libraryDependencies ++= Seq(
        Deps.Cats.Core,
        Deps.Cats.Effect,
        Deps.Circe.Core,
        Deps.Circe.Parser,
        Deps.Circe.Generic,
        Deps.Sttp.Core,
        Deps.Sttp.Circe,
        Deps.Sttp.Armeria,
      )
    )
