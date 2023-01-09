package io.github.dlinov.nbrbxmlapi

final case class CurrencyRate(
    id: Long,
    abbreviation: String,
    scale: Long,
    rate: BigDecimal
)
