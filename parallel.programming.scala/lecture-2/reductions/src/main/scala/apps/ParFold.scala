package apps

import common.parallel

object ParFold extends App {
  val plus: Operation[Int] = (x, y) => x + y
  val mult: Operation[Int] = (x, y) => x * y

  val t = Node(
    Node(
      Leaf(1),
      Leaf(2)
    ),
    Node(
      Leaf(3),
      Leaf(4)
    )
  )

  println(s"Tree: $t")
  println(s"Tree reduces to: ${reduce(t, plus)}")

  println(s"Tree as list: ${toList(t)}")

  println()

  val a = (0 until 10).toArray[Int]
  val res = parFold(100)(a, 0, a.length, 2)(plus)
  print(s"Fold result is: $res")

  def parFold[A](acc: A)(in: Array[A], from: Int, to: Int, maxDepth: Int)(f: (A, A) => A): A = {
    val _maxDepth = math.min(maxDepth, (math.log(to - from)/math.log(2)).toInt)
    println(s"Actual depth: ${_maxDepth}")

    def _reduce(from: Int, to: Int, depth: Int): A = {
      if (depth == _maxDepth) {
        var _acc = in(from)
        for (i <- (from + 1) until to) {
          _acc = f(_acc, in(i))
        }
        _acc
      } else {
        val mid = (from + to) / 2
        val (r1, r2) = parallel(
          _reduce(from, mid, depth + 1),
          _reduce(mid, to, depth + 1)
        )
        f(r1, r2)
      }
    }

    if (from == to) acc
    else f(acc, _reduce(from, to, 0))
  }

  def reduce[A](t: Tree[A], o: Operation[A]): A = {
    t match {
      case Leaf(v) => v
      case Node(l, r) =>
        val (rl, rr) = parallel(reduce(l, o), reduce(r, o))
        o(rl, rr)
    }
  }

  def toList[A](t: Tree[A]): List[A] = {
    val f = (x: A) => List(x)
    val t1 = map[A, List[A]](t, f)
    val concat: Operation[List[A]] = (l1, l2) => l1 ++ l2
    reduce[List[A]](t1, concat)
  }

  def map[A, B](t: Tree[A], f: A => B): Tree[B] ={
    t match {
      case Leaf(v) => Leaf(f(v))
      case Node(l, r) => Node(map(l, f), map(r, f))
    }
  }

  type Operation[A] = (A, A) => A

  trait Tree[A]
  case class Leaf[A](value: A) extends Tree[A] {
    override def toString = s"$value"
  }
  case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A] {
    override def toString = s"($left, $right)"
  }
}
