package apps

object ConcApp extends App {


  trait Conc[+T] {
    def level: Int
    def size: Int
    def isEmpty: Boolean

    def <>[U >: T](other: Conc[U]): Conc[U] = {
      if (this.isEmpty) other
      else if (other.isEmpty) this
      else ???
    }
  }

  case object Empty extends Conc[Nothing] {
    val level = 0
    val size = 0
    val isEmpty = true

    override def toString = "()"
  }

  case class Single[T](value: T) extends Conc[T] {
    val level = 0
    val size = 1
    val isEmpty = false

    override def toString = s"($value)"
  }

  case class <>[T](left: Conc[T], right: Conc[T]) extends Conc[T] {
    require(!left.isEmpty)
    require(!right.isEmpty)

    require(math.abs(left.level - right.level) < 1)

    val level = 1 + math.max(left.level, right.level)
    val size = left.size + right.size
    val isEmpty = false
  }
}
