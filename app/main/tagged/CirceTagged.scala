package tagged

import io.circe.*
import tagged.*

object CirceTagged {
  def taggedDecoder[T: Decoder, U]: Decoder[T @@ U] =
    Decoder.instance(_.as[T].@@@[U])

  def taggedTypeDecoder[T: Decoder](taggedType: TaggedType[T]): Decoder[taggedType.Type] =
    taggedDecoder[T, taggedType.Tag]

  def taggedEncoder[T: Encoder, U]: Encoder[T @@ U] =
    Encoder[T].@@@[U]

  def taggedTypeEncoder[T: Encoder](taggedType: TaggedType[T]): Encoder[taggedType.Type] =
    taggedEncoder[T, taggedType.Tag]
}
