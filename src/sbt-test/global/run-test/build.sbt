name := "run-test"
version := "1.0"

scalaVersion in Global := "2.11.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % Test
libraryDependencies += "redis.clients" % "jedis" % "2.8.0" % Test
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.18" % Test

redisInstances := Seq(
  RedisInstance("3.0.7", RedisInstance.SERVER, Seq(7894)),
  RedisInstance("3.0.7", RedisInstance.CLUSTER, Seq(7895, 7896, 7897))
)