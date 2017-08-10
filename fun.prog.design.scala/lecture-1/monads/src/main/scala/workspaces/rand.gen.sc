import Generators._

IntGenerator.generate
BoolGenerator.generate
PairGenerator.generate
ListGenerator.generate
TreeGenerator.generate

NilTree.insert(6).insert(1,2,5).insert(7).insert(10, 11)

object Generators {

  trait Tree[+T] {
    def x: T
    def isNil: Boolean
    def left: Tree[T]
    def right: Tree[T]

    def insertElem[U >: T](v: U)(implicit ord: Ordering[U]): Tree[U] = this match {
      case NilTree => Node(v, NilTree, NilTree)
      case Node(_x, _l, _r) =>
        if (ord.lt(v, _x)) Node(x, _l.insertElem(v), _r)
        else Node(x, _l, _r.insertElem(v))
    }

    def insert[U >: T](vs: U*)(implicit ord: Ordering[U]): Tree[U] = vs.toList match {
      case Nil => this
      case h :: rest => insertElem(h).insert(rest: _*)
    }

    override def toString = s"($x, $left, $right)"
  }

  case class Node[T](x: T, left: Tree[T], right: Tree[T]) extends Tree[T] {
    val isNil = false
  }

  object NilTree extends Tree[Nothing] {
    val isNil = true
    def x = throw new Error("nil.x")
    def left = throw new Error("nil.left")
    def right = throw new Error("nil.right")

    override def toString = "-"
  }


  trait Generator[+T] {
    self =>

    def generate: T

    def map[U](f: T => U): Generator[U] =
      new Generator[U] {
        override def generate: U = f(self.generate)
      }

    def flatMap[U](f: T => Generator[U]): Generator[U] =
      new Generator[U] {
        def generate = f(self.generate).generate
      }
  }

  val IntGenerator = new Generator[Int] {
    import java.util.Random

    val r = new Random()

    def generate = r.nextInt()
  }

  val BoolGenerator = IntGenerator.map(_ > 0)

  val PairGenerator = for {
    x <- IntGenerator
    y <- IntGenerator
  } yield (x, y)

  val ListGenerator = new Generator[List[Int]] {
    def generate: List[Int] = BoolGenerator.generate match {
      case true => List.empty
      case false => IntGenerator.generate :: generate
    }
  }

  val TreeGenerator = new Generator[Tree[Int]] {
    override def generate: Tree[Int] = BoolGenerator.generate match {
      case true => NilTree
      case false => generate.insert(IntGenerator.generate)
    }
  }
}