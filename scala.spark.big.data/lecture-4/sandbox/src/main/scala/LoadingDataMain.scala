import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._

import org.apache.log4j.Logger
import org.apache.log4j.Level


object LoadingDataMain extends App {
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  val ss = SparkSession.builder()
    .appName("TestApp")
    .config("spark.master", "local")
    .getOrCreate()

  import Utils._
  import ss.implicits._


  val df1 = Data.persons.toDF()

  df1.printSchema()
  df1.show()

  // Create a DataFrame from a CSV file by creating a RDD first
  val csvLines = ss.sparkContext.textFile(filePath("persons.csv"))

  // we can map each line to a tuple
  val df20 = csvLines.map { line =>
    val arr = line.split(",").map(_.trim)
    (arr(0), arr(1), arr(2), arr(3), arr(4))
  }.toDF("id", "firstName", "lastName", "age", "address")

  df20.printSchema()
  df20.show()

  // let's try to map each line to a case class first
  val df21 = csvLines.map { line =>
    val arr = line.split(",").map(_.trim)
    Person(
      id = arr(0).toLong,
      firstName = arr(1),
      lastName = arr(2),
      age = arr(3).toInt,
      address = arr(4) match {
        case "null" => None
        case s => Some(s)
      }
    )
  }.toDF()

  df21.printSchema()
  df21.show()

  // build a DataFrame by explicitly define a schema
  val personSchema = StructType(Seq(
    StructField("firstName", StringType, nullable = false),
    StructField("lastName", StringType, nullable = false),
    StructField("age", IntegerType, nullable = true)
  ))

  val rowRDD = csvLines.map { line =>
    val arr = line.split(",").map(_.trim)
    Row(
      arr(0),
      arr(1),
      arr(2).toIntSafe.orNull
    )
  }

  val df3 = ss.createDataFrame(rowRDD, personSchema)

  df3.printSchema()
  df3.show()

  // some schema introspection methods
  println("DataFrame#columns")
  df3.columns.foreach(println)

  println("DataFrame#dtypes")
  df3.dtypes.foreach(println)

  println("\nData frame from JSON:")
  val dfJson = ss.read.json(filePath("persons.json"))
  dfJson.select('id, 'firstName, 'lastName, 'age, 'address).show()
}

