package eu.monniot.redis

import java.util

import org.scalatest.{FlatSpec, Matchers}
import redis.clients.jedis.{HostAndPort, Jedis, JedisCluster}

class SampleSpec extends FlatSpec with Matchers {

  behavior of "Redis plugin"

  it should "have launched a redis cluster" in {
    val set = new util.HashSet[HostAndPort]()
    set.add(new HostAndPort("localhost", 7895))
    set.add(new HostAndPort("localhost", 7896))
    set.add(new HostAndPort("localhost", 7897))

    val redis = new JedisCluster(set)

    redis.set("test", "OK")
    redis.get("test") shouldEqual "OK"
  }

  it should "have launched a redis server" in {
    val redis = new Jedis("localhost", 7894)

    redis.set("test", "OK")
    redis.get("test") shouldEqual "OK"
  }
}