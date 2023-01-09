package io.github.dlinov.nbrbxmlapi.sources

import java.time.LocalDate
import io.github.dlinov.nbrbxmlapi.CurrencyRate
import io.github.dlinov.nbrbxmlapi.parsers.RatesParser
import io.github.dlinov.nbrbxmlapi.httpclients.RatesHttpClient

// TODO: uncomment things below and decouple amy RatesSource from http4s http client
// by using pair of RatesCient and RatesParser
trait RatesHttpSource[F[_]] extends RatesSource[F] {

//   protected def httpClient: RatesHttpClient[F, CurrencyRate]

//   protected def parser: RatesParser

//   protected def makeFinalUri(currencyCode: String, date: LocalDate): String

}
