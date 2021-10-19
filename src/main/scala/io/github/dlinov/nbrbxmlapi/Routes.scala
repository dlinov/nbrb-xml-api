package io.github.dlinov.nbrbxmlapi

import java.time.LocalDate

import cats.data.EitherT
import cats.effect.Sync
import cats.syntax.flatMap._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.{EntityEncoder, HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl

import scala.util.Try

object Routes {

  def rateRoutes[F[_]: Sync](H: Rates[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    val logger = Slf4jLogger.getLogger[F]
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / "rates" / currency / LocalDateVar(date) =>
      wrapServiceCall(dsl, logger, () => H.exchangeRate(currency, date))
    }
  }

  private def wrapServiceCall[F[_]: Sync, A](
      dsl: Http4sDsl[F],
      logger: Logger[F],
      call: () => F[Either[Throwable, A]]
  )(implicit ee: EntityEncoder[F, A]): F[Response[F]] = {
    import dsl._
    (for {
      callResult <- EitherT(call())
      _ <- EitherT.right[Throwable](logger.info(s"Call result is '$callResult'"))
    } yield Ok(callResult))
      .leftMap { err =>
        logger.warn(err)("Failed request to nbrb API: ") >>
          InternalServerError(err.getMessage)
      }
      .merge
      .flatten
  }

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      Option.unless(str.isEmpty)(Try(LocalDate.parse(str)).toOption).flatten
    }
  }
}
