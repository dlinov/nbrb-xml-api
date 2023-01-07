package io.github.dlinov.nbrbxmlapi

import cats.syntax.either._
// import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.ConfigReader._
import pureconfig.generic.derivation.default._

final case class RedisConfig(connectionString: String) derives ConfigReader

final case class AppConfig(port: Int, redis: RedisConfig) derives ConfigReader

object AppConfig {
  def loadDefault = {
    // pureconfig
    ConfigSource.default.load[AppConfig].leftMap(_.prettyPrint())

    // lightbend config
    // Try {
    //   val config = ConfigFactory.load()
    //   AppConfig(
    //     port = config.getInt("port"),
    //     redis = RedisConfig(url = config.getString("redis.url"))
    //   )
    // }.toEither
    //   .leftMap(_.getMessage)
  }
}
