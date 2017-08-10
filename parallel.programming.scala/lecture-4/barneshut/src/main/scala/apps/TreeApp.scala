package apps

object TreeApp extends App {

  val input = (0 until 10).toArray

  val t = Tree.fromArray(input)
  println(t.filter(_ % 2 == 0))



  trait Tree[+T] {
    def filter(p: T => Boolean): Tree[T] = {
      this match {
        case Empty => Empty
        case Leaf(v) => if (p(v)) this else Empty
        case Node(l, r) => Tree.buildNode(l.filter(p), r.filter(p))
      }
    }
  }

  case class Node[T](left: Tree[T], right: Tree[T]) extends Tree[T] {
    override def toString = s"<$left $right>"
  }
  case class Leaf[T](value: T) extends Tree[T] {
    override def toString = s"($value)"
  }

  object Empty extends Tree[Nothing]

  object Tree {
    def fromArray[T](input: Array[T]): Tree[T] = {
      require(input.length > 0)

      def _convert(from: Int, to: Int): Tree[T] = {
        if ((to - from) == 1) Leaf(input(from))
        else {
          val mid = (from + to) / 2
          Tree.buildNode(_convert(from, mid), _convert(mid, to))
        }
      }

      input.length match {
        case 0 => Empty
        case _ => _convert(0, input.length)
      }
    }

    def buildNode[T](l: Tree[T], r: Tree[T]): Tree[T] = {
      (l, r) match {
        case (Empty, Empty) => Empty
        case (_, Empty) => l
        case (Empty, _) => r
        case _ => Node(l, r)
      }
    }
  }
}
