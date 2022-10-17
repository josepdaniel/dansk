package dev.joedaniel.flashcards

import cats.effect.*
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    Server.routes.flatMap(routes =>
      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"3000")
        .withHttpApp(routes.orNotFound)
        .build
        .useForever
    )
  }
}
