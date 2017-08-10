package apps

import kmeans.Point

object KMeansSeq extends App {
  val r = util.Random
  def getRandPoint: Point = {
    def _newValue: Double = r.nextDouble * 10 - 5
    new Point(_newValue, _newValue, _newValue)
  }

  val points = List(
    new Point(-3d, 3d, 0d), new Point(-3d, 2d, 0), new Point(-1d, 2d, 0d),
    new Point(-1d, -1d, 0d), new Point(-2d, -2d, 0), new Point(-2d, -3d, 0d),
    new Point(3d, -1d, 0d), new Point(3d, -3d, 0),
    new Point(1d, 2d, 0d)
  )

  val means = List( getRandPoint, getRandPoint, getRandPoint )

  val result = kmeans(points, means, 1e-5)
  for ((mean, points) <- result) {
    println(s"Mean: $mean")
    points.foreach(println)
    println("---------------")
  }

  def kmeans(points: List[Point], means: List[Point], threshold: Double): Map[Point, List[Point]] = {
    println("Converging...")
    def hasConverged(prev: List[Point], next: List[Point]): Boolean =
      !prev.zip(next).exists { case (m1, m2) => m1.squareDistance(m2) >= threshold }

    val result = { for ( p <- points; m <- means ) yield (p, m, p.squareDistance(m)) }
        .groupBy(_._1)
        .map { case (p, l) =>
          val res = l.reduce { (acc, x) => if (x._3 < acc._3) x else acc }
          (p, res._2)
        }
        .groupBy(_._2)
        .map { case (_, l) =>
          val points = l.keys.toList
          val point_count = points.size
          val r = points.reduce { (p1, p2) => new Point(p1.x + p2.x, p1.y + p2.y, p1.z + p2.z) }
          val m = new Point(r.x / point_count, r.y / point_count, r.z / point_count)
          (m, l.keys.toList) }

    val newMeans = result.keys.toList

    if (hasConverged(means, newMeans)) result
    else kmeans(points, newMeans, threshold)
  }
}
