lazy val `redis-plugin` = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "redis-plugin",
    organization := "eu.monniot.redis",

    version := "0.6.0",

    resolvers += Resolver.jcenterRepo,
    libraryDependencies += "eu.monniot.redis" % "embedded-redis" % "1.6.0"
  )
  .settings(ourScriptedSettings: _*)
  .settings(publishSettings: _*)

// Scripted - sbt plugin tests
lazy val ourScriptedSettings = Seq(
  scriptedLaunchOpts := { scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
  },
  scriptedBufferLog := false
)

// Publish Information
lazy val publishSettings = Seq(
  bintrayRepository := "sbt-plugins",
  bintrayOrganization := None,
  publishArtifact in Test := false,
  homepage := Some(url("https://github.com/fmonniot/sbt-redis-plugin")),
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  scmInfo := Some(ScmInfo(
    browseUrl = url("https://github.com/fmonniot/sbt-redis-plugin"),
    connection = "scm:git:git@github.com:fmonniot/sbt-redis-plugin.git"
  )),
  developers := List(Developer(
    id = "fmonniot",
    name = "François Monniot",
    email = "francois@monniot.eu",
    url = url("http://francois.monniot.eu")
  ))
)
