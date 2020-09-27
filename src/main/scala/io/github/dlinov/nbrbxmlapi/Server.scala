package io.github.dlinov.nbrbxmlapi

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log
import fs2.Stream
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{GZip, Logger}

import scala.concurrent.ExecutionContext.global

object Server {

  def stream[F[_]: ConcurrentEffect](
      config: AppConfig
  )(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      redisResource = Redis[F].utf8(config.redis.url)
      ratesProcessor = Rates.impl[F](client, redisResource)
      httpApp = Routes.rateRoutes[F](ratesProcessor).orNotFound
      finalHttpApp = GZip(Logger.httpApp(logHeaders = true, logBody = true)(httpApp))
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(config.port.number, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

  implicit def logInstance[F[_]: Sync]: Log[F] = {
    val underlying = Slf4jLogger.getLogger[F]
    new Log[F] {
      override def debug(msg: => String): F[Unit] = underlying.debug(msg)

      override def error(msg: => String): F[Unit] = underlying.error(msg)

      override def info(msg: => String): F[Unit] = underlying.info(msg)
    }
  }
}
