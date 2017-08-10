package akkastream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

object AkkaStream01Main extends App {
  implicit val system = ActorSystem("BasicAkkaStreamApp")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source: Source[Int, NotUsed] = Source(1 to 100)
  val done = source.runForeach(println)

  done.onComplete(_ => system.terminate())
}
