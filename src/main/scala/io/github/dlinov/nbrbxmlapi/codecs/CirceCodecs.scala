package io.github.dlinov.nbrbxmlapi.codecs

import io.circe.{Decoder, Encoder}
import io.github.dlinov.nbrbxmlapi.CurrencyRate
import io.circe.Codec

object CirceCodecs {
  implicit val crDecoder: Decoder[CurrencyRate] =
    Decoder
      .forProduct4(
        "Cur_ID",
        "Cur_Abbreviation",
        "Cur_Scale",
        "Cur_OfficialRate"
      )(CurrencyRate.apply)

  implicit val crEncoder: Encoder[CurrencyRate] =
    Encoder
      .forProduct4(
        "Cur_ID",
        "Cur_Abbreviation",
        "Cur_Scale",
        "Cur_OfficialRate"
      )(cr => (cr.id, cr.abbreviation, cr.scale, cr.rate))

  implicit val crCodec: Codec[CurrencyRate] = Codec.from(crDecoder, crEncoder)
}
