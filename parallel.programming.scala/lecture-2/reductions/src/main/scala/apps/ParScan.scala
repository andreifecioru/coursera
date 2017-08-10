package apps

import common.parallel
import org.scalameter._

object ParScan extends App {
  val sum = (x: Int, y: Int) => x + y
  val a = (1 until 1e8.toInt).toArray[Int]

  val cfg = config(
    Key.exec.minWarmupRuns -> 10,
    Key.exec.maxWarmupRuns -> 40,
    Key.verbose -> true
  )

  val seqTime = cfg withWarmer new Warmer.Default measure {
    scanLeft(100)(a, sum)
  }

  val parTime = cfg withWarmer new Warmer.Default measure {
    parTreeScanLeft(100)(100000)(a, sum)
  }

  println(s"Speedup: ${seqTime.toDouble / parTime}")

  trait Tree[A]
  case class Leaf[A](v: A) extends Tree[A] {
    override def toString = s"$v"
  }
  case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A] {
    override def toString = s"($left, $right)"
  }

  trait TreeVal[A] {
    def v: A
  }
  case class LeafVal[A](v: A) extends TreeVal[A] {
    override def toString = s"$v"
  }
  case class LeafValSeg[A](from: Int, to: Int, v: A) extends TreeVal[A] {
    override def toString = s"[$from, $to]: $v"
  }
  case class NodeVal[A](left: TreeVal[A], right: TreeVal[A], v: A) extends TreeVal[A] {
    override def toString = s"<$left ($v) $right>"
  }

  def upsweep[A](t: Tree[A], f: (A, A) => A): TreeVal[A] = {
    t match {
      case Leaf(v) => LeafVal(v)
      case Node(l, r) =>
        val (left, right) = parallel(upsweep(l, f),
                                     upsweep(r, f))
        NodeVal(left, right, f(left.v, right.v))
    }
  }

  def downsweep[A](acc: A)(t: TreeVal[A], f: (A, A) => A): Tree[A] = {
    t match {
      case LeafVal(v) => Leaf(f(acc, v))
      case NodeVal(l, r, _) =>
        val (lr, rr) = parallel(
          downsweep(acc)(l, f),
          downsweep(f(acc, l.v))(r, f)
        )
        Node(lr, rr)
    }
  }

  def reduce[A](t: Tree[A], f: (A, A) => A): A = {
    t match {
      case Leaf(v) => v
      case Node(l, r) =>
        val (rl, rr) = parallel(reduce(l, f), reduce(r, f))
        f(rl, rr)
    }
  }

  def map[A, B](t: Tree[A], f: A => B): Tree[B] ={
    t match {
      case Leaf(v) => Leaf(f(v))
      case Node(l, r) => Node(map(l, f), map(r, f))
    }
  }

  def arrayToTree[A](in: Array[A]): Tree[A] = {
    def _convert(from: Int, to: Int): Tree[A] = {
      if((to - from) == 1) {
        Leaf(in(from))
      } else {
        val mid = (from + to) / 2
        Node(
          _convert(from, mid),
          _convert(mid, to)
        )
      }
    }

    _convert(0, in.length)
  }

  def treeToArray[A: Manifest](t: Tree[A]): Array[A] = {
    val f = (x: A) => Array(x)
    val t1 = map[A, Array[A]](t, f)
    val concat = (a1: Array[A], a2: Array[A]) => {
      val out = new Array[A](a1.length + a2.length)
      Array.copy(a1, 0, out, 0, a1.length)
      Array.copy(a2, 0, out, a1.length, a2.length)
      out
    }
    reduce[Array[A]](t1, concat)
  }

  def scanLeft[A: Manifest](acc: A)(in: Array[A], f: (A, A) => A): Array[A] = {
    val out = new Array[A](in.length + 1)

    out(0) = acc
    for (i <- in.indices) {
      out(i + 1) = f(out(i), in(i))
    }
    out
  }

  def parScanLeft[A: Manifest](acc: A)(threshold: Int)(in: Array[A], f: (A, A) => A): Array[A] = {
    val out = new Array[A](in.length + 1)

    def _fold(from: Int, to: Int): A = {
      def _reduce(from: Int, to: Int): A = {
        //        println(s"[reduce] $from : $to")
        if ((to - from) <= threshold) {
          var _acc = in(from)
          for (i <- (from + 1) until to) {
            _acc = f(_acc, in(i))
          }
          _acc
        } else {
          val mid = (from + to) / 2
          val (rl, rr) = parallel(
            _reduce(from, mid),
            _reduce(mid, to)
          )
          f(rl, rr)
        }
      }

      f(acc, _reduce(from, to))
    }

    def _map(from: Int, to: Int, f: (Int, A) => A): Unit = {
      //      println(s"[map] $from : $to")
      if ((to - from) <= threshold) {
        for (i <- from until to)  {
          out(i + 1) = f(i, in(i))
        }
      } else {
        val mid = (to + from) / 2
        parallel(
          _map(from, mid, f),
          _map(mid, to, f)
        )
      }
    }

    out(0) = acc

    val _f = (i: Int, x: A) => f(_fold(0, i), x)
    _map(0, in.length, _f)

    out
  }

  def parTreeScanLeft[A: Manifest](acc: A)(threshold: Int)(in: Array[A], f: (A, A) => A): Array[A] = {
    val out = new Array[A](in.length + 1)

    def _scanSeg(acc: A)(from: Int, to: Int): Unit = {
      out(from) = acc
      for (i <- from until to) {
        out(i + 1) = f(out(i), in(i))
      }
    }

    def _reduceSeg(from: Int, to: Int): A = {
      var _acc = in(from)
      for (i <- (from + 1) until to) {
        _acc = f(_acc, in(i))
      }
      _acc
    }

    def _upsweep(from: Int, to: Int): TreeVal[A] = {
      if ((to - from) <= threshold) {
        LeafValSeg(from, to, _reduceSeg(from, to))
      } else {
        val mid = (from + to) / 2
        val (rl, rr) = parallel(
          _upsweep(from, mid),
          _upsweep(mid, to)
        )
        NodeVal(rl, rr, f(rl.v, rr.v))
      }
    }

    def _downsweep(_acc: A)(t: TreeVal[A]): Unit = {
      t match {
        case LeafValSeg(from, to, _) =>
          _scanSeg(_acc)(from, to)
        case NodeVal(l, r, _) =>
          parallel(
            _downsweep(_acc)(l),
            _downsweep(f(_acc, l.v))(r)
          )
      }
    }

    val t = _upsweep(0, in.length)
    _downsweep(acc)(t)
    out
  }
}
