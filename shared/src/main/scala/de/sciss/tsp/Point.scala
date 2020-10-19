/*
 *  Point.scala
 *  (LinKernighanTSP)
 *
 *  Copyright (c) 2017 Rodolfo Pichardo.
 *  Copyright (c) 2020 Hanns Holger Rutz.
 *
 *  This software is published under the MIT License
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.tsp

import scala.collection.immutable.{IndexedSeq => Vec}

object Point {
  /** Converts two dimensional coordinates to costs based on Cartesian distances. */
  def coordinatesToCostTable(coordinates: Vec[Point]): Array[Array[Double]] = {
    val size  = coordinates.size
    val res   = Array.ofDim[Double](size, size)
    var i = 0
    val sizeM1 = size - 1
    while (i < sizeM1) {
      val p1 = coordinates(i)
      var j = i + 1
      while (j < size) {
        val p2    = coordinates(j)
        val cost  = p1 distance p2
        res(i)(j) = cost
        res(j)(i) = cost
        j += 1
      }
      i += 1
    }
    res
  }
}
/** Two dimensional point. */
final case class Point(x: Double, y: Double) {
  def distanceSq(that: Point): Double = {
    val dx    = that.x - this.x
    val dy    = that.y - this.y
    dx * dx + dy * dy
  }

  def distance(that: Point): Double = math.sqrt(distanceSq(that))
}
