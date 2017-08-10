pi(10000000)

def pi(points: Int): Double = {
  type Point = (Double, Double)

  def distance(point: Point): Double =
    math.sqrt(point._1 * point._1 + point._2 * point._2)

  def inCircle(point: Point): Boolean =
    distance(point) <= 1.0

  def generatePoints(points: Int) = {
    def _generate = {
      import scala.util.Random
      (0 until points).map(p => Random.nextDouble())
    }
    _generate zip _generate
  }

  def countInCircle(points: Int): Double = {
    if (points < 100000) generatePoints(points).count(inCircle)
    else {
      import common.parallel
//      println(s"Spawning threads (points: $points)")
      val (r1, r2) = parallel(countInCircle(points / 2), countInCircle(points/2))
      r1 + r2
    }
  }

  (countInCircle(points) / points.toDouble) * 4.0
}