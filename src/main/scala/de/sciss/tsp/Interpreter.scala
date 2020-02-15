package de.sciss.tsp

import java.io.{BufferedReader, File, FileReader}
import java.util.StringTokenizer

import scala.collection.immutable.{IndexedSeq => Vec}

object Interpreter {
  /**
   * Reads a text file with edges, and returns the parsed data.
   *
   * @param   file the text file containing the edges.
   *               Each line is expected to hold data in the format
   *               `"[id] [x coordinate] [y coordinate]"`. Example: `"1 587.2 323.1"`
   * @return  the identifiers and coordinates
   */
  def apply(file: File): (Vec[Int], Vec[Point]) = {
    val in = new BufferedReader(new FileReader(file))
    var idB = Vec.newBuilder[Int]
    var cB  = Vec.newBuilder[Point]
    try {
      while ({
        val line = in.readLine()
        line != null && {
          val ln = line.trim
          if (!ln.contains(":") && ln != "NODE_COORD_SECTION" && ln != "EOF") {
            val tokens = getTokens(line)
            idB += tokens.id
            cB  += tokens.point
          }
          true
        }
      }) ()

    } finally {
      in.close()
    }
    (idB.result(), cB.result())
  }

  /*
   * This function takes a string and tokenizes it. It expects the string to be have exactly 3 tokens
   *
   * Expects data on this format `"[id] [x coordinate] [y coordinate]"`,
   * Example: `"1 587.2 323.1"`
   *
   * @param line the string one wants to tokenize
   * @return the token extracted.
   */
  private def getTokens(line: String): Token = {
    val tok = new StringTokenizer(line)
    val id  = tok.nextToken.toInt
    val x   = tok.nextToken.toDouble
    val y   = tok.nextToken.toDouble
    if (!tok.hasMoreTokens) Token(id, x, y)
    else throw new IllegalArgumentException
  }
}