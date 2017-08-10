package observatory

import org.junit.runner.RunWith

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite with BeforeAndAfterAll {

  ignore("'locateTemperatures' should work as expected") {
    val result = Extraction.locateTemperatures(year = 1975, stationsFile = "/stations.csv", temperaturesFile = "/1975.csv")

    result.foreach(println)
  }

  ignore("'locationYearlyAverageRecords' should work as expected") {
    val records = Extraction.locateTemperatures(year = 1975, stationsFile = "stations.csv", temperaturesFile = "1975.csv")
    val avgTemp = Extraction.locationYearlyAverageRecords(records)

    println(avgTemp)
  }
}