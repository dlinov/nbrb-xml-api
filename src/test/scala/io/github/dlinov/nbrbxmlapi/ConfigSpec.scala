package io.github.dlinov.nbrbxmlapi

import cats.syntax.either._
import pureconfig.ConfigSource

class ConfigSpec extends munit.FunSuite {
  test("AppConfig") {
    val expected = AppConfig(23456, RedisConfig("redis://localhost")).asRight[String]
    val obtained = AppConfig.loadDefault

    assertEquals(obtained, expected)
  }
}
