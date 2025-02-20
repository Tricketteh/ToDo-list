name := """ToDo-list"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(guice,
  "org.playframework" %% "play-json" % "3.0.4",
  "org.postgresql" % "postgresql" % "42.7.5",
  "org.playframework" %% "play-slick" % "6.1.1",
  "io.sentry" % "sentry-logback" % "8.2.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
