package timeusage

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql._
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TimeUsageSuite extends FunSuite with BeforeAndAfterAll {
  var ss: SparkSession = _

  override def beforeAll(): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    ss = SparkSession.builder()
      .appName("unit-test")
      .config("spark.master", "local")
      .getOrCreate()

    // reduce the number of DF partitions
    ss.conf.set("spark.sql.shuffle.partitions", 10)
  }

  override def afterAll(): Unit = {
    ss.close()
    ss = null
  }

  ignore("'read' should work as expected") {
    val (_, df) = TimeUsage.read("test_data_9.csv")
    assert(df.count == 9)
  }

  ignore("'classifiedColumns' should work as expected") {
    val (colNames, _) = TimeUsage.read("test_data_9.csv")
    val (primaryNeedsCols, workingCols, otherCols) = TimeUsage.classifiedColumns(colNames)

    assert(primaryNeedsCols.size == 55, "no. of private needs cols")
    assert(workingCols.size == 23, "no. of working activities cols")
    assert(otherCols.size == 346, "no. of other activities cols")
  }

  ignore("'timeUsageSummary' should work as expected") {
    val (colNames, df) = TimeUsage.read("test_data_9.csv")
    val (primaryNeedsCols, workingCols, otherCols) = TimeUsage.classifiedColumns(colNames)

    val tuDF = TimeUsage.timeUsageSummary(primaryNeedsCols, workingCols, otherCols, df)

    tuDF.show()

    val cols = workingCols ++ List(new Column("telfs"))

    df.select(cols:_*).show()
  }


  ignore("'timeUsageGrouped' should work as expected") {
    val (colNames, df) = TimeUsage.read("test_data_9.csv")
    val (primaryNeedsCols, workingCols, otherCols) = TimeUsage.classifiedColumns(colNames)

    val summed = TimeUsage.timeUsageSummary(primaryNeedsCols, workingCols, otherCols, df)
//    summed.show()

    val grouped = TimeUsage.timeUsageGrouped(summed)
    grouped.show()
  }

  ignore("'timeUsageGroupedSql' should work as expected") {
    val (colNames, df) = TimeUsage.read("test_data_9.csv")
    val (primaryNeedsCols, workingCols, otherCols) = TimeUsage.classifiedColumns(colNames)

    val summed = TimeUsage.timeUsageSummary(primaryNeedsCols, workingCols, otherCols, df)
//    summed.show()

    val grouped = TimeUsage.timeUsageGroupedSql(summed)
    grouped.show()
  }

  ignore("'timeUsageGroupedTyped' should work as expected") {
    val (colNames, df) = TimeUsage.read("test_data_9.csv")
    val (primaryNeedsCols, workingCols, otherCols) = TimeUsage.classifiedColumns(colNames)

    val summed = TimeUsage.timeUsageSummary(primaryNeedsCols, workingCols, otherCols, df)
    val summedGrouped = TimeUsage.timeUsageSummaryTyped(summed)

    val grouped = TimeUsage.timeUsageGroupedTyped(summedGrouped)
    grouped.show()
  }
}
