package stackoverflow

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.net.URL
import java.nio.channels.Channels
import java.io.File
import java.io.FileOutputStream

@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfterEach {

  lazy val testObject = new StackOverflow {
    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  override def afterAll(): Unit = {
    import StackOverflow._
    sc.stop()
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }

  test("groupedPostings works on some sample data") {
    val testData = List(
      // Questions
      Posting(1, 1, Some(11), None, 100, Some("C++")),
      Posting(1, 2, Some(12), None, 110, Some("C#")),
      Posting(1, 3, None, None, 120, Some("Ruby")),

      // Answers
      Posting(2, 11, None, Some(1), 10, Some("C++")),
      Posting(2, 12, None, Some(2), 11, Some("C#")),
      Posting(2, 13, None, Some(3), 12, Some("Ruby")),
      Posting(2, 14, None, Some(3), 22, Some("Ruby"))
    )

    val expectedData = List(
      1 -> Iterable(
        Posting(1, 1, Some(11), None, 100, Some("C++")) -> Posting(2, 11, None, Some(1), 10, Some("C++"))
      ),

      2 -> Iterable(
        Posting(1, 2, Some(12), None, 110, Some("C#")) -> Posting(2, 12, None, Some(2), 11, Some("C#"))
      ),

      3 -> Iterable(
        Posting(1, 3, None, None, 120, Some("Ruby")) -> Posting(2, 13, None, Some(3), 12, Some("Ruby")),
        Posting(1, 3, None, None, 120, Some("Ruby")) -> Posting(2, 14, None, Some(3), 22, Some("Ruby"))
      )
    )

    import StackOverflow._
    val postings: RDD[Posting] = sc.parallelize(testData).persist()

    val result = testObject.groupedPostings(postings).collect()

    assert(result.toSet == expectedData.toSet, "The given elements are not the same as the expected elements")
  }

  test("scoredPostings works on some sample data") {
    val testData = List(
      1 -> Iterable(
        Posting(1, 1, Some(11), None, 100, Some("C++")) -> Posting(2, 11, None, Some(1), 10, Some("C++"))
      ),

      2 -> Iterable(
        Posting(1, 2, Some(12), None, 110, Some("C#")) -> Posting(2, 12, None, Some(2), 11, Some("C#"))
      ),

      3 -> Iterable(
        Posting(1, 3, None, None, 120, Some("Ruby")) -> Posting(2, 13, None, Some(3), 12, Some("Ruby")),
        Posting(1, 3, None, None, 120, Some("Ruby")) -> Posting(2, 14, None, Some(3), 22, Some("Ruby"))
      )
    )

    val expectedData = List(
      Posting(1, 1, Some(11), None, 100, Some("C++")) -> 10,
      Posting(1, 2, Some(12), None, 110, Some("C#")) -> 11,
      Posting(1, 3, None, None, 120, Some("Ruby")) -> 22
    )

    import StackOverflow._
    val grouped: RDD[(Int, Iterable[(Posting, Posting)])] = sc.parallelize(testData).persist()

    val result = testObject.scoredPostings(grouped).collect()

    assert(result.toSet == expectedData.toSet, "The given elements are not the same as the expected elements")
  }

  test("vectorPostings works in some sample data") {
    val testData = List(
      Posting(1, 1, Some(11), None, 100, Some("C++")) -> 10,
      Posting(1, 2, Some(12), None, 110, Some("C#")) -> 11,
      Posting(1, 3, None, None, 120, Some("Ruby")) -> 22
    )

    import StackOverflow._
    val expectedData = List(
      5 * langSpread -> 10, // C++
      4 * langSpread -> 11, // C#
      6 * langSpread -> 22  // Ruby
    )

    val scored: RDD[(Posting, Int)] = sc.parallelize(testData).persist()
    val result =  testObject.vectorPostings(scored) .collect()

    assert(result.toSet == expectedData.toSet, "The given elements are not the same as the expected elements")
  }

  test("clusterResults"){
    import StackOverflow._

    val centers = Array((0,0), (100000, 0))
    val rdd = sc.parallelize(List(
      (0, 1000),
      (0, 23),
      (0, 234),
      (0, 0),
      (0, 1),
      (0, 1),
      (50000, 2),
      (50000, 10),
      (100000, 2),
      (100000, 5),
      (100000, 10),
      (200000, 100)  ))
    testObject.printResults(testObject.clusterResults(centers, rdd))
  }

}
