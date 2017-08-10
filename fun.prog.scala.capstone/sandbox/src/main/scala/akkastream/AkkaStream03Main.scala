package akkastream

import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class Person(firstName: String, lastName: String)

object AkkaStream03Main extends App {
  implicit val system = ActorSystem("BasicAkkaStreamApp")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val fileSource: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get("data/in/names.txt"))

  val persons = fileSource
    .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
    .map(_.utf8String)
    .map { entry =>
      val tokens = entry.split(" ").take(2)
      if (tokens.length == 2) Some(Person(tokens(0), tokens(1)))
      else None
    }

  val done = persons.runForeach(println)

  done.onComplete {
    case Success(_) =>
      println(s"Done.")
      system.terminate()

    case Failure(e) =>
      println(s"ERROR: ${e.getMessage}")
      system.terminate()
  }
}
