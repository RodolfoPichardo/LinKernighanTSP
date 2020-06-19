# LinKernighanTSP

[![Build Status](https://travis-ci.org/Sciss/LinKernighanTSP.svg?branch=main)](https://travis-ci.org/Sciss/LinKernighanTSP)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/linkernighantsp_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/linkernighantsp_2.12)

## statement

An implementation of the Lin-Kernighan heuristics algorithm for the (symmetric)
traveling salesman problem (TSP).

This is derived from https://github.com/RodolfoPichardo/LinKernighanTSP – using
[sbt](http://www.scala-sbt.org/) for building, 
and translating to the Scala programming language. Performance has been monitored to be somewhat similar
to the original code (for a graph with 734 nodes, we measured a 13% slow-down compared to the Java sources).
The project has been modified to use a generic cost table instead of 2-dimensional cartesian coordinates,
and the initial tour has to be explicitly provided.

## building and running

This project builds with sbt against Scala 2.13, 2.12.

To compile, use `sbt compile`, to run the demo use `sbt run`. You can also provide a dataset number, e.g.
`sbt 'run 17'`.

## linking

To use this project as a library, use the following artifact:

    libraryDependencies += "de.sciss" %% "linkernighantsp" % v

The current version `v` is `"0.1.2"`

----

Below is the original read-me.

## Features

It implements exactly the same features described by Shen Lin and Brian Kernighan in their original paper
"An Effective Heuristic Algorithm for the Traveling-Salesman Problem"

In addition, this implementation reads *.tsp* files to form a representation of the TSP problem; however, the format
of the *.tsp* file is not yet TSPLIB compliant, but it would be a nice and easy issue to fix.

## How to contribute

We want to keep contributions as simple as possible, so the rules are simple: you contribute changes, I review them, 
if I agree with the changes I will pull it down.

That being said, there are a few recommendations for the quality of code to contribute:

* Efficiency: make the code as efficient as possible, efficiency is one of the key aspects of this kind of algorithms.
* Readability: document all the new functions by adding comments, and if there are tricky pieces of code, comment them as well.
* Reliability: ensure the code works!

Finally, review the TODO list in this page.

## How to run

Currently, the code is ran by compiling all the files and running the Main class.

## Resources

- Original Paper by S. Lin and B. W. Kernighan

Lin, Shen; Kernighan, B. W. (1973). "An Effective Heuristic Algorithm for the Traveling-Salesman Problem". 
*Operations Research*. **21** (2): 498–516.
doi:[10.1287/opre.21.2.498](https://eng.ucmerced.edu/people/yzhang/papers/Heuristic/Lin_Kernighan).

- Waterloo TSP test data

[link](http://www.math.uwaterloo.ca/tsp/data/)

**NOTE:** we are currently using a different format for our test data.
