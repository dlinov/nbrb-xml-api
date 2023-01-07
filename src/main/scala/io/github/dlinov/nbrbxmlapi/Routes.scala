package io.github.dlinov.nbrbxmlapi

import java.time.LocalDate
import cats.data.EitherT
import cats.effect.Sync
import cats.syntax.flatMap._
import org.http4s.{EntityEncoder, HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.util.Try

trait Routes[F[_]: Sync] {
  def apiRoutes(
      rates: Rates[F],
      healthCheck: HealthCheck[F]
    ): HttpRoutes[F]
}

object Routes {
  def impl[F[_]: Sync] = new Routes[F] {
    private implicit val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
    private val logger = Slf4jLogger.getLogger[F]

    override def apiRoutes(
      rates: Rates[F],
      healthCheck: HealthCheck[F]
    ): HttpRoutes[F] = {
      import dsl._
      HttpRoutes.of[F] {
        case GET -> Root / "ping" =>
          wrapServiceCall(Sync[F].attempt(healthCheck.ping()))
        case GET -> Root / "rates" / currency / LocalDateVar(date) =>
          wrapServiceCall(rates.exchangeRate(currency, date))
      }
    }

    private def wrapServiceCall[A](
      call: F[Either[Throwable, A]]
    )(implicit ee: EntityEncoder[F, A], dsl: Http4sDsl[F]): F[Response[F]] = {
      import dsl._
      
      // Sync[F].handleError {
      //   Sync[F].map(call){ result -> 
      //     logger.info(s"Call result is '$result'") >> Ok(result)
      //   }
      // } { err =>
      //     logger.warn(err)("Failed request to nbrb API: ") >>
      //       InternalServerError(err.getMessage)
      // }.flatten
      
      (for {
        callResult <- EitherT(call)
        _ <- EitherT.right[Throwable](logger.info(s"Call result is '$callResult'"))
      } yield Ok(callResult))
        .leftMap { err =>
          logger.warn(err)("Failed request to nbrb API: ") >>
            InternalServerError(err.getMessage)
        }
        .merge
        .flatten
    }
  }

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      Option.unless(str.isEmpty)(Try(LocalDate.parse(str)).toOption).flatten
    }
  }
}
