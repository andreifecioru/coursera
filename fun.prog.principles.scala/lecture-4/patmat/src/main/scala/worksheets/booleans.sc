object True extends Boolean {
  def ifThenElse[T](t: => T, e: => T): T = t

  override def toString: String = "True"
}

object False extends Boolean {
  def ifThenElse[T](t: => T, e: => T): T = e

  override def toString: String = "False"
}

abstract class Boolean {
  def ifThenElse[T](t: => T, e: => T): T

  def &&(other: Boolean): Boolean = ifThenElse(other, False)
  def ||(other: Boolean): Boolean = ifThenElse(True, other)

  def unary_!(): Boolean = ifThenElse(False, True)

  def ==(other: Boolean): Boolean = ifThenElse(other, !other)
  def !=(other: Boolean): Boolean = ifThenElse(!other, other)
  def <[T](other: Boolean): Boolean = ifThenElse(False, other)
}

False && True
!True
!False

True == True
True == False
False == False
False == True

True != True
True != False
False != False
False != True

True < True
True < False
False < False
False < True

