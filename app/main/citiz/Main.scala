package citiz

import cats.*
import cats.effect.*
import cats.implicits.*
import citiz.model.{City, CityXml, FileName, XmlContent}
import io.circe.syntax.EncoderOps
import parser.CityParser
import syseff.{Console, Environment, StdConsole, SysEnvironment}

import java.io.File
import scala.util.Try

object Main extends IOApp {

  private def loadSettingsFromEnvs[F[_]: Sync](
    implicit E: Environment[F]): F[Settings] =
    for {
      ins <- E.getEnv("IN_PATH")
      out <- E.getEnv("OUT_PATH")
    } yield Settings(ins, out)

  private def collectCityFiles[F[_]: Concurrent](folder: String): F[Seq[File]] =
    Sync[F].delay {
      new File(s"$folder").listFiles(onlyFiles).toSeq
    }

  private def loadCityXml[F[_]: Concurrent](file: File): F[Option[CityXml]] =
    for {
      xml <- readFile(file)
      city <- xml match {
        case Some(content) => Sync[F].delay(Some(CityXml(FileName(file.getName), XmlContent(content))))
        case None => Sync[F].pure(None)
      }
    } yield city

  def writeCityJson[F[_]: Concurrent](outFolder: String, city: City): F[Try[Unit]] = {
    import citiz.model.AppJsonCodecs.*
    writeFile(new File(outFolder, s"${city.data.name}.json"), city.asJson.noSpaces)
  }

  /** Here Be Dragons. */
  override def run(args: List[String]): IO[ExitCode] = {
    implicit val C: Console[IO] = StdConsole[IO]
    implicit val E: Environment[IO] = SysEnvironment[IO]
    implicit val D: FetchCityDetails[IO] = HttpFetchCityDetails[IO]
    program[IO].as(ExitCode.Success)
  }

  private def program[F[_]: Concurrent](
    implicit C: Console[F],
    E: Environment[F],
    D: FetchCityDetails[F],
    P: Parallel[F]): F[Unit] =
    for {
      settings <- loadSettingsFromEnvs

      inputFiles <- collectCityFiles(settings.inFolder)

      // load cities from files, but ignore read errors
      citiesXml <- inputFiles.parTraverse(loadCityXml[F]).map(_.flatten)

      // parse cities and start taking care about errors from this point on by using Either
      citiesData = citiesXml
        .map(_.xml)
        .map(CityParser.parse)
        .map(_.leftMap(error => s"Error parsing city: $error"))

      // fetch details for cities and merge everything together
      cities <- citiesData.parTraverse { eitherCityData =>
        for {
          a <- eitherCityData.map(_.wikiDataId).traverse(D.fetchDetails)
          b = a.map(_.leftMap(error => s"Error fetching city details: ${error.getMessage}")).flatten
          c = (eitherCityData, b).mapN(City)
        } yield c
      }

      _ <- C.print(s"Loaded ${cities.size} cities.")

      // finally write cities to files
      _ <- cities.flatMap(_.toOption).parTraverse(writeCityJson(settings.outFolder, _))

      _ <- cities.traverse {
        case Right(city) => C.putStrLn(s"✅ City: ${city.details.name}")
        case Left(error) => C.print(s"❌ $error")
      }

      _ <- C.putStrLn(s"Total cities: ${cities.size}. Errors: ${cities.count(_.isLeft)} - Successes: ${cities.count(_.isRight)}")
    } yield ()

}
