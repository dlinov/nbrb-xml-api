import java.net.URL

final case class Port(number: Int) extends AnyVal

final case class RedisConfig(url: String)

final case class AppConfig(port: Port, redis: RedisConfig)
