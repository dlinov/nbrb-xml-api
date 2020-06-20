import java.time.LocalDate
import java.time.format.DateTimeFormatter

import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import io.circe.{Decoder => JsonDecoder}
import org.http4s.Uri._
import org.http4s.{EntityDecoder, EntityEncoder, Query, Uri}
import org.http4s.circe._
import org.http4s.client.Client
import org.slf4j.LoggerFactory

trait Rates[F[_]] {
  def exchangeRate(currencyCode: String, date: LocalDate): F[Rates.CurrencyRate]
}

object Rates {
  implicit def apply[F[_]](implicit ev: Rates[F]): Rates[F] = ev

  final case class CurrencyRate(
      id: Long,
      abbreviation: String,
      scale: Long,
      rate: BigDecimal
  )
  object CurrencyRate {
    implicit def crEncoder[F[_]: Sync]: EntityEncoder[F, CurrencyRate] = {
      import org.http4s.scalaxml._
      xmlEncoder[F].contramap[CurrencyRate] { cr =>
        <rate>
          <id>{cr.id}</id>
          <abbreviation>{cr.abbreviation}</abbreviation>
          <scale>{cr.scale}</scale>
          <officialRate>{cr.rate}</officialRate>
        </rate>
      }
    }
    implicit def crDecoder: JsonDecoder[CurrencyRate] =
      JsonDecoder
        .forProduct4(
          "Cur_ID",
          "Cur_Abbreviation",
          "Cur_Scale",
          "Cur_OfficialRate"
        )(CurrencyRate.apply)
    implicit def crEntityDecoder[F[_]: Sync]: EntityDecoder[F, CurrencyRate] =
      jsonOf[F, CurrencyRate]
  }

  def impl[F[_]: ConcurrentEffect](C: Client[F]): Rates[F] =
    new Rates[F] {
      private val logger = LoggerFactory.getLogger(getClass)
      private val baseUri = Uri
        .unsafeFromString("https://www.nbrb.by/api/exrates/rates")
        .withQueryParam("parammode", "2")
      private val baseUri2 = Uri(
        scheme = Scheme.https.some,
        authority = Authority(
          host = RegName("www.nbrb.by")
        ).some,
        path = s"api/exrates/rates",
        query = Query.fromPairs(
          "parammode" -> "2" // "2" is to use currency codes
        )
      )

      override def exchangeRate(currencyCode: String, date: LocalDate): F[CurrencyRate] = {
        logger.info(s"${baseUri == baseUri2}")
        val uri = baseUri
          .addPath(s"${currencyCode.toLowerCase}")
          .withQueryParam("ondate", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        C.get(uri)(_.as[CurrencyRate])
      }
    }
}
