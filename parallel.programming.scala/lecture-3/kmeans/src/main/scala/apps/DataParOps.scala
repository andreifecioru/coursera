package apps

object DataParOps extends App {
  val a = (0 to 10).toArray

  println(s"Sum: ${sum(a)}")
  println(s"Max: ${max(a)}")

  val name = "andrei"
  println(s"Vowel count: ${countVowels(name.toLowerCase.toArray)}")

  def sum(in: Array[Int]): Int = {
    in.par.fold(0)(_ + _)
  }

  def max(in: Array[Int]): Int = {
    if (in.length == 0) throw new Error("empty input array")

    in.par.reduce { (x, y) => if (x > y) x else y }
  }

  def countVowels(in: Array[Char]): Int = {
    val VOWELS = List('a', 'e', 'i', 'o', 'u')

    def _count(count: Int, char: Char): Int = if (VOWELS.contains(char)) count + 1 else count

    in.par.aggregate(0)(_count, _ + _)
  }

}
