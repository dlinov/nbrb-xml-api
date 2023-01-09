package io.github.dlinov.nbrbxmlapi.sources

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import cats.effect.Async
import cats.syntax.option._
import io.github.dlinov.nbrbxmlapi.CurrencyRate
import io.github.dlinov.nbrbxmlapi.codecs.CirceCodecs.crDecoder
import org.http4s._
import org.http4s.Uri._
import org.http4s.circe._
import org.http4s.client.Client
import org.slf4j.LoggerFactory

final class Nbrbby[F[_]: Async](
    httpClient: Client[F]
) extends RatesHttpSource[F] {

  private val logger = LoggerFactory.getLogger(getClass)
  private val baseUri = Uri(
    scheme = Scheme.https.some,
    authority = Authority(
      host = RegName("www.nbrb.by")
    ).some,
    path = Uri.Path.unsafeFromString("/api/exrates/rates"),
    query = Query.fromPairs(
      "parammode" -> "2" // "2" is to use currency codes
    )
  )

  override def get(currencyCode: String, date: LocalDate): F[CurrencyRate] = {
    val uri = makeFinalUri(currencyCode, date)
    logger.info(s"Making request for $uri...")
    httpClient.get(uri)(_.as[CurrencyRate])
  }

  private implicit def crEntityDecoder: EntityDecoder[F, CurrencyRate] =
    jsonOf[F, CurrencyRate]

  private def makeFinalUri(currencyCode: String, date: LocalDate): Uri = {
    val currKey = currencyCode.toLowerCase
    val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val key = s"$currKey-$dateKey"
    baseUri
      .addPath(currKey)
      .withQueryParam("ondate", dateKey)
  }

}
