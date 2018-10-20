# sbt-redis-plugin [![Build Status](https://travis-ci.org/fmonniot/sbt-redis-plugin.svg?branch=master)](https://travis-ci.org/fmonniot/sbt-redis-plugin)

Launch redis before your test and bring it down after. No more waiting for it on each test or test suite !

## Usage

Add this snippet to your `project/plugins.sbt`

```scala
resolvers += Resolver.url("fmonniot", url("https://dl.bintray.com/fmonniot/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("eu.monniot.redis" % "redis-plugin" % "0.6.0")
```

_Warning: `redis-plugin` version 0.6+ support SBT 1.x only, use version 0.5.0 if you have to stay on 0.13.x_

You can now configure the plugin:

```scala
// [Optional] Configure a redis binary matrix
redisBinaries := Seq(
  (("3.0.7", OS.UNIX, Architecture.x86_64), "/path/to/the/correct/redis-server")
)

// Launch a server and a cluster
redisInstances := Seq(
  RedisInstance("3.0.7", RedisInstance.SERVER, 7894),
  RedisInstance("3.0.7", RedisInstance.CLUSTER, Seq(7895, 7896, 7897))
)
```