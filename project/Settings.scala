import sbt.Keys._
import sbt.{addCompilerPlugin, _}
import scalafix.sbt.ScalafixPlugin.autoImport._

object Settings {

  val ScalacOptions: Seq[String] = Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-explaintypes",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Wdead-code",
    "-Werror",
    "-Wnumeric-widen",
    "-Wunused:implicits",
    "-Wunused:imports",
    "-Wunused:linted",
    "-Wunused:locals",
    "-Wunused:params",
    "-Wunused:patvars",
    "-Wunused:privates",
    "-Wvalue-discard",
    "-Xcheckinit",
    "-Xlint:-byname-implicit",
    "-Xsource:3"
  )

  val common: Seq[Setting[_]] =
    compile ++ publish

  def global(projectId: String): Seq[Setting[_]] =
    common ++ Seq(
      ThisBuild / organization := s"com.foursquare.$projectId",
      ThisBuild / versionScheme := Some("early-semver"),

      ThisBuild / semanticdbEnabled := true,
      ThisBuild / semanticdbVersion := "4.5.13",
      ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0",

      ThisBuild / autoCompilerPlugins := true,
    )

  private lazy val compile: Seq[Setting[_]] =
    Seq(
      scalaVersion := "2.13.10",
      scalacOptions ++= ScalacOptions ++ Seq(
        "-Ybackend-parallelism", Math.max(java.lang.Runtime.getRuntime.availableProcessors() - 1, 1).toString
      ),

      Compile / scalaSource := baseDirectory.value / "main",
      Compile / resourceDirectory := baseDirectory.value / "resources",

      Test / scalaSource := baseDirectory.value / "test",
      Test / resourceDirectory := baseDirectory.value / "test-resources",

      Compile / doc /sources := Seq.empty,
      Compile / packageDoc/ publishArtifact := false,

      sourcesInBase := false,

      libraryDependencies ++= Seq(),

      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
    )

  private lazy val publish: Seq[Setting[_]] =
    Seq(
      publishMavenStyle := true,
      Compile / packageDoc / publishArtifact := false
    )

}
