package io.github.dlinov.nbrbxmlapi.caches.serde

import io.circe.parser.decode
import io.circe.syntax._
import io.circe.Codec
import io.github.dlinov.nbrbxmlapi.CurrencyRate

class CirceSerde[A: Codec] extends Serde[A, String] {

  override def serialize(value: A): String = value.asJson.noSpaces

  override def deserialize(json: String): Option[A] = decode[A](json).toOption

}

object CirceSerde {
  import io.github.dlinov.nbrbxmlapi.codecs.CirceCodecs._

  val currencyRate = new CirceSerde[CurrencyRate]
}
