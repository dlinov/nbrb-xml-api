package io.github.dlinov.nbrbxmlapi.sources

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import cats.effect.Concurrent
import io.github.dlinov.nbrbxmlapi.CurrencyRate
import io.github.dlinov.nbrbxmlapi.parsers.{MyfinParser, RatesParser}
import org.http4s.EntityDecoder
import org.http4s.client.Client

final class Myfinby[F[_]: Concurrent](
    httpClient: Client[F]
) extends RatesHttpSource[F] {

  override def get(currencyCode: String, date: LocalDate): F[CurrencyRate] = {
    val uri = makeFinalUri(currencyCode, date)
    httpClient.get(uri)(_.as[CurrencyRate])
  }

  private val baseUri: String = "https://myfin.by"
  private implicit val crEntityDecoder: EntityDecoder[F, CurrencyRate] =
    EntityDecoder.text.map(parse)

  private def makeFinalUri(currencyCode: String, date: LocalDate): String = {
    val currKey = currencyCode.toLowerCase
    val dateKey = date.format(Myfinby.dateFormatter)
    s"$baseUri/bank/kursy_valjut_nbrb/$currKey/$dateKey"
  }

  private def parse(html: String): CurrencyRate = ???

}

object Myfinby {
  private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
}
