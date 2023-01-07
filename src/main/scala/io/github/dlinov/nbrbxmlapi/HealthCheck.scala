package io.github.dlinov.nbrbxmlapi

import cats.effect.{Async, Resource}
import dev.profunktor.redis4cats.RedisCommands

trait HealthCheck[F[_]] {
  def ping(): F[String]
}

object HealthCheck {
  implicit def apply[F[_]](implicit ev: HealthCheck[F]): HealthCheck[F] = ev
  
  def impl[F[_]: Async](
      redis: Resource[F, RedisCommands[F, String, String]]
  ): HealthCheck[F] =
    new HealthCheck[F] {
      override def ping(): F[String] = redis.use(_.ping)
    }
}
