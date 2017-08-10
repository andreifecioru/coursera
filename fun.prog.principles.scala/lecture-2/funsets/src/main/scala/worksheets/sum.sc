import scala.annotation.tailrec

def sum(f: Int => Int)(a: Int, b: Int): Int = {
  @tailrec
  def loop(acc: Int, a: Int): Int =
    if (a > b) acc
    else loop(acc + f(a), a + 1)

  loop(0, a)
}

def reduce(f: (Int, Int) => Int)(a: Int, b: Int) = {
  @tailrec
  def loop(acc: Int, a: Int): Int =
    if (a > b) acc
    else loop(f(acc, a), a + 1)

  if (a > b) throw new UnsupportedOperationException("a > b")
  loop(a, a + 1)
}

sum(x => x)(5, 6)

// if you want a function value, you need to treat it as a
// partially applied function
val summation = reduce(_ + _) _
summation(1, 4)

val factorial = reduce(_ * _) _
factorial(1, 4)
