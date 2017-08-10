package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._

import ParallelParenthesesBalancing._

@RunWith(classOf[JUnitRunner])
class ParallelParenthesesBalancingSuite extends FunSuite {

  test("balance should work for empty string") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("", expected = true)
  }

  test("balance should work for string of length 1") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("(", expected = false)
    check(")", expected = false)
    check(".", expected = true)
  }

  test("balance should work for string of length 2") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("()", expected = true)
    check(")(", expected = false)
    check("((", expected = false)
    check("))", expected = false)
    check(".)", expected = false)
    check(".(", expected = false)
    check("(.", expected = false)
    check(").", expected = false)
  }

  test("parallel balance should work for string of length 2") {
    def check(input: String, expected: Boolean) =
      assert(parBalance(input.toArray, 1) == expected,
        s"balance($input) should be $expected")

    check("()", expected = true)
    check(")(", expected = false)
    check("((", expected = false)
    check("))", expected = false)
    check(".)", expected = false)
    check(".(", expected = false)
    check("(.", expected = false)
    check(").", expected = false)
  }

}