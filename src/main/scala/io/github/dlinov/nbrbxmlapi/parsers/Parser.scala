package io.github.dlinov.nbrbxmlapi.parsers

import io.github.dlinov.nbrbxmlapi.Rates.CurrencyRate

trait RatesParser {
  import RatesParser._

  def parse(input: String): Either[ParserError, CurrencyRate]
}

object RatesParser {
  trait ParserError {
    def message: String
  }
}
