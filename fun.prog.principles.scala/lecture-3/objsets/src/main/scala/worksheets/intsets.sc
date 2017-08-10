import FunSets._

val t1 = Empty.include(7)
    .include(10)
    .include(5)
    .include(8)
    .include(13)

val t2 = Empty.include(6)
    .include(3)
    .include(2)
    .include(10)
    .include(9)
    .include(7)
    .include(11)
    .include(12)

val u = t1.union(t2)
u.inOrder

object FunSets {

  sealed trait IntSet {
    def include(x: Int): IntSet

    def contains(x: Int): Boolean

    def inOrder: List[Int]

    def union(that: IntSet): IntSet
  }

  object Empty extends IntSet {
    def contains(x: Int): Boolean = false

    def include(x: Int): IntSet = new NonEmpty(x, Empty, Empty)

    val inOrder = Nil

    def union(that: IntSet): IntSet = that

    override def toString: String = "-"
  }

  case class NonEmpty(v: Int, l: IntSet, r: IntSet) extends IntSet {
    def contains(x: Int): Boolean =
      if (x == v) true
      else {
        if (x < v) l.contains(x)
        else r.contains(x)
      }

    def include(x: Int): IntSet = {
      if (x < v) new NonEmpty(v, l.include(x), r)
      else if (x > v) new NonEmpty(v, l, r.include(x))
      else this
    }

    def union(that: IntSet): IntSet = that match {
      case NonEmpty(value, left, right) =>
        include(value).union(left).union(right)
      case Empty => this
    }

    override def toString: String = {
      s"(${l.toString} $v ${r.toString})"
    }

    def inOrder: List[Int] = {
      l.inOrder ::: List(v) ::: r.inOrder
    }
  }
}
