name := """sandbox"""

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Xexperimental"
)

libraryDependencies ++= {
  val akkaStreamVersion = "2.5.1"

  Seq(
    // Akka Stream
    "com.typesafe.akka" %% "akka-stream" % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaStreamVersion  % Test,

    // Unit test support
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "org.scalacheck" %% "scalacheck" % "1.12.1" % Test,
    "junit" % "junit" % "4.10" % Test
  )
}

