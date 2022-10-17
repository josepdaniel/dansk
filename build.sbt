val scala3Version = "3.1.3"
val circeVersion = "0.15.0-M1"
val http4sVersion = "1.0.0-M36"
val awsSdkVersion = "2.17.243"

val commonPref = Seq(
  organization := "dev.joedaniel",
  version := "0.0.1",
  scalaVersion := scala3Version
)

val commonSettings = commonPref :+
  (libraryDependencies ++=
    Seq(
      "org.typelevel" %% "cats-core" % "2.8.0",
      "org.typelevel" %% "cats-effect" % "3.3.14",
      "org.typelevel" %% "cats-effect-kernel" % "3.3.14",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.http4s" %% "http4s-core" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "is.cir" %% "ciris" % "2.3.3",
      "org.typelevel" %% "log4cats-slf4j" % "2.4.0",
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "software.amazon.awssdk" % "s3" % awsSdkVersion,
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
    ))

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
