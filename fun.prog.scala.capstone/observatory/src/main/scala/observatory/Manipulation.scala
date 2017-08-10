package observatory

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Double)]): (Int, Int) => Double = {
    def grid(lat: Int, lon: Int): Double = Visualization.predictTemperature(temperatures, Location(lat, lon))

    grid
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Double)]]): (Int, Int) => Double = {
    def avgTemp(lat: Int, lon: Int): Double = {
      val (sum, count) = temperaturess.map { temps =>
        makeGrid(temps)(lat, lon)
      }.foldLeft((0.0, 0)) { (acc, item) =>
        (acc._1 + item, acc._2 + 1)
      }

      sum / count.toDouble
    }

    avgTemp
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A sequence of grids containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Double)], normals: (Int, Int) => Double): (Int, Int) => Double = {
    def dev(lat: Int, lon: Int): Double = {
      val normal = normals(lat, lon)
      val temp = makeGrid(temperatures)(lat, lon)

      temp - normal
    }

    dev
  }
}

