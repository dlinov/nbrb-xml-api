package io.github.dlinov.nbrbxmlapi.parsers

import io.github.dlinov.nbrbxmlapi.Rates.CurrencyRate

class NbrbXmlParser extends RatesParser {
  import RatesParser._

  override def parse(input: String): Either[ParserError, CurrencyRate] =
    throw new NotImplementedError("not ready")
}
