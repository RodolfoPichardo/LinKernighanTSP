/*
 *  Main.scala
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

import java.io.File
import java.util.Scanner

import scala.util.Try

object Main {
  private def mkListOfFiles(): Array[File] = {
    val folder      = new File("data/")
    val listOfFiles = folder.listFiles()
    if (listOfFiles == null) Array.empty else listOfFiles
  }

  private def getDatasetId(listOfFiles: Array[File]): Int = {
    println("Starting...")
    val numFiles = listOfFiles.length
    for (i <- 0 until numFiles) {
      val name = listOfFiles(i).getName
      if (listOfFiles(i).isFile && name.substring(name.length - 3).equalsIgnoreCase("tsp")) {
        println("  [" + i + "] " + listOfFiles(i).getName)
      }
    }
    val scanner = new Scanner(System.in)
    var idx = 0
    while ( {
      print("Select the dataset to test: ")
      idx = scanner.nextInt()

      (idx >= numFiles || idx < 0)
    }) ()
    idx
  }

  def main(args: Array[String]): Unit = {
    val listOfFiles = mkListOfFiles()

    var idx = -1

    if (args.length > 0) {
      idx = Try(args(0).toInt).getOrElse(-1)
    }

    if (idx < 0) idx = getDatasetId(listOfFiles)

    // Read the file
    val (ids, coordinates) = Interpreter(listOfFiles(idx))
    require (ids.size == coordinates.size)
    // Create the instance of the problem
    val costs = Point.coordinatesToCostTable(coordinates)
    val tour0 = LinKernighan.createRandomTour(ids.size, seed = 0L)
    val lk    = LinKernighan(costs, tour0)

    // Time keeping
    val startMillis = System.currentTimeMillis()
    // Show the results even if shutdown
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        println(s"The solution took: ${System.currentTimeMillis() - startMillis}ms")
        println("The solution is: ")
        println(lk)
      }
    })
    lk.run()
  }
}