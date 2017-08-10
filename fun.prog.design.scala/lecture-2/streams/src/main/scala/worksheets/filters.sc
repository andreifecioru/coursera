(100 to 200).toStream.take(3).toList

nthOdd(10, 100, 6)
firstOdd(100, 200)


def firstOdd(lower: Int, upper: Int): Int = nthOdd(lower, upper, 0)

def nthOdd(lower: Int, upper: Int, nth: Int): Int =
  nthInteger(lower, upper, nth)(_ % 2 == 1)

def nthInteger(lower: Int, upper: Int, nth: Int)(p: Int => Boolean): Int = {
  if (lower >= upper) throw new Error("not found")

  if (p(lower)) {
    if (nth == 0) lower
    else nthInteger(lower + 1, upper, nth - 1)(p)
  }
  else nthInteger(lower + 1, upper, nth)(p)
}



