name := """sandbox"""

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-language:implicitConversions")

// Change this to another test framework if you prefer
libraryDependencies ++= {
  val sparkVersion = "2.1.0"

  Seq(
    // spark and spark sql
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,

    // unit-testing
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )
}

