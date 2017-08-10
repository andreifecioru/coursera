package akkastream

import java.nio.file.Paths

import scala.util.{Success, Failure}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.Future

object AkkaStream02Main extends App {
  implicit val system = ActorSystem("BasicAkkaStreamApp")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source: Source[Int, NotUsed] = Source(1 to 50)
  val factorials: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) => acc * next)

  val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("data/out/factorials.txt"))
  val done: Future[IOResult] = factorials
    .map(num => ByteString(s"$num\n"))
    .runWith(fileSink)

  done.onComplete {
    case Success(result) =>
      println(s"${result.count} bytes written")
      result.status match {
        case Success(_) =>
          println("Done. Check your output")
        case Failure(e) =>
          println(s"ERROR: ${e.getMessage}")
      }
      system.terminate()

    case Failure(e) =>
      println(s"ERROR: ${e.getMessage}")
      system.terminate()
  }
}
