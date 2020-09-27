import java.time.LocalDate
import java.time.format.DateTimeFormatter

import cats.effect.{Resource, Sync}
import cats.syntax.either._
import cats.syntax.option._
import cats.syntax.flatMap._
import cats.syntax.functor._
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effects.SetArg.Ttl
import dev.profunktor.redis4cats.effects.SetArgs
import io.circe.{Decoder => JsonDecoder, Encoder => JsonEncoder}
import io.circe.parser.decode
import io.circe.syntax._
import org.http4s.Uri._
import org.http4s.{EntityDecoder, EntityEncoder, Query, Uri}
import org.http4s.circe._
import org.http4s.client.Client
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

trait Rates[F[_]] {
  def exchangeRate(currencyCode: String, date: LocalDate): F[Either[Exception, Rates.CurrencyRate]]
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
    implicit def crXmlEncoder[F[_]]: EntityEncoder[F, CurrencyRate] = {
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
    implicit val crDecoder: JsonDecoder[CurrencyRate] =
      JsonDecoder
        .forProduct4(
          "Cur_ID",
          "Cur_Abbreviation",
          "Cur_Scale",
          "Cur_OfficialRate"
        )(CurrencyRate.apply)
    implicit val crEncoder: JsonEncoder[CurrencyRate] =
      JsonEncoder
        .forProduct4(
          "Cur_ID",
          "Cur_Abbreviation",
          "Cur_Scale",
          "Cur_OfficialRate"
        )(cr => (cr.id, cr.abbreviation, cr.scale, cr.rate))
    implicit def crEntityDecoder[F[_]: Sync]: EntityDecoder[F, CurrencyRate] =
      jsonOf[F, CurrencyRate]
  }

  def impl[F[_]: Sync](
      C: Client[F],
      redis: Resource[F, RedisCommands[F, String, String]]
  ): Rates[F] =
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
      private val cacheTtl = SetArgs(Ttl.Ex(14.days))

      override def exchangeRate(
          currencyCode: String,
          date: LocalDate
      ): F[Either[Exception, CurrencyRate]] = {
        redis.use { RC =>
          val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
          val currKey = currencyCode.toLowerCase
          val key = s"$currKey-$dateKey"
          for {
            maybeCached <- RC.get(key)
            rate <- maybeCached.fold {
              val uri = baseUri
                .addPath(s"$currKey")
                .withQueryParam("ondate", dateKey)
              for {
                _ <- Sync[F].delay(logger.debug(s"Making request for $key..."))
                fetchedRate <- C.get(uri)(_.as[CurrencyRate])
                _ <- RC.set(key, fetchedRate.asJson.noSpaces, cacheTtl)
              } yield fetchedRate.asRight[Exception]
            } { cachedRateJson =>
              for {
                _ <- Sync[F].delay(logger.debug(s"Using cached result for $key"))
                rate <- Sync[F].delay(decode[CurrencyRate](cachedRateJson))
              } yield rate
            }
          } yield rate
        }
      }
    }
}
