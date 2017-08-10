def insert(x: Int, l: List[Int]): List[Int] = l match {
  case Nil => List(x)
  case head :: tail =>
    if (x > head) head :: insert(x, tail)
    else x :: l
}

def isort(in: List[Int]): List[Int] = in match {
  case head :: tail => insert(head, isort(tail))
  case Nil => Nil
}


isort(List(7,3,5,4,9))