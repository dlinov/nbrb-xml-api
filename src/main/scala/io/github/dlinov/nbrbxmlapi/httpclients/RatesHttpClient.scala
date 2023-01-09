package io.github.dlinov.nbrbxmlapi.httpclients

import java.time.LocalDate

trait RatesHttpClient[F[_], A] {

  def get(url: String): F[A]

}
