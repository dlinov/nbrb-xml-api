package io.github.dlinov.nbrbxmlapi.caches

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import cats.Functor
import cats.effect.{MonadCancel, Resource}
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effects.SetArgs
import dev.profunktor.redis4cats.effects.SetArg.Ttl
import io.github.dlinov.nbrbxmlapi.CurrencyRate
import io.github.dlinov.nbrbxmlapi.caches.serde.Serde

import scala.concurrent.duration.FiniteDuration

// TODO: make it scala-3-kind-projector-compatible syntax instead of implicit
// see: https://docs.scala-lang.org/scala3/guides/migration/plugin-kind-projector.html
class RedisRatesCache[F[_] /*: MonadCancel[*[_], Throwable]*/ ](
    redisResource: Resource[F, RedisCommands[F, String, String]],
    serde: Serde[CurrencyRate, String]
)(implicit mc: MonadCancel[F, Throwable])
    extends RatesCache[F] {

  def get(currencyCode: String, date: LocalDate): F[Option[CurrencyRate]] = {
    val key = makeKey(currencyCode, date)
    val cachedItem = redisResource.use(_.get(key))
    Functor[F].map(cachedItem)(_.flatMap(serde.deserialize))
  }

  def set(
      currencyCode: String,
      date: LocalDate,
      value: CurrencyRate,
      ttl: FiniteDuration
  ): F[Boolean] = {
    val key = makeKey(currencyCode, date)
    val serializedValue = serde.serialize(value)
    redisResource.use(_.set(key, serializedValue, SetArgs(Ttl.Ex(ttl))))
  }

  private def makeKey(currencyCode: String, date: LocalDate): String = {
    val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val currKey = currencyCode.toLowerCase
    s"$currKey-$dateKey"
  }

}
