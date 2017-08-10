package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    var count = 0
    for (i <- chars.indices) {
      if (chars(i) == '(') count += 1
      if (chars(i) == ')') count -= 1
      if (count < 0) return false
    }
    count == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {
    trait Tree[T] {
      def v: T
    }

    case class Leaf[T](v: T) extends Tree[T] {
      override def toString = s"{$v}"
    }
    case class Node[T](left: Tree[T], right: Tree[T], v: T) extends Tree[T] {
      override def toString = s"<$left $v $right>"
    }

    def rightMostLeafValue[T](t: Tree[T]): T =
      t match {
        case Leaf(v) => v
        case Node(l, r, v) => rightMostLeafValue(r)
      }

    case class Parens(open: Int, closed: Int) {
      def +(other: Parens): Parens = Parens(
        this.open + other.open,
        this.closed + other.closed
      )
    }
    case class Stats(count: Int, ok: Boolean)

    def traverse(from: Int, to: Int, acc: Parens): Parens = {
      if (from == to) acc
      else
        chars(from) match {
          case '(' => traverse(from + 1, to, Parens(acc.open + 1, acc.closed))
          case ')' => traverse(from + 1, to, Parens(acc.open,acc. closed + 1))
          case _ => traverse(from + 1, to, acc)
        }
    }

    def reduce(from: Int, to: Int): Stats  = {
      def _upsweep(from: Int, to: Int): Tree[Parens] = {
        if ((to - from) <= threshold) {
          Leaf(traverse(from, to, Parens(0, 0)))
        } else {
          val mid = (from + to) / 2
          val (rl, rr) = parallel(
            _upsweep(from, mid),
            _upsweep(mid, to)
          )
          Node(rl, rr, rl.v + rr.v)
        }
      }

      def _downsweep(t: Tree[Parens], acc: Stats): Tree[Stats] = {
        def f(acc: Stats, v: Parens): Stats =
          if (acc.ok) {
            val count = acc.count + v.open - v.closed
            Stats(count, count >= 0)
          } else acc

        t match {
          case Leaf(v) =>
            Leaf(f(acc, v))
          case Node(l, r, v) =>
            val (rl, rr) = (
              _downsweep(l, acc),
              _downsweep(r, f(acc, l.v))
            )
            Node(rl, rr, Stats(0, ok = true))
        }
      }

      val upsweepTree = _upsweep(from, to)
//      println(s"Upsweep tree: $upsweepTree")

      val downsweepTree = _downsweep(upsweepTree, Stats(0, ok = true))
//      println(s"Downsweep tree: $downsweepTree")

      val rightMostValue = rightMostLeafValue(downsweepTree)
//      println(s"Right-most value: $rightMostValue")

      rightMostValue
    }

    reduce(0, chars.length) == Stats(0, ok = true)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
