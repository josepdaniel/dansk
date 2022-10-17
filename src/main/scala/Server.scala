package dev.joedaniel.flashcards

import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.Status.*
import org.http4s.dsl.io.*
import org.http4s.Uri.Path.*
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import scala.io.*
import io.circe.Codec
import io.circe.parser.decode
import io.circe.syntax.*
import scala.util.Random

def file(name: String): Resource[IO, BufferedSource] = Resource.make(IO(Source.fromFile(name)))(file => IO(file.close()))
def fileAsString(name: String) = file(name).map(_.mkString)

enum FlashcardError {
    case NoMatches
}

case class Phrase(
    dk: String,
    en: String,
    tags: Option[Set[String]],
    notes: Option[String]
) derives Codec.AsObject

case class Grammar(
    explanation: String,
    examples: Vector[String],
    tags: Option[Set[String]],
    relevantPhrases: Option[Vector[Phrase]]
) derives Codec.AsObject

case class Flashcards (
    phrases: Vector[Phrase],
    grammar: Vector[Grammar]
) derives Codec.AsObject  {
    def join: Flashcards = Flashcards(phrases, grammar.map{g => 
        g.copy(relevantPhrases = Some(phrases.filter(p => p.tags.exists(t => t.intersect(g.tags.getOrElse(Set.empty)).size > 0 ))))
    })
    def randomPhrase: IO[Phrase] = IO(Random.between(0, phrases.size)).map(idx => phrases(idx))
    def randomPhraseWithTag(tag: String): IO[Either[FlashcardError, Phrase]] = {
        val eligible = phrases.filter(_.tags.exists(v => v.contains(tag)))
        if (eligible.size > 0) {
            IO(Right(Random.between(0, eligible.size)).map(idx => eligible(idx)))
        } else {
            IO(Left(FlashcardError.NoMatches))
        }
        
    }
}


object Server {

    object Tag extends OptionalQueryParamDecoderMatcher[String]("tag")

    // Fail fast if the json file can't be parsed
    val routes = fileAsString("src/main/resources/flashcards.json").use { source => 
        val flashcards = decode[Flashcards](source).toOption.get.join
    
        IO(HttpRoutes.of[IO] { 
            case GET -> Root / "phrase" / "random" :? Tag(tag) => tag match {
                case None => flashcards.randomPhrase.flatMap(phrase => Ok(phrase.asJson))
                case Some(t) => flashcards.randomPhraseWithTag(t).flatMap(phrase => phrase.fold(_ => NotFound(), p => Ok(p.asJson)))
            }
            case GET -> Root / "grammar" :? Tag(tag) => tag match {
                case None => Ok(flashcards.grammar.asJson)
                case Some(t) => Ok(flashcards.grammar.filter(grammar => grammar.tags.exists(v => v.contains(t))).asJson)
            }

        })
    }

    
}
