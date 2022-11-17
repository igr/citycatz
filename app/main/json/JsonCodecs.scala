package json

import cats.Show
import enumeratum.*
import io.circe.*
import io.circe.syntax.*
import tagged.auto.typeclass.*

import java.util.UUID

trait JsonCodecs
  extends JsonCodecs0 {

  // common types

  implicit val javaBigDecimalCodec: Codec[java.math.BigDecimal] =
    Codec.from(
      _.as[BigDecimal].map(_.bigDecimal),
      value => Json.fromBigDecimal(BigDecimal(value))
    )

  implicit val uuidCodec: Codec[UUID] =
    Codec.from(
      _.as[String].map(UUID.fromString),
      uuid => Json.fromString(uuid.toString)
    )

  def mapObjectEncoder[K: Show, V: Encoder]: Encoder[Map[K, V]] =
    Encoder.instance { map =>
      Json.obj(map.map({ case (k, v) => (Show[K].show(k), v.asJson) }).toSeq*)
    }

  def mapObjectDecoder[K, V: Decoder](keyFromString: String => K): Decoder[Map[K, V]] =
    Decoder[Map[String, V]].map {
      _.map { case (k, v) => (keyFromString(k), v) }
    }
}

trait JsonCodecs0 {

  def enumEncoder[T <: EnumEntry]: Encoder[T] =
    Encoder.instance(t => Json.fromString(t.entryName))

  def enumDecoder[T <: EnumEntry](`enum`: Enum[T]): Decoder[T] =
    Decoder.instance(_.as[String].map(`enum`.withName))

  def enumCodec[T <: EnumEntry](`enum`: enumeratum.Enum[T]): Codec[T] =
    Codec.from(enumDecoder(`enum`), enumEncoder)

}

object JsonCodecs extends JsonCodecs
