package observatory

import observatory.LayerName.{Deviations, Temperatures}

/**
  * 6th (and last) milestone: user interface polishing
  */
object Interaction2 {

  /**
    * @return The available layers of the application
    */
  def availableLayers: Seq[Layer] = {
    val tempColors: Seq[(Double, Color)] = Seq(
      -60.0 -> Color(0 , 0, 0),
      -50.0 -> Color(33, 0, 107),
      -27.0 -> Color(255, 0, 255),
      -15.0 -> Color(0, 0, 255),
      0.0 -> Color(0, 255, 255),
      12.0 -> Color(255, 255, 0),
      32.0 -> Color(255, 0, 0),
      60.0 -> Color(255, 255, 255)
    )

    val devColors: Seq[(Double, Color)] = Seq(
      -7.0 -> Color(0 , 0, 255),
      -2.0 -> Color(0, 255, 255),
      0.0 -> Color(255, 255, 255),
      2.0 -> Color(255, 255, 0),
      4.0 -> Color(255, 0, 0),
      7.0 -> Color(0, 0, 0)
    )

    val bounds = Range(1975, 2016)

    Seq(
      Layer(Temperatures, tempColors, bounds),
      Layer(Deviations, devColors, bounds)
    )
  }

  /**
    * @param selectedLayer A signal carrying the layer selected by the user
    * @return A signal containing the year bounds corresponding to the selected layer
    */
  def yearBounds(selectedLayer: Signal[Layer]): Signal[Range] = {
    Signal(selectedLayer().bounds)
  }

  /**
    * @param selectedLayer The selected layer
    * @param sliderValue The value of the year slider
    * @return The value of the selected year, so that it never goes out of the layer bounds.
    *         If the value of `sliderValue` is out of the `selectedLayer` bounds,
    *         this method should return the closest value that is included
    *         in the `selectedLayer` bounds.
    */
  def yearSelection(selectedLayer: Signal[Layer], sliderValue: Signal[Int]): Signal[Int] = {
    Signal({
      val bounds = yearBounds(selectedLayer)()
      val slider = sliderValue()

      val maxYear = bounds.max
      val minYear = bounds.min

      slider match {
        case _ if bounds.contains(slider) => slider
        case _ if slider > maxYear => maxYear
        case _ if slider < minYear => minYear
        case _ =>
          throw new IllegalStateException(s"ERROR: bounds: $bounds | slider: $slider")
      }
    })
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The URL pattern to retrieve tiles
    */
  def layerUrlPattern(selectedLayer: Signal[Layer], selectedYear: Signal[Int]): Signal[String] = {
    Signal({
      s"/target/${selectedLayer().layerName.id}/${selectedYear()}/{z}/{x}/{y}.png"
    })
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The caption to show
    */
  def caption(selectedLayer: Signal[Layer], selectedYear: Signal[Int]): Signal[String] = {
    Signal({
      s"${if (selectedLayer().layerName == Temperatures) "Temperatures" else "Deviations" } (${selectedYear()})"
    })
  }

}

sealed abstract class LayerName(val id: String)
object LayerName {
  case object Temperatures extends LayerName("temperatures")
  case object Deviations extends LayerName("deviations")
}

/**
  * @param layerName Name of the layer
  * @param colorScale Color scale used by the layer
  * @param bounds Minimum and maximum year supported by the layer
  */
case class Layer(layerName: LayerName, colorScale: Seq[(Double, Color)], bounds: Range)

