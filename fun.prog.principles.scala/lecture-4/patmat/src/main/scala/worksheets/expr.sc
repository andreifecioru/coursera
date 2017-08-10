trait Expr {
  def eval: Int = this match {
    case Num(n) => n
    case Sum(e1, e2) => e1.eval + e2.eval
  }

  def show: String = this match {
    case Num(n) => n.toString
    case Sum(e1, e2) => s"${e1.show} + ${e2.show}"
    case Prod(Sum(s1, s2), p2 @ Num(_)) => s"(${s1.show} + ${s2.show}) * ${p2.show}"
    case Prod(p1 @ Num(_), Sum(s1, s2)) => s"${p1.show} * (${s1.show} + ${s2.show})"
    case Prod(Sum(s1, s2), Sum(s3, s4)) => s"(${s1.show} + ${s2.show}) * (${s3.show} + ${s4.show})"
  }
}

case class Num(n: Int) extends Expr
case class Sum(e1: Expr, e2: Expr) extends Expr
case class Prod(e1: Expr, e2: Expr) extends Expr

Sum(Num(1), Num(2)).eval

Sum(Num(1), Num(2)).show
Sum(Num(1), Sum(Num(2), Num(3))).show

Prod(Num(1), Sum(Num(2), Num(3))).show
Prod(Sum(Num(0), Num(1)), Sum(Num(2), Num(3))).show
