package observatory


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {

  test("[#2 - Raw data display] color interpolation") {
    val scale = List(
      0.0 -> Color(255, 0, 0),
      1.0 -> Color(0, 0, 255)
    )

    val value = 0.5

    val result = Visualization.interpolateColor(scale, value)

    assert(result == Color(128, 0, 128))
  }

  test("[#2 - Raw data display] exceeding the greatest value of a color scale should return the color associated with the greatest value") {
    val scale = List(
      0.0 -> Color(255, 0, 0),
      -1.0 -> Color(0, 0, 255),
      -2.0 -> Color(255, 0, 255)
    )

    val value = 2.0

    val result = Visualization.interpolateColor(scale, value)

    assert(result == Color(255, 0, 255))
  }

  test(
    """[#2 - Raw data display]
      |predicted temperature at location z should be closer to known temperature at
      |location x than to known temperature at location y, if z is closer (in distance)
      |to x than y, and vice versa""".stripMargin) {

    val location_1 = Location(0.0, 0.0)
    val location_2 = Location(10.0, 10.0)
    val temperatures = List(
      location_1 -> 10.0,
      location_2 -> 20.0
    )

    Range(0, 11).map(x => Location(x, x)).foreach { location =>
      val result = Visualization.predictTemperature(temperatures, location)
      println(s"Location: $location -> $result. Distances: ${Visualization.d(location_1, location)} | ${Visualization.d(location_2, location)}")
    }

  }
}
