package apps

import common.parallel
object ParTreeMap extends App {
  val a = (0 until 100).toArray[Int]

  val t = arrayToTree(a, 10)
//  println(t)

  val f = (x: Int) => x * 2
  val t1 = parMap(t, f)
  val b = treeToArray(t1)
  println(b.mkString(" "))

  trait Tree[T] {
    def size: Int
  }

  case class Leaf[T](data: Array[T]) extends Tree[T] {
    def size = data.length
    override def toString = data.mkString(" ")
  }

  case class Node[T](left: Tree[T], right: Tree[T]) extends Tree[T] {
    def size = left.size + right.size
    override def toString = s"($left, $right)"
  }

  def arrayToTree[T: Manifest](in: Array[T], depth: Int): Tree[T] = {
    val _depth = math.min(depth, (math.log(in.length) / math.log(2)).toInt)
    println(s"Actual depth: ${_depth}")

    def _toTree(from: Int, to: Int, depth: Int): Tree[T] = {
      if (depth == _depth) {
        val temp = new Array[T](to - from)
        Array.copy(in, from, temp, 0, to - from)
        Leaf(temp)
      } else {
        val mid = (from + to) / 2
        val (l, r) = parallel(_toTree(from, mid, depth + 1), _toTree(mid, to, depth + 1))
        Node(l, r)
      }
    }

    _toTree(0, in.length, 0)
  }

  def treeToArray[T: Manifest](in: Tree[T]): Array[T] = {
    in match {
      case Leaf(data) =>
        data
      case Node(l, r) =>
        val (rl, rr) = parallel(treeToArray(l), treeToArray(r))

        val result = new Array[T](l.size + r.size)
        Array.copy(rl, 0, result, 0, l.size)
        Array.copy(rr, 0, result, l.size, r.size)
        result
    }
  }

  def parMap[A: Manifest, B: Manifest](in: Tree[A], f: A => B): Tree[B] = {
    def _map(t: Tree[A]): Tree[B] = {
      t match {
        case Leaf(data) =>
          val b = new Array[B](data.length)
          for (i <- data.indices) {
            b(i) = f(data(i))
          }
          Leaf(b)
        case Node(l, r) =>
          val (rl, rr) = parallel(_map(l), _map(r))
          Node(rl, rr)
      }
    }

    _map(in)
  }
}
