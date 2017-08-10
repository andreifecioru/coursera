package apps

import org.scalameter.Warmer.Default

import util.control.Breaks._
import common.parallel
import org.scalameter._

object ParMerge extends App {
  val dim = 1e7.toInt
  val r = util.Random

  val a = new Array[Int](dim)
  (0 until dim).foreach { i =>
    a(i) = r.nextInt(dim)
  }
  val time = config(
    Key.exec.minWarmupRuns -> 10,
    Key.exec.maxWarmupRuns -> 40,
    Key.verbose -> true
  ) withWarmer new Default measure  {
    parMergeSort(a, 2)
  }

  println(s"Parallel merge sort execution time: $time ms")
  a.take(10).foreach { x =>
    print(s"$x ")
  }

  def parMergeSort(xs: Array[Int], maxDepth: Int): Array[Int] = {
    val ys = new Array[Int](xs.length)
    val _maxDepth: Int = math.min(maxDepth, (math.log(xs.length) / math.log(2)).toInt)
    println(s"[parMergeSort] Actual depth: ${_maxDepth}")

    def copy(src: Array[Int], dst: Array[Int], from: Int, to: Int, maxDepth: Int): Unit = {
      require(from < to)
      require(from >= 0)
      require(to <= src.length)
      require(to <= dst.length)
      val _maxDepth: Int = math.min(maxDepth, (math.log(to - from) / math.log(2)).toInt)
//      println(s"[copy] Actual depth: ${_maxDepth}")

      def _copy(from: Int, to: Int, depth: Int): Unit = {
        if (depth == _maxDepth) {
          Array.copy(src, from, dst, from, to - from)
        } else {
//          println(s"Going deeper: $depth")
          val mid = (from + to) / 2
          parallel(_copy(from, mid, depth + 1), _copy(mid, to, depth + 1))
        }
      }

      _copy(from, to, 0)
    }

    def merge(src: Array[Int], dst: Array[Int],
              from: Int, to: Int, mid: Int): Unit = {
      var r1 = from
      var r2 = mid
      var w = from

      breakable {
        while (true) {
          if (r1 == mid) {
            Array.copy(src, r2, dst, w, to - r2)
            break
          }

          if (r2 == to) {
            Array.copy(src, r1, dst, w, mid - r1)
            break
          }

          if (src(r1) < src(r2)) {
            dst(w) = src(r1)
            r1 += 1
          } else {
            dst(w) = src(r2)
            r2 += 1
          }
          w += 1
        }
      }
    }

    def sort(from: Int, to: Int, depth: Int): Unit = {
      if (depth == _maxDepth) {
//        println(s"Max depth reached $depth. Segment len: ${to - from}")
        val temp = new Array[Int](to - from)
        Array.copy(xs, from, temp, 0, to - from)
        Array.copy(temp.sorted, 0, xs, from, to - from)
      } else {
        val mid = (to + from) / 2

        parallel(
          sort(from, mid, depth + 1),
          sort(mid, to, depth + 1)
        )
        val flip = (_maxDepth - depth - 1) % 2 == 0
        val src = if (flip) xs else ys
        val dst = if (flip) ys else xs

        merge(src, dst, from, to, mid)

//        println(s"-------- Depth: $depth ------")
//        if (flip)
//          println("Destination: ys")
//        else
//          println("Destination: xs")
//
//        print("src: ")
//        src.foreach { x =>
//          print(s"$x ")
//        }
//        print("\ndst: ")
//        dst.foreach { x =>
//          print(s"$x ")
//        }
//        println(s"\n-----------------------------\n")

        //noinspection EqualityToSameElements
        if ((depth == 0) && dst != xs) {
          println("Performing final copy")
          copy(dst, xs, 0, dst.length, _maxDepth)
        }
      }
    }

    sort(0, xs.length, 0)
    xs
  }
}
