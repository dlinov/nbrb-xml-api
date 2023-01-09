package io.github.dlinov.nbrbxmlapi.parsers

import io.github.dlinov.nbrbxmlapi.CurrencyRate

class NbrbXmlParser extends RatesParser {
  import RatesParser._

  override def parse(input: String): Either[ParserError, CurrencyRate] = ???
}
