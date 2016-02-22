import com.typesafe.sbt.packager.archetypes.ServerLoader

net.virtualvoid.sbt.graph.Plugin.graphSettings

name := "play-swagger-example"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DebianPlugin)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "joda-time" % "joda-time" % "2.8.1",
  "org.joda" % "joda-convert" % "1.7",
  "jp.t2v" %% "play2-auth" % "0.14.0",
  "jp.t2v" %% "play2-auth-test" % "0.14.0" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.play" %% "play-mailer" % "3.0.1",
  "com.pellucid" %% "sealerate" % "0.0.3",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24",
  "it.innove" % "play2-pdf" % "1.3.0",
  "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.0",
  "com.restfb" % "restfb" % "1.7.0",
  "io.swagger" %% "swagger-play2" % "1.5.1"
)

resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

