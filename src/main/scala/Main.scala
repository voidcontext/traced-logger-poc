import cats.implicits.*
import cats.effect.IOApp
import natchez.EntryPoint
import com.ovoenergy.natchez.extras.slf4j.Slf4j
import cats.effect.IO
import cats.data.Kleisli
import natchez.Span
import org.typelevel.log4cats.StructuredLogger
import cats.effect.ExitCode
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.ovoenergy.natchez.extras.log4cats.TracedLogger
import natchez.Trace
import cats.effect.kernel.Sync

object Main extends IOApp: 
  val entryPoint: EntryPoint[IO] = Slf4j.entryPoint[IO]

  val logger: IO[StructuredLogger[TracedIO]] =
    Slf4jLogger.create[IO].map(TracedLogger.lift(_, _.toHeaders))

  type TracedIO[A] = Kleisli[IO, Span[IO], A]

  def application[F[_]: Sync: StructuredLogger: Trace]: F[ExitCode] =
    val logger = Slf4jLogger.getLoggerFromName("Application.logger")
    for
      _ <-  logger.info("This is not going to be traced, see mdc:")
      _ <- StructuredLogger[F].info("This is going to be traced, see mdc:")
      // An alternative option:
      tracedLocalLogger <- Slf4jLogger.create.map(TracedLogger(_, _.toHeaders))
      - <- tracedLocalLogger.info("A local logger still can be traced, see mdc:")
    yield ExitCode.Success

  def run(args: List[String]): IO[ExitCode] =
      entryPoint.root("root_span")
        .use(span =>
          for
            given Logger[TracedIO] <- logger
            kernel <- span.kernel
            exitCode <- application[TracedIO].run(span)
          yield exitCode
        )
