package io.github.dlinov.nbrbxmlapi.sources

import io.github.dlinov.nbrbxmlapi.CurrencyRate
import java.time.LocalDate

trait RatesSource[F[_]] {

  def get(currencyCode: String, date: LocalDate): F[CurrencyRate]

}
