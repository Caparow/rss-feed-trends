name := "rss-feed-trends"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  //akka-http
  "com.typesafe.akka" %% "akka-http"   % "10.1.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",

  "com.typesafe.play" %% "play-json" % "2.6.7",

  //guice
  "com.google.inject" % "guice" % "4.1.0",
  "com.google.inject.extensions" % "guice-throwingproviders" % "4.1.0",

  //scalactic
  "org.scalactic" %% "scalactic" % "3.0.5",

  //pure config
  "com.github.pureconfig" %% "pureconfig" % "0.9.1"
)