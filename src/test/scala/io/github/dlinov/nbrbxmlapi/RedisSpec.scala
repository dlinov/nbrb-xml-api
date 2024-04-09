package io.github.dlinov.nbrbxmlapi

import cats.effect.IO
import cats.syntax.option._
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout._
import munit.CatsEffectSuite

class RedisSpec extends CatsEffectSuite {
  test("Azure Redis cache connection string") {
    val config = RedisConfig("redis://localhost:6379/0")
    val key = "test-key"
    val expected = "test-value"
    val obtained = Redis[IO]
      .utf8(config.connectionString)
      .use { redis =>
        for {
          _ <- redis.ping
          _ <- redis.set(key, expected)
          fetched <- redis.get(key)
        } yield fetched
      }

    assertIO(obtained, expected.some)
  }
}
