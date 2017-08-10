def abs(x: Double) = if (x >= 0) x else -x

def sqrt(x: Double, th: Double): Double = {
  def guess(e: Double): Double = {
    def isGoodEnough(g: Double) = abs(e - g) < th
    def improve() = (x/e + e) / 2.0

    val g = improve()
    if (isGoodEnough(g)) g else guess(g)
  }

  guess(1)
}

println(sqrt(2, 0.0001))
println(sqrt(1e-6, 0.0001))
println(sqrt(1e60, 0.0001))

