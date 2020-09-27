package io.github.dlinov.nbrbxmlapi

import java.time.LocalDate

import cats.data.EitherT
import cats.effect.Sync
import cats.syntax.flatMap._
import org.http4s.{EntityEncoder, HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl

import scala.util.Try

object Routes {

  def rateRoutes[F[_]: Sync](H: Rates[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / "rates" / currency / LocalDateVar(date) =>
      wrapServiceCall(dsl, () => H.exchangeRate(currency, date))
    }
  }

  private def wrapServiceCall[F[_]: Sync, A](
      dsl: Http4sDsl[F],
      call: () => F[Either[Exception, A]]
  )(implicit ee: EntityEncoder[F, A]): F[Response[F]] = {
    import dsl._
    (for {
      callResult <- EitherT(call())
    } yield Ok(callResult))
      .leftMap(err => InternalServerError(err.getMessage))
      .merge
      .flatten
  }

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      Option.unless(str.isEmpty)(Try(LocalDate.parse(str)).toOption).flatten
    }
  }
}
