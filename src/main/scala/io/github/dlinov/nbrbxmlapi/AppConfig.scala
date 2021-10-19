package io.github.dlinov.nbrbxmlapi

final case class Port(number: Int) extends AnyVal

final case class RedisConfig(url: String) {
  def canonicalUri: String = {
    if (url.startsWith("redis://") || url.startsWith("rediss://")) { // heroku format
      url
    } else { // azure format - host:port,password=<..>,ssl=True,abortConnect=False
      val urlParts = url.split(',')
      val hostAndPort = urlParts(0)
      val password = urlParts
        .find(_.startsWith("password="))
        .map(_.substring("password=".length))
        .getOrElse("")
      val useSsl = urlParts
        .find(_.startsWith("ssl="))
        .flatMap(_.substring("ssl=".length).toBooleanOption)
        .getOrElse(true)
      s"redis${if (useSsl) "s" else ""}://:$password@$hostAndPort"
    }
  }
}

final case class AppConfig(port: Port, redis: RedisConfig)
