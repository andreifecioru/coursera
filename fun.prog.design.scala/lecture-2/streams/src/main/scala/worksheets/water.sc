import WaterContainers._

val large = Glass.empty(9)
val small = Glass.empty(4)

//Glass.pourings(large, small).filter {
//  case (l, s) =>
//    println(s"large: $l")
//    l.capacity == 3
//}.take(1).toList
Glass.computePourings(large, small, 3)


object WaterContainers {

  case class Glass(capacity: Int, filled: Int) extends Ordered[Glass] {
    require(capacity >= 0)
    require(filled >= 0)
    require(filled <= capacity)

    lazy val available = capacity - filled
    lazy val isEmpty = filled == 0
    lazy val isFilled = available == 0

    def fillUp = Glass.filled(capacity)
    def empty = Glass.empty(capacity)

    override def compare(that: Glass): Int = capacity - that.capacity
  }

  object Glass {
    def empty(capacity: Int): Glass = {
      Glass(capacity, 0)
    }

    def filled(capacity: Int): Glass = {
      Glass(capacity, capacity)
    }

    def pour(from: Glass, to: Glass): (Glass, Glass, String) = {
      (from, to) match {
        case _ if from.isEmpty =>
          val msg = s"Pouring ${from.capacity} from faucet to $from"
          (from.fillUp, to, msg)
        case _ if to.isFilled =>
          val msg = s"Pouring ${to.filled} from $to to sink"
          (from, to.empty, msg)
        case _ =>
          val howMuch = Math.min(from.filled, to.available)
          val msg = s"Pouring $howMuch from $from to $to"

          (
            Glass(from.capacity, from.filled - howMuch),
            Glass(to.capacity, to.filled + howMuch),
            msg
          )
      }
    }

    def pourings(large: Glass, small: Glass, message: String): Stream[(Glass, Glass, String)] = {
      require(large > small)

      lazy val (l, s, msg) = pour(large, small)
      (large, small, message) #:: pourings(l, s, msg)
    }

    def computePourings(g1: Glass, g2: Glass, target: Int): List[(Glass, Glass, String)] = {
      val (large, small) = if (g1 > g2) (g1, g2) else (g2, g1)

      require(target <= large.capacity)

      pourings(large, small, "starting ...").takeWhile {
        case (l, _, _) => l.filled != target
      }.toList
    }
  }
}
