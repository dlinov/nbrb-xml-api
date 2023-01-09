package io.github.dlinov.nbrbxmlapi

import java.time.{LocalDate, Month}
import cats.effect.IO
import io.github.dlinov.nbrbxmlapi.sources.Myfinby
import munit.CatsEffectSuite
import org.http4s.blaze.client.BlazeClientBuilder

class RatesSpec extends CatsEffectSuite {
  // TODO: mock response after decoupling source from http client
  test("myfin.by") {
    BlazeClientBuilder[IO].resource.use { client =>
      val expected = CurrencyRate(1L, "usd", 1L, 2.5061)
      val source = new Myfinby[IO](client)
      val obtained = source.get("USD", LocalDate.of(2022, Month.DECEMBER, 15))

      assertIO(obtained, expected)
    }
  }
}
