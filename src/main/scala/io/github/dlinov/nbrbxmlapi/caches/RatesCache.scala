package io.github.dlinov.nbrbxmlapi.caches

import java.time.LocalDate
import io.github.dlinov.nbrbxmlapi.CurrencyRate

import scala.concurrent.duration.FiniteDuration

trait RatesCache[F[_]] {

  def get(currencyCode: String, date: LocalDate): F[Option[CurrencyRate]]

  def set(
      currencyCode: String,
      date: LocalDate,
      value: CurrencyRate,
      ttl: FiniteDuration
  ): F[Boolean]

}
