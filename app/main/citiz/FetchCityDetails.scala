package citiz

import cats.effect.*
import cats.implicits.*
import citiz.model.{GeoCityDetails, GetCityData}
import sttp.client3.*
import sttp.client3.armeria.cats.ArmeriaCatsBackend
import sttp.client3.circe.*

/** API client for fetching city data. */
trait FetchCityDetails[F[_]] {

  def fetchDetails(cityId: String): F[Either[ResponseException[String, io.circe.Error], GeoCityDetails]]

  protected def getCityRequest(cityId: String) = basicRequest.get(uri"http://geodb-free-service.wirefreethought.com/v1/geo/cities/$cityId")

}

/** Sync version of the API client. */
class HttpFetchCityDetails[F[_]: Sync] extends FetchCityDetails[F] {

  def fetchDetails(cityId: String): F[Either[ResponseException[String, io.circe.Error], GeoCityDetails]] = {
    val backend = HttpClientSyncBackend()
    import citiz.model.AppJsonCodecs.*

    Sync[F].delay {

      val response: Identity[Response[Either[ResponseException[String, io.circe.Error], GeoCityDetails]]] =
        getCityRequest(cityId)
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

/** Async version of the API client. */
class HttpFetchCityDetailsAsync[F[_]: Async] extends FetchCityDetails[F] {

  override def fetchDetails(cityId: String): F[Either[ResponseException[String, io.circe.Error], GeoCityDetails]] = {
    val backend = ArmeriaCatsBackend[F]()
    import citiz.model.AppJsonCodecs.*

    getCityRequest(cityId)
      .response(asJson[GetCityData])
      .mapResponseRight(_.data)
      .send(backend)
      .map(_.body)
  }
}

object HttpFetchCityDetailsAsync {
  def apply[F[_]: Async]: HttpFetchCityDetailsAsync[F] = new HttpFetchCityDetailsAsync[F]
}
