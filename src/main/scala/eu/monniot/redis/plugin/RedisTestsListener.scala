package eu.monniot.redis.plugin

import java.io.{BufferedReader, InputStreamReader}

import redis.embedded.{Redis, RedisExecProvider, RedisServer}
import redis.embedded.cluster.RedisCluster
import sbt.{File, Logger, TestEvent, TestResult, TestsListener}


class RedisTestsListener(logger: Logger,
                         binaries: Seq[((String, OS, Architecture), String)],
                         instances: Seq[RedisInstance]) extends TestsListener {

  private var redisServers: Seq[RedisServer] = _

  private var redisClusters: Seq[RedisCluster] = _

  override def doInit(): Unit = {
    val redisExecProviders = buildProvider(binaries)

    logger.debug(s"Redis configuration: ${binaries.toMap}")
    logger.debug(s"Redis servers defined: $instances")

    startRedisCluster(logger, redisExecProviders, instances.filter(m => m.isRedisCluster))
    startRedisServer(logger, redisExecProviders, instances.filter(m => m.isRedisServer))
  }

  override def doComplete(finalResult: TestResult): Unit = {
    logger.info("Stopping redis instances")

    if (redisServers != null) {
      redisServers.foreach(_.stop())
    }

    if (redisClusters != null) {
      redisClusters.foreach(_.stop())
    }
  }

  private def buildProvider(redisBinaries: Seq[((String, OS, Architecture), String)]) = {
    redisBinaries
      .map { case ((v, os, arch), path) =>
        (v, os, arch, path)
      }
      .groupBy(_._1)
      .map { case (v, list) =>
        val provider = RedisExecProvider.build()

        list.foreach { case (_, os, arch, path) =>
          provider.`override`(os.toJava, arch.toJava, path)
        }

        (v, provider)
      }
  }

  private def startRedisServer(logger: Logger, providers: Map[String, RedisExecProvider], redisList: Seq[RedisInstance]): Unit = {
    redisServers = redisList.map { config =>

      val port = config.ports.copy().next()

      ensureFileExecutable(providers(config.version).get(), logger)

      val redisServer = new RedisServer.Builder()
        .redisExecProvider(providers(config.version))
        .port(port)
        .build()

      startAndCaptureErrors(redisServer, logger)

      logger.info(s"Redis Server started on port $port")

      redisServer
    }
  }

  private def startRedisCluster(logger: Logger, providers: Map[String, RedisExecProvider], redis: Seq[RedisInstance]): Unit = {
    redisClusters = redis.map { config =>

      logger.info("Starting Redis Cluster")

      ensureFileExecutable(providers(config.version).get(), logger)

      val redisCluster = new RedisCluster.Builder()
        .serverPorts(config.ports.copy())
        .numOfMasters(config.numOfMaster)
        .withServerBuilder(
          new RedisServer.Builder()
            .setting("bind 127.0.0.1")
            .redisExecProvider(providers(config.version))
        )
        .build()

      startAndCaptureErrors(redisCluster, logger)

      logger.info(s"Redis Cluster started on ports ${redisCluster.ports()}")

      redisCluster
    }
  }

  private def ensureFileExecutable(file: File, logger: Logger) = {
    if (!file.canExecute) {
      logger.debug(s"Making ${file.getAbsolutePath} executable.")
      file.setExecutable(true, true)
    }
  }

  private def startAndCaptureErrors(redis: Redis, logger: Logger): Unit = {
    val reader = new BufferedReader(new InputStreamReader(redis.errors()))

    try {
      redis.start()
    } catch {
      case e: RuntimeException =>
        val ports = redis.ports()
        val error = Stream.continually(reader.readLine()).takeWhile(_ != null).foldLeft(false) { case (_, line) =>
          if (line.contains("Address already in use")) {
            logger.error(s"[${redis.getClass.getSimpleName}@$ports] $line")
            true
          } else false
        }

        if (error) throw e
    }
  }


  // TestReportListener interface, not used but necessary

  override def startGroup(name: String): Unit = {}

  override def testEvent(event: TestEvent): Unit = {}

  override def endGroup(name: String, t: Throwable): Unit = {}

  override def endGroup(name: String, result: TestResult): Unit = {}
}
