package citiz.model

import citiz.GeoCityDetails
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import json.JsonCodecs
import parser.*
import tagged.auto.typeclass.*

object AppJsonCodecs extends JsonCodecs {

  implicit lazy val cityEncoder: Encoder[City] = deriveEncoder
  implicit lazy val geoCityDetailsEncoder: Encoder[GeoCityDetails] = deriveEncoder

//  implicit lazy val cityNameEncoder: Encoder[CityName @@ String] = taggedEncoder[CityName, String]
  implicit val cityTagEncoder: Encoder[CityTag] = enumEncoder

  implicit lazy val cityDataEncoder: Encoder[CityData] = cityData => Json.obj(
    "name" -> cityData.name.asJson,
    "country" -> cityData.country.asJson,
    "population" -> cityData.population.asJson,
    "tags" -> cityData.tags.asJson,
    "params" -> cityData.params.asJson,
  )
}
