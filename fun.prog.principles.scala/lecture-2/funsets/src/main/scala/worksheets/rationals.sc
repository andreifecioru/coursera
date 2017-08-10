val x = new Rational(4, 6)
val y = new Rational(9, 18)

x + y
x - y

x < y
x.max(y)


class Rational(x: Int, y: Int) extends Ordered[Rational] {
  require(y > 0, "Denominator needs to be positive")

  private val simplifyBy = abs(gcd(x, y))

  def nom = x / simplifyBy

  def denom = y / simplifyBy

  // aux. constructor
  def this(x: Int) = this(x, 1)

  def +(that: Rational) = {
    new Rational(
      nom * that.denom + that.nom * denom,
      denom * that.denom
    )
  }
  def unary_- = new Rational(-nom, denom)

  def -(that: Rational) = - that + this

  def max(that: Rational) = if (this < that) that else this

  override def toString = s"$nom/$denom"

  private def gcd(x: Int, y: Int): Int =
    if (y == 0) x
    else gcd(y, x % y)

  private def abs(x: Int): Int = if (x > 0) x else -x

  def compare(that: Rational): Int =
    (nom * that.denom) compare (denom * that.nom)
}

