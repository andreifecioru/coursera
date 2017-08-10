import common._

val data = (0 to 10).toArray

pnorm(data, 2)

def pnorm(xs: Array[Int], p: Double): Int = {
  def power(b: Int, e: Double): Int =
    math.exp(p * math.log(math.abs(b))).toInt

  def sumSegment(b: Int, e: Int): Int = {
    if (b >= e) 0
    else power(xs(b), p) + sumSegment(b + 1, e)
  }


  def compute(b: Int, e: Int): Int = {
    if ((e - b) < 4) sumSegment(b, e)
    else {
      val m = b + (e - b) / 2
      val (r1, r2) = parallel(compute(b, m), compute(m, e))
      r1 + r2
    }
  }

  compute(0, xs.length)
}