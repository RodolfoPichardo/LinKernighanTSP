package de.sciss.tsp

object Edge {
  def apply(a: Int, b: Int): Edge = {
    val endPoint1 = if (a > b) a else b
    val endPoint2 = if (a > b) b else a
    new Edge(_1 = endPoint1, _2 = endPoint2)
  }
}
final case class Edge private(_1: Int, _2: Int) /*extends Comparable[Edge]*/ {
//  assert (_1 >= _2)

/*
  def compareTo(that: Edge): Int =
    if (this._1 < that._1 || this._1 == that._1 && this._2 < that._2) -1
    else if (this == that) 0
    else 1
*/
}
