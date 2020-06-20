import java.time.LocalDate

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import scala.util.Try

object Routes {

  def rateRoutes[F[_]: Sync](H: Rates[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "rates" / currency / LocalDateVar(date) =>
        for {
          rate <- H.exchangeRate(currency, date)
          resp <- Ok(rate)
        } yield resp
    }
  }

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      Option.unless(str.isEmpty)(Try(LocalDate.parse(str)).toOption).flatten
    }
  }
}
