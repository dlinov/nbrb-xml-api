package io.github.dlinov.nbrbxmlapi.parsers

import io.github.dlinov.nbrbxmlapi.CurrencyRate

class MyfinParser extends RatesParser {
  import RatesParser._

  override def parse(input: String): Either[ParserError, CurrencyRate] = ???

}
