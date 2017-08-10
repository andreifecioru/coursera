def fixed_p(f: Double => Double): Double = {
  def loop(e: Double): Double = {
    def isGoodEnough(g: Double): Boolean = Math.abs(g - e) < 0.0001
    val g = f(e)
//    println(s"Guess: $g")
    if (isGoodEnough(g)) g
    else loop(g)
  }

  loop(1)
}

def averageDamp(f: Double => Double)(x: Double) = (x + f(x)) / 2

def sqrt(x: Double) = fixed_p(averageDamp(x / _))

//fixed_p(1 + _ / 2)
//fixed_p(2 + _ / 3)
//fixed_p(Math.sqrt)

sqrt(2)
