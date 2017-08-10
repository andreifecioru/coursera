package funsets

import org.scalatest.FunSuite


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * This class is a test suite for the methods in object FunSets. To run
 * the test suite, you can either:
 *  - run the "test" command in the SBT console
 *  - right-click the file in eclipse and chose "Run As" - "JUnit Test"
 */
@RunWith(classOf[JUnitRunner])
class FunSetSuite extends FunSuite {

  /**
   * Link to the scaladoc - very clear and detailed tutorial of FunSuite
   *
   * http://doc.scalatest.org/1.9.1/index.html#org.scalatest.FunSuite
   *
   * Operators
   *  - test
   *  - ignore
   *  - pending
   */

  /**
   * Tests are written using the "test" operator and the "assert" method.
   */
  // test("string take") {
  //   val message = "hello, world"
  //   assert(message.take(5) == "hello")
  // }

  /**
   * For ScalaTest tests, there exists a special equality operator "===" that
   * can be used inside "assert". If the assertion fails, the two values will
   * be printed in the error message. Otherwise, when using "==", the test
   * error message will only say "assertion failed", without showing the values.
   *
   * Try it out! Change the values so that the assertion fails, and look at the
   * error message.
   */
  // test("adding ints") {
  //   assert(1 + 2 === 3)
  // }


  import FunSets._

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  /**
   * When writing tests, one would often like to re-use certain values for multiple
   * tests. For instance, we would like to create an Int-set and have multiple test
   * about it.
   *
   * Instead of copy-pasting the code for creating the set into every test, we can
   * store it in the test class using a val:
   *
   *   val s1 = singletonSet(1)
   *
   * However, what happens if the method "singletonSet" has a bug and crashes? Then
   * the test methods are not even executed, because creating an instance of the
   * test class fails!
   *
   * Therefore, we put the shared values into a separate trait (traits are like
   * abstract classes), and create an instance inside each test method.
   *
   */

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
  }

  test("singletonSet(1) contains 1") {
    new TestSets {
      assert(contains(s1, 1), "Singleton")
    }
  }

  test("union contains all elements of each set") {
    new TestSets {
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
    }
  }

  test("intersection works as expected") {
    new TestSets {
      val s = union(s1, s2)
      val t = union(s2, s3)
      val i = intersect(s, t)

      assert(contains(i, 2), "Intersect contains 2")
      assert(!contains(i, 1), "Intersect does not contain 1")
      assert(!contains(i, 3), "Intersect does not contain 3")
    }
  }

  test("diff works as expected") {
    new TestSets {
      val s = union(s1, s2)

      val d = diff(s, s1)

      assert(contains(d, 2), "Diff contains 2")
      assert(!contains(d, 1), "Diff contains 1")
    }
  }

  test("filter works as expected") {
    new TestSets {
      val s = union(union(s1, s2), s3)

      val f = filter(s, _ % 2 != 0)

      assert(contains(f, 1), "Filter contains 1")
      assert(contains(f, 3), "Filter contains 3")
      assert(!contains(f, 2), "Filter does not contain 2")
    }
  }

  test("forall works as expected") {
    val s: Set = _ % 10 == 0

    assert(forall(s, _ % 5 == 0), "All divisible by 10 are divisible by 5")
    assert(!forall(s, _ % 15 == 0), "Not all divisible by 10 are divisible by 15")
  }

  test("exists works as expected") {
    val s: Set = (0 to 10).contains

    assert(exists(s, (5 to 10).contains), "There is a number between 5 and 10 smaller than 10")
    assert(!exists(s, (20 to 30).contains), "There's no number between 20 and 30 smaller than 10")
  }

  test("map works as expected") {
    val s: Set = (0 to 10).contains

    val m: Set = map(s, _ * 2)

    val expected: Set = (0 to 10).map(_ * 2).contains

    assert(forall(m, contains(expected, _)), "Doubling numbers below 10")
  }

}
