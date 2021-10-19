package io.github.dlinov.nbrbxmlapi

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.either._
import org.slf4j.LoggerFactory
//import pureconfig.ConfigSource
//import pureconfig.generic.auto._
import com.typesafe.config.ConfigFactory

import scala.util.Try

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    (for {
      config <- loadAppConfig()
    } yield {
      Server.stream[IO](config).compile.drain.as(ExitCode.Success)
    }).leftMap { errors =>
      LoggerFactory.getLogger(getClass).error(errors)
      IO.pure(ExitCode.Error)
    }.merge
  }

  private def loadAppConfig(): Either[String, AppConfig] = {
    // pureconfig
    // ConfigSource.default.load[AppConfig].leftMap(_.prettyPrint())
    Try {
      val config = ConfigFactory.load()
      AppConfig(
        port = Port(config.getInt("port")),
        redis = RedisConfig(url = config.getString("redis.url"))
      )
    }.toEither
      .leftMap(_.getMessage)
  }

}
