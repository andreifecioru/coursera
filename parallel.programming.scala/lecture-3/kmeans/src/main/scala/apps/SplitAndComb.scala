package apps


object SplitAndComb extends App {
  val a = (0 until 10).toArray

  val ma = MyArray(a)
  val sum = ma.foldLeft(0)(_ + _)

  println(s"Sum: $sum")

  trait MyIterator[T] {
    def next(): T
    def hasNext: Boolean
  }

  trait LeftFoldable[T] extends MyIterator[T] {
    def foldLeft[B](acc: B)(f: (B, T) => B): B = {
      var _acc = acc
      while(hasNext) {
        _acc = f(_acc, next())
      }

      _acc
    }
  }

  case class MyArray[T](in: Array[T]) extends LeftFoldable[T] {
    private[this] var idx: Int = 0

    def next(): T =
      if (idx >= in.length) throw new Error("end of array")
      else {
        val elem = in(idx)
        idx += 1
        elem
      }

    def hasNext: Boolean = idx < in.length
  }



}
