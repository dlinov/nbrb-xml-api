package io.github.dlinov.nbrbxmlapi

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.either._
import org.slf4j.LoggerFactory

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    (for {
      config <- AppConfig.loadDefault
    } yield {
      Server.stream[IO](config).compile.drain.as(ExitCode.Success)
    }).leftMap { errors =>
      LoggerFactory.getLogger(getClass).error(errors)
      IO.pure(ExitCode.Error)
    }.merge
  }

}
