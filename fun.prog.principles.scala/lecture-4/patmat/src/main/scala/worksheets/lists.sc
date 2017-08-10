trait List[T] {
  def head: T
  def tail: List[T]
  def isEmpty: Boolean
}

class Nil[T] extends List[T] {
  def head: T = throw new NoSuchElementException("emtpy")
  def tail: List[T] = throw new NoSuchElementException("emtpy")
  def isEmpty: Boolean = true
  override def toString: String = "Nil"
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty: Boolean = false
  override def toString: String =
    s"$head, $tail"
}

object List {
  def apply[T](): List[T] = new Nil[T]
  def apply[T](x1: T): List[T] = new Cons[T](x1, new Nil)
  def apply[T](x1: T, x2: T): List[T] = new Cons(x1, new Cons(x2, new Nil))
}

List()
List(1)
List(1, 2)