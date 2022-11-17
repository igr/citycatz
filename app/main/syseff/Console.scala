package syseff

import cats.Show
import cats.effect.Sync

trait Console[F[_]] {

  def putStrLn(s: String): F[Unit]

  def print[A: Show](a: A): F[Unit]

  def getStrLn: F[String]
}

class StdConsole[F[_]: Sync] extends Console[F] {

  def putStrLn(s: String): F[Unit] = Sync[F].delay(println(s))

  def print[A: Show](a: A): F[Unit] = putStrLn(Show[A].show(a))

  def getStrLn: F[String] = Sync[F].delay(scala.io.StdIn.readLine())
}

object StdConsole {
  def apply[F[_]: Sync]: StdConsole[F] = new StdConsole[F]
}
