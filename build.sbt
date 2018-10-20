name := "redis-plugin"

organization := "eu.monniot.redis"

sbtPlugin := true

scalaVersion in Global := "2.10.6"

version := "0.5.0"

resolvers += Resolver.jcenterRepo

libraryDependencies += "eu.monniot.redis" % "embedded-redis" % "1.6.0"

// Scripted - sbt plugin tests
scriptedSettings
scriptedLaunchOpts <+= version apply { v => "-Dproject.version=" + v }

// Publish Information

bintrayRepository := "sbt-plugins"

bintrayOrganization := None

publishArtifact in Test := false

homepage := Some(url("https://github.com/fmonniot/sbt-redis-plugin"))

licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/fmonniot/sbt-redis-plugin"),
  connection = "scm:git:git@github.com:fmonniot/sbt-redis-plugin.git"
))

developers := List(Developer(
  id = "fmonniot",
  name = "François Monniot",
  email = "francois@monniot.eu",
  url = url("http://francois.monniot.eu")
))
