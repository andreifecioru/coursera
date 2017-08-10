package observatory

import java.time.LocalDate
import java.nio.file.Paths

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

import akka.Done
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl._
import akka.actor.ActorSystem
import akka.util.ByteString

/**
  * 1st milestone: data extraction
  */
object Extraction {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    implicit val system = ActorSystem(s"ExtractDataSystemFor$year")
    implicit val mat = ActorMaterializer()
    implicit val ec = system.dispatcher

    def getFilePath(fileName: String) = {
      Paths.get(this.getClass.getResource(fileName).toURI)
    }

    def fahrenheitToCelsius(f: Double) = (f - 32) * 5.0 / 9.0

    lazy val stations: Seq[((String, String), Location)] = {
      import scala.io.Source

      val stations = for (line <- Source.fromFile(getFilePath(stationsFile).toUri).getLines()) yield {
        Try {
          val tokens = line.split(",")

          val stnId = tokens(0)
          val wbanId = tokens(1)
          val lat = tokens(2).toDouble
          val lon = tokens(3).toDouble

          ((stnId, wbanId), Location(lat, lon))
        }.toOption
      }

      (for {
        stationMaybe <- stations
        station <- stationMaybe if !station._1._1.isEmpty || !station._1._2.isEmpty
      } yield station).toSeq
    }

    val temps = FileIO.fromPath(getFilePath(temperaturesFile))
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .map(_.utf8String)
      .map { entry =>
        val tokens = entry.split(",")
        Try {
          val stnId = tokens(0)
          val wbanId = tokens(1)
          val month = tokens(2).toInt
          val day = tokens(3).toInt
          val temp = tokens(4).toDouble

          ((stnId, wbanId), LocalDate.of(year, month, day), fahrenheitToCelsius(temp))
        }.toOption
      }.map { _.flatMap { temp =>
          stations.find(_._1 == temp._1).map { location => (temp._2, location._2, temp._3) }
        }
      }

    val output = temps.runFold(Iterable[(LocalDate, Location, Double)]())( (acc, tempMaybe) => tempMaybe match {
      case Some(temp) =>
        acc ++ Seq(temp)
      case None =>
        acc
    })

    Await.result(output, Duration.Inf)
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    records.groupBy(_._2).mapValues {
      items => items.foldLeft((0.0, items.size))((acc, item) => (acc._1 + item._3, acc._2))
    }.map {
      case (location, (sum, size)) => location -> sum / size
    }
  }
}
