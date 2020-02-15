package de.sciss.tsp

import java.io.File
import java.util.Scanner

import scala.util.Try

object Main {
  private def getListOfFiles: Array[File] = {
    val folder      = new File("data/")
    val listOfFiles = folder.listFiles
    if (listOfFiles == null) Array.empty else listOfFiles
  }

  private def getDatasetId(listOfFiles: Array[File]): Int = {
    System.out.println("Starting...")
    val numFiles = listOfFiles.length
    for (i <- 0 until numFiles) {
      val name = listOfFiles(i).getName
      if (listOfFiles(i).isFile && name.substring(name.length - 3).equalsIgnoreCase("tsp")) {
        System.out.println("  [" + i + "] " + listOfFiles(i).getName)
      }
    }
    val scanner = new Scanner(System.in)
    var idx = 0
    do {
      System.out.print("Select the dataset to test: ")
      idx = scanner.nextInt
    } while (idx >= numFiles || idx < 0)

    idx
  }

  def main(args: Array[String]): Unit = {
    System.out.println("Starting...")
    val listOfFiles = getListOfFiles

    var idx = -1

    if (args.length > 0) {
      idx = Try(args(0).toInt).getOrElse(-1)
    }

    if (idx < 0) idx = getDatasetId(listOfFiles)

    // Read the file
    val (ids, coordinates) = Interpreter(listOfFiles(idx))
    // Create the instance of the problem
    val lk = new LinKernighan(ids.size, coordinates, seed = 0L)

    // Time keeping
    val startMillis = System.currentTimeMillis()
    // Show the results even if shutdown
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        System.out.printf("The solution took: %dms\n", System.currentTimeMillis() - startMillis)
        System.out.println("The solution is: ")
        System.out.println(lk)
      }
    })
    lk.run()
  }
}