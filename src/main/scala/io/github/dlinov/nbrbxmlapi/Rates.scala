package io.github.dlinov.nbrbxmlapi

import java.time.LocalDate

import cats.Monad
import cats.data.OptionT
import cats.effect.{Async, Concurrent, Resource, Sync}
import cats.syntax.option._
import cats.syntax.flatMap._
import cats.syntax.functor._
import dev.profunktor.redis4cats.RedisCommands
import io.github.dlinov.nbrbxmlapi.caches._
import io.github.dlinov.nbrbxmlapi.httpclients._
import io.github.dlinov.nbrbxmlapi.parsers._
import io.github.dlinov.nbrbxmlapi.sources._
import org.http4s.client.Client
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

trait Rates[F[_]] {
  def exchangeRate(currencyCode: String, date: LocalDate): F[CurrencyRate]
}

object Rates {
  implicit def apply[F[_]](implicit ev: Rates[F]): Rates[F] = ev

  def genericImpl[F[_]: Monad](
      cache: RatesCache[F],
      source: RatesSource[F]
  ): Rates[F] = new Rates[F] {
    // TODO: use some F-logger
    private val logger = LoggerFactory.getLogger(getClass)
    private val cacheTtl = 60.days

    override def exchangeRate(
        currencyCode: String,
        date: LocalDate
    ): F[CurrencyRate] = {
      for {
        maybeCached <- cache.get(currencyCode, date)
        rate <- OptionT.fromOption(maybeCached).getOrElseF {
          for {
            fetchedRate <- source.get(currencyCode, date)
            _ <- cache.set(currencyCode, date, fetchedRate, cacheTtl) // TODO: fire-and-forget here
          } yield fetchedRate
        }
      } yield rate
    }
  }
}
