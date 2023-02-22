val scala3Version = "3.2.2"
val natchezExtrasVersion = "7.0.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "traced-logging",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= List(
      "org.typelevel" %% "log4cats-slf4j" % "2.2.0",
       "com.ovoenergy" %% "natchez-extras-log4cats" % natchezExtrasVersion,
      "ch.qos.logback"  %  "logback-classic"      % "1.2.3",
      "com.ovoenergy"   %% "natchez-extras-slf4j" % natchezExtrasVersion,
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
