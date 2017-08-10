import List._

Nil.toString
val c1 = Cons(5, Cons(2, Cons(3, Cons(4, Cons(1, Nil)))))
val c2 = Cons('a', Cons('c', Cons('b', Cons('e', Cons('d', Nil)))))
val c3 = Cons('A', Cons(c1, Cons(c2, Nil)))
val c4 = Cons(-5, Cons(-2, Cons(3, Cons(4, Cons(1, Nil)))))
val c5 = Cons(-5, Cons(-5, Cons(-2, Cons(3, Cons(3, Cons(3, Cons(4, Cons(1, Cons(1, Nil)))))))))

c1.head
c1.tail
c1.last
c1.init
c1.size

c1.take(2)
c1.take(0)
c1.take(10)

c1.drop(2)
c1.drop(0)
c1.drop(10)

c1.splitAt(0)
c1.splitAt(2)
c1.splitAt(10)

c1.removeAt(0)
c1.removeAt(2)

c1.concat(c2)
c1.concat(c2).reverse

c3.flatten

isort(c1)
msort(c1)
msort(c2)

c1.map(_ * 2)
c1.map(_ * 2.0)
c1.filter(_ % 2 == 0).map(x => x * x)

c4.span(_ < 0)

c5.reverse
pack(c5)
encode(c5)
c5.sizeFold
c5.mapFold(_ * 10)
c5.foldLeft(0)(_ + _)
c5.foldLeft(1)(_ * _)
c5.foldLeft("")((s, i) => s"$s|${i.toString}")
c5.foldRight("")((i, s) => s"$s|${i.toString}")

case class Cons[T](head: T, tail: List[T]) extends List[T] {
  def isEmpty = false
}

trait List[+T] {
  def head: T
  def tail: List[T]
  def isEmpty: Boolean

  def last: T = this match {
    case Nil => throw new Error("Nil.last")
    case Cons(h, Nil) => h
    case Cons(h, rest) => rest.last
  }

  def init: List[T] = {
    this match {
      case Nil => throw new Error("Nil.init")
      case Cons(h, Nil) => Nil
      case Cons(h, Cons(_, Nil)) =>
        Cons(h, Nil)
      case Cons(h, rest) =>
        Cons(h, rest.init)
    }
  }

  def concat[U >: T](other: List[U]): List[U] =  this match {
    case Nil => other
    case Cons(h, rest) => Cons(h, rest.concat(other))
  }

  def reverse: List[T] = this match {
    case Nil => Nil
    case Cons(h, rest) => rest.reverse.concat(Cons(h, Nil))
  }

  def removeAt(n: Int): List[T] = this match {
    case Nil => throw new Error("Nil.removeAt")
    case Cons(h, rest) if n == 0 => rest
    case Cons(h, rest) if n < 0 => this
    case Cons(h, rest) => Cons(h, rest.removeAt(n - 1))
  }

  def flatten: List[Any] = this match {
    case Nil => Nil
    case Cons(_h @ Cons(_, _), _rest) => _h.flatten.concat(_rest.flatten)
    case Cons(h, rest) => Cons(h, rest.flatten)
  }

  def size: Int = this match {
    case Nil => 0
    case Cons(h, rest) => 1 + rest.size
  }

  def take(n: Int): List[T] = this match {
    case Nil => Nil
    case Cons(h, rest) if n > 0 => Cons(h, rest.take(n - 1))
    case _ => Nil
  }

  def drop(n: Int): List[T] = this match {
    case Nil => Nil
    case Cons(h, rest) if n > 0 => rest.drop(n - 1)
    case _ => this
  }

  def splitAt(n: Int): (List[T], List[T]) = this match {
    case Nil => (Nil, Nil)
    case Cons(h, rest) if n > 0 =>
      val result = rest.splitAt(n - 1)
      (Cons(h, result._1), result._2)
    case _ => (Nil, this)
  }

  def map[U](f: (T) => U): List[U] = this match {
    case Nil => Nil
    case Cons(h, rest) => Cons(f(h), rest.map(f))
  }

  def filter(f: T => Boolean): List[T] = this match {
    case Nil => Nil
    case Cons(h, rest) =>
      if (f(h)) Cons(h, rest.filter(f))
      else rest.filter(f)
    }

  def span(p: (T) => Boolean): (List[T], List[T]) = this match {
    case Nil => (Nil, Nil)
    case Cons(h, rest) =>
      if (p(h)) {
        val result = rest.span(p)
        (Cons(h, result._1), result._2)
      } else (Nil, this)
  }

  def foldLeft[U](id: U)(f: (U, T) => U): U = this match {
    case Nil => id
    case Cons(h, rest) => f(rest.foldLeft(id)(f), h)
  }

  def foldRight[U](id: U)(f: (T, U) => U): U = this match {
      case Nil => id
      case Cons(h, rest) => f(h, rest.foldRight(id)(f))
  }

  def sizeFold: Int = this.foldRight(0)((_, i) => i + 1)

  def mapFold[U](f: (T) => U): List[U] = this match {
    case Nil => Nil
    case Cons(h, rest) => rest.foldRight(Cons(f(h), Nil))((item, acc) => Cons(f(item), acc))
  }

  override def toString = this match {
    case Nil => "Nil"
    case Cons(h, rest) => s"$h, $rest"
  }
}

object List {

  object Nil extends List[Nothing] {
    def head = throw new Error("Nil.head")
    def tail = throw new Error("Nil.tail")

    def isEmpty = true
  }

  def isort[T](in: List[T])(implicit ord: Ordering[T]): List[T] = {
    def insert(x: T, xs: List[T]): List[T] =
      xs match {
        case Nil => Cons(x, Nil)
        case Cons(h, rest) =>
          if (ord.gt(x, h)) Cons(h, insert(x, rest))
          else Cons(x, xs)
      }

    in match {
      case Nil => Nil
      case Cons(h, rest) => insert(h, isort(rest))
    }
  }

  def msort[T](in: List[T])(implicit ord: Ordering[T]): List[T] = {
    def merge(l1: List[T], l2: List[T]): List[T] =
      (l1, l2) match {
        case (Nil, _) => l2
        case (_, Nil) => l1
        case (Cons(h1, rest1), Cons(h2, rest2)) =>
          if (ord.lt(h1, h2)) Cons(h1, merge(rest1, l2))
          else Cons(h2, merge(l1, rest2))
      }

    in match {
      case Nil | Cons(_, Nil) => in
      case _ =>
        val (fst, snd) = in.splitAt(in.size / 2)
        merge(msort(fst), msort(snd))
    }
  }


  def pack[T](in: List[T])(implicit ord: Ordering[T]): List[List[T]] = in match {
    case Nil => Nil
    case Cons(h, rest) =>
      val (prefix, suffix) = rest.span(ord.equiv(_, h))
      Cons(Cons(h, prefix), pack(suffix))
  }

  def encode[T](in: List[T])(implicit ord: Ordering[T]): List[(T, Int)] =
    pack(in).map(l => (l.head, l.size))

  def apply[T]: List[T] = Nil
  def apply[T](x: T): List[T] = Cons(x, Nil)
  def apply[T](x: T, y: T): List[T] = Cons(x, Cons(y, Nil))
}


