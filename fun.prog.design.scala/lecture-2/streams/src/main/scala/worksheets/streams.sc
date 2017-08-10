from(100).take(10).toList
fibonacci.take(10).toList
factorial.take(10).toList

listToStream((1 to 100).toList).take(10).toList

primes.take(100).toList

sqrt(2, 1e-5)

def fibonacci: Stream[Int] = {
  def next(p: Int, c: Int): Stream[Int] = c #:: next(c, p + c)
  0 #:: next(0, 1)
}

def factorial: Stream[Int] = {
  def next(p: Int): Stream[Int] = (p * (p + 1)) #:: next(p + 1)
  1 #:: next(1)
}

def from(n: Int): Stream[Int] = n #:: from(n + 1)

def listToStream[T](l: List[T]): Stream[T] = l.head #:: listToStream(l.tail)

def primes: Stream[Int] = {
  def sieve(s: Stream[Int]): Stream[Int] =
    s.head #:: sieve(s.tail filter (_ % s.head != 0))

  sieve(from(2))
}

def sqrt(x: Double, eps: Double) = {
  def sqrtStream: Stream[Double] = {
    def improve(guess: Double): Double = (guess + x / guess) / 2
    lazy val guesses:Stream[Double] = 1.0 #:: guesses.map(improve)
    guesses
  }

  sqrtStream.filter(v => Math.abs(v * v - 2) < eps).head
}
