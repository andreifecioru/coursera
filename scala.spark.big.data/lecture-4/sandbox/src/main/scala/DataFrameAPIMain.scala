import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

import org.apache.log4j.Logger
import org.apache.log4j.Level


object DataFrameAPIMain extends App {
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  val ss = SparkSession.builder()
    .appName("test-app")
    .config("spark.master", "local")
    .getOrCreate()

  // reduce the number of DF partitions
  ss.conf.set("spark.sql.shuffle.partitions", 10)

  import ss.implicits._

  val personDF = Data.persons.toDF()
  personDF.printSchema()

  println("All persons: ")
  personDF.show()

  val purchaseRDD = ss.sparkContext.parallelize(Data.purchases)
  val purchaseDF = purchaseRDD.toDF()
  purchaseDF.printSchema()

  println("All purchases: ")
  purchaseDF.show()

  // --------------- [ Limiting output ] --------------
  println("Limit output to 2 entries:")
  val twoPersonsDF = personDF.limit(2)
  twoPersonsDF.show()

  // --------------- [ Selecting columns ] -------------
  // select only the 'firstName and the 'lastName columns
  println("Select the 'firstName and the 'lastName columns")
  val firstAndLastNameDF = personDF.select('firstName, 'lastName)
  firstAndLastNameDF.show()

  // drop the 'address column
  println("Drop the 'address column.")
  val personsWithAddressDF = personDF.drop('address)
  personsWithAddressDF.show()

  // combine columns
  val concat = udf { (firstName: String, lastName: String) => s"$firstName $lastName" }

  println("Full names:")
  val fullNameDF = personDF
    .withColumn("fullName", concat('firstName, 'lastName))
    .select('firstName, 'lastName, 'fullName)
  fullNameDF.show()


  // -------------- [ Cleaning data ] ----------------
  println("Cleaned-up data:")
  personDF.na.drop().show()

  println("Default 'Unknown' address.")
  personDF.na.fill(Map("address" -> "Unknown")).show()

  println(
    """Replace
      | - first names 'Andrei' -> 'Mihai'
      | - last names: 'Dudu' -> 'Gogu'""".stripMargin)
  personDF.na.replace(Array("firstName", "lastName"), Map("Andrei" -> "Mihai", "Dudu" -> "Gogu")).show()

  // -------------- [ Filtering data ] ---------------
  println("Adult persons:")
  val adultPersonsDF = personDF.where('age > 18)
  adultPersonsDF.show()

  println("A little more complex filtering:")
  val adultsWithAddressDF = personDF.where(('age > 18) and ('address isNull))
  adultsWithAddressDF.show()

  val adultsWithLongNamesDF = personDF.where(('age > 18) and (length('firstName) > 3) and ('address isNotNull))
  adultsWithLongNamesDF.show()

  // --------------- [ Scalar aggregate functions ] ----------
  println("Oldest person:")
  val oldestPersonDF = personDF.select(count('firstName), count('lastName), max('age))
  oldestPersonDF.show()

  println("Average age:")
  personDF.select(avg('age)).show()

  // ---------------- [ Window functions ] -----------
  val ageDiff = max('age)
    .over(Window.rangeBetween(Window.unboundedPreceding, Window.unboundedFollowing))
    .as("maxAge")

  println("Delta to oldest age:")
  personDF
    .select('firstName, 'lastName, 'age, ageDiff)
    .withColumn("toMax", 'maxAge - 'age)
    .show()

  // -------------- [ Back to RDD ] -----------
  println("Back to RDD")
  personDF.rdd.map(_.mkString(", ")).foreach(println)

  // ------------ [ Grouping ] ---------
  println("\nAverage age per city")
  personDF.na.drop(Array("address"))
    .groupBy('address)
    .agg(avg('age))
    .show()

  // --------------[ Joins ] -----------
  println("Inner join:")
  personDF.join(purchaseDF, personDF("id") === purchaseDF("personId"), "inner").show()

  println("Outer join:")
  personDF.join(purchaseDF, personDF("id") === purchaseDF("personId"), "outer").show()

  // ---------- [ SQL syntax ] --------
  personDF.createOrReplaceTempView("persons_temp")
  purchaseDF.createOrReplaceTempView("purchases_temp")

  println("A list of tables:")
  ss.catalog.listTables().show()

  println("Describe tables")
  ss.catalog.listTables().foreach { table =>
    ss.catalog.listColumns(table.name).show()
  }

  println("A simple SQL query:")
  ss.sql("select firstName, lastName, age from persons_temp where age > 18").show()

  // ------- [ Saving data ] ----------
  println("Saving data.")
  personDF.write.format("json").save("out/persons")
}
