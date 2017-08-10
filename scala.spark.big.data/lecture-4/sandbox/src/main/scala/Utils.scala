import java.io.File

object Utils {
  def filePath(resourcePath: String): String = {
    new File(this.getClass.getClassLoader.getResource(resourcePath).toURI).getPath
  }

  implicit class StringImprovements(val s: String) {
    import scala.util.control.Exception.catching
    def toIntSafe = catching(classOf[NumberFormatException]) opt s.toInt
  }
}

