package de.sciss.tsp

/**
 * This class allows us to take a line from a file and parse it
 * to extract the intended variables
 */
final case class Token(id: Int, x: Double, y: Double) {
  val point: Point = Point(x, y)
}