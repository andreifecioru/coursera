package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    import Math._
    val n = pow(2.0, zoom)

    val lon = x / n * 360.0 - 180.0
    val lat = toDegrees(atan(sinh(PI * (1 - 2 * y / n))))

    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {
    val matrix = for { y <- Range(0, 256); x <- Range(0, 256) } yield x -> y

    val colorMap = matrix.map {
      case (_x, _y) => (x * 256 + _x, y * 256 + _y)
    }.map {
      case (_x, _y) => tileLocation(zoom + 8, _x, _y)
    }.map { location =>
      Visualization.predictTemperature(temperatures, location)
    }.map { temperature =>
      Visualization.interpolateColor(colors, temperature)
    }.map { color =>
      Pixel(color.red, color.green, color.blue, 127)
    }.toArray

    Image(256, 256, colorMap)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit
  ): Unit = {
    import Math._

    for {
      zoom <- Range(0, 4)
      (year, data) <- yearlyData
    } {
      val maxDim = pow(2, zoom).toInt
      val range = Range(0, maxDim)
      for {
        x <- range
        y <- range
      } {
        generateImage(year, zoom, x, y, data)
      }
    }
  }
}
