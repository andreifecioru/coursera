package apps

import common.parallel

object ParMap extends App {

  val a = (0 until 1e7.toInt).toArray[Int]
  val b = new Array[Int](a.length)
  val f = (x: Int) => x + 5

  mapOnArrays(a, b, 0, a.length, f, 4)

  b.take(10).foreach { x =>
    print(s"$x ")
  }

  println()

  def mapOnArrays[A, B](in: Array[A], out: Array[B], from: Int, to: Int, f: A => B, maxDepth: Int): Unit = {
    require(from < to)
    require(from >= 0)
    require(to <= in.length)
    require(to <= out.length)

    val _maxDepth = math.min(maxDepth, (math.log(to - from)/math.log(2)).toInt)
    println(s"Actual max depth: ${_maxDepth}")

    def _map(from: Int, to: Int, depth: Int): Unit = {
      if (depth == _maxDepth) {
        for (i <- from until to) {
          out(i) = f(in(i))
        }
      } else {
        val mid = (from + to) / 2
        parallel(_map(from, mid, depth + 1), _map(mid, to, depth + 1))
      }
    }

    _map(from, to, 0)
  }
}
