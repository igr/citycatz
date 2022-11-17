package citiz

import cats.effect.Sync
import citiz.model.{GeoCityDetails, GetCityData}
import sttp.client3.*
import sttp.client3.circe.*

trait FetchCityDetails[F[_]] {

  def fetchDetails(cityId: String): F[Either[ResponseException[String, io.circe.Error], GeoCityDetails]]

}

class HttpFetchCityDetails[F[_]: Sync] extends FetchCityDetails[F] {

  def fetchDetails(cityId: String): F[Either[ResponseException[String, io.circe.Error], GeoCityDetails]] = {
    val request = basicRequest.get(uri"http://geodb-free-service.wirefreethought.com/v1/geo/cities/$cityId")
    val backend = HttpClientSyncBackend()

    import citiz.model.AppJsonCodecs.*

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
