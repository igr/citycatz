package citiz

import cats.effect.Sync
import sttp.client3.*
import sttp.client3.circe.*

case class GeoCityDetails(name: String,
                          country: String,
                          countryCode: String,
                          region: String,
                          regionCode: String,
                          elevationMeters: Option[Int],
                          latitude: Double,
                          longitude: Double,
                          population: Int,
                          timezone: String,
                          deleted: Boolean)

case class GetCityData(data: GeoCityDetails)

trait FetchCityDetails[F[_]] {

  def fetchDetails(cityId: String): F[Either[ResponseException[String, io.circe.Error], GeoCityDetails]]

}

class HttpFetchCityDetails[F[_]: Sync] extends FetchCityDetails[F] {

  def fetchDetails(cityId: String): F[Either[ResponseException[String, io.circe.Error], GeoCityDetails]] = {
    val request = basicRequest.get(uri"http://geodb-free-service.wirefreethought.com/v1/geo/cities/$cityId")
    val backend = HttpClientSyncBackend()

    import io.circe.generic.auto.*

    Sync[F].delay {

      val response: Identity[Response[Either[ResponseException[String, io.circe.Error], GeoCityDetails]]] =
        request
          .response(asJson[GetCityData])
          .mapResponseRight(_.data)
          .send(backend)

      response.body
    }
  }

}

object HttpFetchCityDetails {
  def apply[F[_]: Sync]: HttpFetchCityDetails[F] = new HttpFetchCityDetails[F]
}
