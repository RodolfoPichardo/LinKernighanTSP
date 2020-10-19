/*
 *  Edge.scala
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

object Edge {
  def apply(a: Int, b: Int): Edge = {
    val endPoint1 = if (a > b) a else b
    val endPoint2 = if (a > b) b else a
    new Edge(_1 = endPoint1, _2 = endPoint2)
  }
}
final case class Edge private(_1: Int, _2: Int)