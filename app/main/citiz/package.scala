import cats.effect.{Concurrent, Sync}

import io.circe.JsonObject
import io.circe.parser.parse
import parser.ParamsMap

import java.io.{BufferedWriter, File, FileFilter, PrintWriter}
import scala.io.Source
import scala.util.{Try, Using}

package object citiz {

  val onlyFiles: FileFilter = (file: File) => file.isFile && file.canRead

  def readFile[F[_]: Concurrent](file: File): F[Option[String]] =
    Sync[F].delay {
      Using(Source.fromFile(file))(source => source.mkString).toOption
    }

  def writeFile[F[_]: Concurrent](file: File, content: String): F[Try[Unit]] =
    Sync[F].delay {
      Using(new BufferedWriter(new PrintWriter(file)))(writer => writer.write(content))
    }

  // ---------------------------------------------------------------- params

  private def toMap(json: JsonObject): Map[String, String] =
    json.toMap.view.mapValues(_.asString).toMap.collect { case (k, Some(v)) => (k, v) }

  def jsonToMap(jsonString: String): ParamsMap =
    parse(jsonString) match {
      case Right(json) => json.asObject.map(toMap).getOrElse(Map.empty)
      case Left(_) => Map.empty
    }
}
