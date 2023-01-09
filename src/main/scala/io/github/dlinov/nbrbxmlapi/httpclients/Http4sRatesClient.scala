package io.github.dlinov.nbrbxmlapi.httpclients

import java.time.LocalDate
import cats.MonadThrow
import org.http4s.{EntityDecoder, Uri}
import org.http4s.client.Client

class Http4sRatesClient[
    F[_]: MonadThrow,
    A: ({ type L[x] = EntityDecoder[F, x] })#L
](
    underlying: Client[F]
) extends RatesHttpClient[F, A] {

  override def get(url: String): F[A] = underlying.get(url)(_.as[A])

}
