package io.github.dlinov.nbrbxmlapi

import java.time.LocalDate
import cats.Monad
import cats.data.OptionT
import cats.effect.{Async, Concurrent, Resource, Sync}
import cats.syntax.option.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import dev.profunktor.redis4cats.RedisCommands
import io.github.dlinov.nbrbxmlapi.caches.*
import io.github.dlinov.nbrbxmlapi.httpclients.*
import io.github.dlinov.nbrbxmlapi.parsers.*
import io.github.dlinov.nbrbxmlapi.sources.*
import org.http4s.client.Client
import org.slf4j.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.*

trait Rates[F[_]] {
  def exchangeRate(currencyCode: String, date: LocalDate): F[CurrencyRate]
}

object Rates {
  implicit def apply[F[_]](implicit ev: Rates[F]): Rates[F] = ev

  def genericImpl[F[_]: Sync](
      cache: RatesCache[F],
      source: RatesSource[F]
  ): Rates[F] = new Rates[F] {
    private val cacheTtl = 60.days

    override def exchangeRate(
        currencyCode: String,
        date: LocalDate
    ): F[CurrencyRate] = {
      for {
        logger <- Slf4jLogger.create[F]
        maybeCached <- cache.get(currencyCode, date)
        maybeCachedT = OptionT.fromOption(maybeCached)
        _ <- maybeCachedT
          .foldF {
            logger.info(s"There is no cached rate for $currencyCode")
          } { rate =>
            logger.info(s"Loaded rate $rate for $currencyCode from cache")
          }
        rate <- maybeCachedT.getOrElseF {
          for {
            fetchedRate <- source.get(currencyCode, date)
            _ <- logger.info(s"Fetched rate $fetchedRate for $currencyCode online")
            _ <- cache.set(currencyCode, date, fetchedRate, cacheTtl) // TODO: fire-and-forget here
          } yield fetchedRate
        }
      } yield rate
    }
  }
}
