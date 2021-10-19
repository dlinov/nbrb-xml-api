package io.github.dlinov.nbrbxmlapi

import cats.effect.{Async, Sync}
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log
import fs2.Stream
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.{GZip, Logger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Server {

  def stream[F[_]: Async](
      config: AppConfig
  ): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F].stream
      redisUri = config.redis.canonicalUri
      redisResource = Redis[F].utf8(redisUri)
      ratesProcessor = Rates.impl[F](client, redisResource)
      httpApp = Routes.rateRoutes[F](ratesProcessor).orNotFound
      finalHttpApp = GZip(Logger.httpApp(logHeaders = true, logBody = true)(httpApp))
      exitCode <- BlazeServerBuilder[F]
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
