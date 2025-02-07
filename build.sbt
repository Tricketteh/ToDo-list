name := """ToDo-list"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(guice,
  "org.playframework" %% "play-json" % "3.0.4",
  "org.reactivemongo" % "play2-reactivemongo_2.13" % "1.1.0-play30.RC14",
  "org.reactivemongo" % "reactivemongo-play-json-compat_2.13" % "1.1.0-play210.RC14",
  "io.sentry" % "sentry-logback" % "8.1.0" % Test,
  "com.github.sbt.junit" % "jupiter-interface" % "0.13.3" % Test,
  "org.mockito" % "mockito-core" % "5.15.2" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
