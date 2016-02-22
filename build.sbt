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
  "io.swagger" %% "swagger-play2" % "1.5.1",
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
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootswatch-flatly" % "3.3.1+2",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars.bower" % "angular-route" % "1.4.3",
  "org.webjars" % "ui-grid" % "3.0.1" exclude("org.webjars", "angularjs"),
  "org.webjars.bower" % "Sortable" % "1.2.0",
  "org.webjars" % "font-awesome" % "4.4.0",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0" exclude("org.webjars", "bootstrap")
)

resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

//packager conf

maintainer in Linux := "Marcin Gosk <marcin.gosk@dixrad.com>"

serverLoading in Debian := ServerLoader.SystemV

packageSummary in Linux := "play-swagger-example"

packageDescription := "play-swagger-example"

daemonUser in Linux := "play-swagger-example"

daemonGroup in Linux := "play-swagger-example"

