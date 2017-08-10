package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {
  def d(x1: Location, x2: Location): Double = {
    /* Formula:
      delta_lambda = abs(x1.lon - x2.lon)

      delta_sigma = arccos(
        sin(x1.lat) * sin(x2.lat) +
        cos(x1.lat) * cos(x2.lat) * cos(delta_lambda)
      )

      result = radius_km * delta_sigma
     */
    import Math._

    val radius_km = 6371

    val delta_lambda = toRadians(abs(x1.lon - x2.lon))

    val delta_sigma = acos(
      sin(toRadians(x1.lat)) * sin(toRadians(x2.lat)) +
      cos(toRadians(x1.lat)) * cos(toRadians(x2.lat)) * cos(delta_lambda)
    )

    radius_km * delta_sigma
  }


  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    val p = 4.0

    def w(x: Location, xi: Location): Double = {
      1.0 / Math.pow(d(x, xi), p)
    }

    def u(x: Location): Double = {
      temperatures.find(t => d(t._1, x) < 1.0).map(_._2)
        .getOrElse {
          val result = temperatures.foldLeft((0.0, 0.0)) { (acc, temp) =>
            val _w = w(x, temp._1)
            val nom = acc._1 + _w * temp._2
            val denom = acc._2 + _w
            (nom, denom)
          }

          result._1 / result._2
        }
    }

    u(location)
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    def interpolate(x: Double, pts: Iterable[(Double, Int)]): Int = {
      def findInterval(intervals: Iterable[((Double, Int), (Double, Int))]): ((Double, Int), (Double, Int)) = {
        require(intervals.nonEmpty)

        val head = intervals.head
        val rest = intervals.tail

        if (rest.isEmpty || x < head._2._1) head
        else findInterval(rest)
      }

      def _interpolate(interval: ((Double, Int), (Double, Int))): Int = {
        val pt0 = interval._1
        val pt1 = interval._2

        if (pt0._1 >= pt1._1) {
          points.foreach {
            case (v, c) => println(s"value: $v > $c")
          }
        }

        require(pt0._1 < pt1._1, s"Requirement one > pt0: ${pt0._1} | pt1: ${pt1._1}")
//        require(pt0._1 <= x, s"Requirement two > pt0: ${pt0._1} | x: $x")


        if (x <= pt0._1) pt0._2
        else if (x >= pt1._1) pt1._2
        else {
          /**
            * Formula:
            * y = (y0 * (x1 - x) + y1 * (x - x0)) / (x1 - x0)
            */
          val result = (
            pt0._2 * (pt1._1 - x) + pt1._2 * (x - pt0._1)
            ) / (pt1._1 - pt0._1)

          result.round.toInt
        }
      }

      _interpolate(findInterval(pts.zip(pts.tail)))
    }

    val sortedPoints = points.toList.sortBy(_._1)
    val redChannel = sortedPoints.map { case (v, c) => v -> c.red }
    val greenChannel = sortedPoints.map { case (v,c) => v -> c.green }
    val blueChannel = sortedPoints.map { case (v, c) => v -> c.blue }

    Color(
      interpolate(value, redChannel),
      interpolate(value, greenChannel),
      interpolate(value, blueChannel)
    )
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    val matrix = for { y <- Range(0, 180); x <- Range(0, 360) } yield x -> y

    val colorMap = matrix.map {
      case (x, y) => (x - 180, 90 - y)
    }.map {
      case (lon, lat) => predictTemperature(temperatures, Location(lat, lon))
    }.map { temp =>
      interpolateColor(colors, temp)
    }.map { color =>
      Pixel(color.red, color.green, color.blue, 255)
    }.toArray

    Image(360, 180, colorMap)
  }
}

