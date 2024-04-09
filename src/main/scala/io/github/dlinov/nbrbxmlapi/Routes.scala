package io.github.dlinov.nbrbxmlapi

import java.time.LocalDate
import cats.effect.Sync
import cats.syntax.flatMap.*
import io.github.dlinov.nbrbxmlapi.codecs.Http4sEntityEncoders
import org.http4s.{EntityEncoder, HttpRoutes, Response, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.util.Try

trait Routes[F[_]: Sync] {
  def apiRoutes(
      rates: Rates[F],
      healthCheck: HealthCheck[F]
  ): HttpRoutes[F]

  def staticRoutes: HttpRoutes[F]
}

object Routes {
  def impl[F[_]: Sync]: Routes[F] & Http4sEntityEncoders = new Routes[F] with Http4sEntityEncoders {
    private implicit val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
    private val logger = Slf4jLogger.getLogger[F]

    override def apiRoutes(
        rates: Rates[F],
        healthCheck: HealthCheck[F]
    ): HttpRoutes[F] = {
      import dsl._
      HttpRoutes.of[F] {
        case GET -> Root / "ping" =>
          wrapServiceCall(healthCheck.ping())
        case GET -> Root / "rates" / currency / LocalDateVar(date) =>
          wrapServiceCall(rates.exchangeRate(currency, date))
      }
    }

    override def staticRoutes: HttpRoutes[F] = {
      import dsl._
      HttpRoutes.of[F] { case GET -> Root / "favicon.ico" =>
        StaticFile.fromResource("favicon.ico").getOrElseF(NotFound())
      }
    }

    private def wrapServiceCall[A](
        call: F[A]
    )(implicit ee: EntityEncoder[F, A], dsl: Http4sDsl[F]): F[Response[F]] = {
      import dsl._

      Sync[F].handleError {
        Sync[F].map(call) { result =>
          logger.info(s"Call result is '$result'") >>
            Ok(result)
        }
      } { err =>
        logger.warn(err)("Failed request to nbrb API: ") >>
          InternalServerError(err.getMessage)
      }.flatten
    }
  }

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      Option.unless(str.isEmpty)(Try(LocalDate.parse(str)).toOption).flatten
    }
  }
}
