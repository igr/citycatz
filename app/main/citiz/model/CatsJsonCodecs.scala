package citiz.model

import io.circe.*
import io.circe.syntax.*

object CatsJsonCodecs {

  implicit def eitherEncoder[A, B](implicit a: Encoder[A],
                                            b: Encoder[B]): Encoder[Either[A, B]] =
    Encoder.instance[Either[A, B]](_.fold(_.asJson, _.asJson))

  implicit def eitherDecoder[L: Decoder, R: Decoder]: Decoder[L Either R] =
    (c: HCursor) => c.as[L] match {
      case Right(l) => Right(Left(l))
      case _ => c.as[R].map(Right(_))
    }

}
