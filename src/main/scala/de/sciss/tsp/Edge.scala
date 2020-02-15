package de.sciss.tsp

final case class Edge(_1: Int, _2: Int) extends Comparable[Edge] {
  def compareTo(that: Edge): Int =
    if (this._1 < that._1 || this._1 == that._1 && this._2 < that._2) -1
    else if (this == that) 0
    else 1
}
