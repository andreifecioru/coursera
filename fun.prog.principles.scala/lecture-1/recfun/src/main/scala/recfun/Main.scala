package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int =
    (c, r) match {
      case _ if (c < 0) || (r < 0) => 0
      case (0, _) | (_, 0) => 1
      case _ if r == c => 1
      case _ => pascal(c - 1, r - 1) + pascal(c, r - 1)
    }
  
  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    def process(chars: List[Char], acc: Int): Boolean =
      if (acc < 0) false
      else chars match {
        case Nil => acc == 0
        case first :: rest =>
          first match {
            case '(' => process(rest, acc + 1)
            case ')' => process(rest, acc - 1)
            case _ => process(rest, acc)
          }
      }

    process(chars, 0)
  }
  
  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def count(n: Int, m: Int): Int =
      (n, m) match {
        case (0, _) => 1
        case _ if (n < 0) || (m < 0) => 0
        case _ => count(n, m - 1) + count(n - coins(m), m)
      }

    count(money, coins.length - 1)
  }
}
