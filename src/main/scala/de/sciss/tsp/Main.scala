package de.sciss.tsp

import java.io.File
import java.util.Scanner

object Main {
  def main(args: Array[String]): Unit = {
    System.out.println("Starting...")
    val folder      = new File("data/")
    val listOfFiles = folder.listFiles
    val numFiles    = if (listOfFiles == null) 0 else listOfFiles.length
    for (i <- 0 until numFiles) {
      val name = listOfFiles(i).getName
      if (listOfFiles(i).isFile && name.substring(name.length - 3).equalsIgnoreCase("tsp")) {
        System.out.println("  [" + i + "] " + listOfFiles(i).getName)
      }
    }
    @SuppressWarnings(Array("resource")) val scanner = new Scanner(System.in)
    var idx = 0
    do {
      System.out.print("Select the dataset to test: ")
      idx = scanner.nextInt
    } while ( {
      idx >= numFiles || idx < 0
    })
    // Read the file
    val (ids, coordinates) = Interpreter(listOfFiles(idx))
    // Create the instance of the problem
    val lk = new LinKernighan(ids, coordinates)
    // Time keeping
    var startMillis = 0L
    startMillis = System.currentTimeMillis
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