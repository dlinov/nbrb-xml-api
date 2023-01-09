package io.github.dlinov.nbrbxmlapi.codecs

import io.github.dlinov.nbrbxmlapi.CurrencyRate
import org.http4s.EntityEncoder
import org.http4s.scalaxml._

trait Http4sEntityEncoders {

  implicit def crXmlEncoder[F[_]]: EntityEncoder[F, CurrencyRate] = {
    xmlEncoder[F].contramap[CurrencyRate] { cr =>
      <rate>
        <id>{cr.id}</id>
        <abbreviation>{cr.abbreviation}</abbreviation>
        <scale>{cr.scale}</scale>
        <officialRate>{cr.rate}</officialRate>
      </rate>
    }
  }

}
