import Poly._

val l = Nil
    .append(1)
    .append(2)
    .append(3)
    .append(4)

l(0)
l(3)
l(-1)

object Poly {
  sealed trait List[T] {
    def isEmpty: Boolean
    def head:T
    def tail: List[T]
    def apply(i: Int): T
    def append(v: T): List[T]
  }

  class Cons[T](val head: T, val tail: List[T]) extends List[T] {
    def isEmpty = false

    def apply(i: Int) =
      if (i == 0) head
      else tail.apply(i - 1)

    def append(v: T) = new Cons(v, this)

    override def toString = s"$head, ${tail.toString}"
  }

  class Nil[T] extends List[T] {
    def isEmpty = true
    def head = throw new NoSuchElementException("Nil.head")
    def tail = throw new NoSuchElementException("Nil.tail")
    def apply(i: Int) = throw new IndexOutOfBoundsException("Nil.apply")

    def append(v: T) = new Cons(v, new Nil[T])

    override def toString = ""
  }

  val Nil = new Nil[Any]
}