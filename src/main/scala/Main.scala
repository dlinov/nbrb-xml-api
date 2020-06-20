import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.either._
import org.slf4j.LoggerFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    (for {
      config <- ConfigSource.default.load[AppConfig]
    } yield {
      Server.stream[IO](config).compile.drain.as(ExitCode.Success)
    }).leftMap { errors =>
      LoggerFactory.getLogger(getClass).error(errors.prettyPrint())
      IO.pure(ExitCode.Error)
    }.merge

  }

}
