import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import fs2.Stream
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
      ratesAlg = Rates.impl[F](client)
      httpApp = (
          Routes.rateRoutes[F](ratesAlg)
      ).orNotFound
      finalHttpApp = GZip(Logger.httpApp(logHeaders = true, logBody = true)(httpApp))
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(config.port.number, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
