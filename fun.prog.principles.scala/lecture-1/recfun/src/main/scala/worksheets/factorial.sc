import scala.annotation.tailrec

def factorial(n : Int): Int =
  if (n == 0) 1
  else n * factorial(n - 1)


def factorial2(n: Int): Int = {
  @tailrec
  def fact(acc: Int, n: Int): Int =
    if (n == 0) acc
    else fact(n * acc, n - 1)

  fact(1, n)
}

factorial(3)
factorial2(3)
