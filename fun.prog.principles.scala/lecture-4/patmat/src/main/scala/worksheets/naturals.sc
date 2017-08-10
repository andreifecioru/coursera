object Nat {
  abstract class Nat {
    def isZero: Boolean
    def prev: Nat
    def next = NonZero(this)

    def +(other: Nat): Nat
    def -(other: Nat): Nat
  }

  case class NonZero(prev: Nat) extends Nat {
    val isZero = false

    def +(other: Nat): Nat = NonZero(prev + other)
    def -(other: Nat): Nat =
      if (other.isZero) this
      else prev - other.prev
  }

  val Zero: Nat = new Nat {
    val isZero = true
    def prev = throw new NoSuchElementException("0.prev")

    def +(other: Nat): Nat = other
    def -(other: Nat): Nat =
      if (other.isZero) this
      else throw new NoSuchElementException("0.prev")
  }
}

import Nat._

