package io.github.dlinov.nbrbxmlapi.sources

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import cats.effect.Concurrent
import io.github.dlinov.nbrbxmlapi.CurrencyRate
import io.github.dlinov.nbrbxmlapi.parsers.{MyfinParser, RatesParser}
import org.http4s.EntityDecoder
import org.http4s.client.Client
import org.jsoup.Jsoup
import org.jsoup.select.Evaluator.AttributeWithValue

import scala.jdk.CollectionConverters.ListHasAsScala

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

  private def parse(html: String): CurrencyRate = {
    val doc = Jsoup.parse(html)
    // <meta  property="og:url" content="https://myfin.by/bank/kursy_valjut_nbrb/usd/15-12-2022" />
    val url = doc
      .head()
      .getElementsByTag("meta")
      .asScala
      .find(elem => elem.attr("property") == "og:url")
      .getOrElse(throw new RuntimeException("parsing failed!"))
      .attr("content")
    val urlParts = url.split('/').takeRight(2)
    val currencyCode = urlParts(0)
    val date = LocalDate.parse(urlParts(1), Myfinby.dateFormatter)
    // document.getElementsByClassName('')[0].innerText
    // "2.5061"
    val rate = doc.select(".cur-rate__value").first().text()
    CurrencyRate(1L, currencyCode, 1L, BigDecimal(rate))
  }

}

object Myfinby {
  private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
}
