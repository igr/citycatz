package syseff

import cats.effect.Sync
import cats.implicits.*

import scala.util.control.NoStackTrace

trait Environment[F[_]] {

  def getEnv(s: String): F[String]

  def lookupEnv(s: String): F[Option[String]]
}

final case class EnvironmentVariableDoesNotExist(variable: String) extends Throwable with NoStackTrace {
  override def getMessage: String = s"Environment variable $variable does not exist"
}

/** System environments. */
class SysEnvironment[F[_]: Sync] extends Environment[F] {

  def getEnv(s: String): F[String] = lookupEnv(s).map(_.getOrElse(throw EnvironmentVariableDoesNotExist(s)))

  def lookupEnv(s: String): F[Option[String]] = Sync[F].delay(sys.env.get(s))
}

object SysEnvironment {
  def apply[F[_]: Sync]: SysEnvironment[F] = new SysEnvironment[F]
}

/** Map-backed environments. */
class MapEnvironment[F[_]: Sync](map: Map[String, String]) extends Environment[F] {

  def getEnv(s: String): F[String] = lookupEnv(s).map(_.getOrElse(throw EnvironmentVariableDoesNotExist(s)))

  def lookupEnv(s: String): F[Option[String]] = Sync[F].pure(map.get(s))
}

object MapEnvironment {
  def apply[F[_]: Sync](map: Map[String, String]): MapEnvironment[F] = new MapEnvironment[F](map)
}
