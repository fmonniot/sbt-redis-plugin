import eu.monniot.redis.plugin.{Architecture, OS, RedisKeys, RedisTestsListener}
import sbt.Keys._
import sbt._

object RedisPlugin extends AutoPlugin {

  import RedisKeys._

  override def trigger: PluginTrigger = allRequirements

  override def requires = plugins.JvmPlugin

  object autoImport extends RedisKeys {
    val RedisInstance = eu.monniot.redis.plugin.RedisInstance
    val OS = eu.monniot.redis.plugin.OS
    val Architecture = eu.monniot.redis.plugin.Architecture
  }

  override lazy val projectSettings = Seq(
    redisInstances := Seq.empty,
    redisBinaries := Seq(
      ("3.0.7", OS.MAC_OS_X, Architecture.x86_64) -> "redis-server-3.0.7-darwin",
      ("3.0.7", OS.UNIX, Architecture.x86_64) -> "redis-server-3.0.7"
    ),

    testListeners += new RedisTestsListener(
      streams.value.log,
      redisBinaries.value,
      redisInstances.value
    )
  )
}
