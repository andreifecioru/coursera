package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = for {
    k <- arbitrary[Int]
    h <- oneOf(const(empty), genHeap)
  } yield insert(k, h)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("insert in empty heap") = forAll { (x: Int, y: Int) =>
    val min_val = x min y
    val max_val = x max y

    val h = insert(y, insert(x, empty))

    val m1 = findMin(h)
    val m2 = findMin(deleteMin(h))

    (min_val == m1) && (max_val == m2)
  }

  property("insert and delete empty heap") = forAll { (x: Int) =>
    val h = insert(x, empty)

    isEmpty(deleteMin(h))
  }

  property("delete from heap") = forAll { (l: List[Int]) =>
    def fromList(l: List[Int], h: H): H = l match {
      case Nil => h
      case fst :: rest => fromList(rest, insert(fst, h))
    }

    def toList(h: H, l: List[Int]): List[Int] = {
      if (isEmpty(h)) l
      else {
        val m = findMin(h)
        toList(deleteMin(h), m :: l)
      }
    }

    val expected = l.sorted.reverse
    expected == toList(fromList(l, empty), Nil)
  }

  property("heap re-insert min val") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("heap delete min val") = forAll {  (h: H) =>
    if (isEmpty(h)) true
    else {
      val m = findMin(h)
      val h1 = deleteMin(h)
      if (isEmpty(h1)) true
      else {
        val m1 = findMin(h1)
        m <= m1
      }
    }
  }

  property("merge") = forAll { (h1: H, h2: H) =>
    val h3 = meld(h1, h2)

    if (isEmpty(h3)) true
    else {
      val m = findMin(h3)
      val h4 = deleteMin(h3)
      if (isEmpty(h4)) true
      else {
        val m1 = findMin(h4)
        m <= m1
      }
    }
  }




}
