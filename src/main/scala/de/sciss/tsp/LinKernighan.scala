package de.sciss.tsp

import scala.annotation.tailrec
import scala.collection.immutable.{IndexedSeq => Vec}

/**
 * Constructor that creates an instance of the Lin-Kerninghan problem without
 * the optimizations. (Basically the tour it has is the drunken sailor)
 *
 * @param ids         the id of all the cities (sorted)
 * @param coordinates the coordinates of all the cities
 * @param seed        seed for the initial random tour
 */
final class LinKernighan(ids: Vec[Int], coordinates: Vec[Point], seed: Long) {

  // The number of cities of this instance
  private[this] val size = ids.size
  // The current tour solution
  private[this] var tourVr: Array[Int] = createRandomTour(seed)
  // The distance table
  private[this] val distanceTable: Array[Array[Double]] = initDistanceTable()

  def tour: Array[Int] = tourVr

  /**
   * This function is the crown jewel of this class, it tries to optimize
   * the current tour
   */
  def run(): Unit = {
    var oldDistance = 0.0
    var newDistance = tourDistance
    do {
      oldDistance = newDistance
      improveAll()
      newDistance = tourDistance
    } while (newDistance < oldDistance)
  }

  /*
   * This function create a random tour using the drunken sailor algorithm
   *
   * @return array with the list of nodes in the tour (sorted)
   */
  private def createRandomTour(seed: Long): Array[Int] = { // init array
    val array = new Array[Int](size)
    for (i <- 0 until size) {
      array(i) = i
    }
    val random = new scala.util.Random(seed)
    for (i <- 0 until size) {
      val index = random.nextInt(i + 1)
      // Simple swap
      val a = array(index)
      array(index) = array(i)
      array(i) = a
    }
    array
  }

  /*
   * This function creates a table with the distances of all the cities
   *
   * @return a two dimensional array with all the distances
   */
  private def initDistanceTable(): Array[Array[Double]] = {
    val res = Array.ofDim[Double](size, size)
    for (i <- 0 until size - 1) {
      for (j <- i + 1 until size) {
        val p1 = coordinates(i)
        val p2 = coordinates(j)
        res(i)(j) = math.sqrt(math.pow(p2.x - p1.x, 2) + math.pow(p2.y - p1.y, 2))
        res(j)(i) = res(i)(j)
      }
    }
    res
  }

  /**
   * This function returns the current tour distance
   *
   * @return the distance of the tour
   */
  def tourDistance: Double = {
    var sum = 0.0
    val _tour = tourVr
    for (i <- 0 until size) {
      val a = _tour(i) // <->
      val b = _tour((i + 1) % size)
      sum += distanceTable(a)(b)
    }
    sum
  }

  /*
   * This function tries to improve the tour
   */
  private def improveAll(): Unit = {
    var i = 0
    while (i < size) {
      improve(i)
      i += 1
    }
  }

  /**
   * This function attempts to improve the tour by stating from a particular node
   *
   * @param t1 the reference to the city to start with.
   */
  @tailrec
  private def improve(t1: Int, previous: Boolean = false): Unit = {
    val t2 = if (previous) previousIdx(t1) else nextIdx(t1)
    val t3 = getNearestNeighbor(t2)
    if (t3 != -1 && getDistance(t2, t3) < getDistance(t1, t2)) { // Implementing the gain criteria
      improveWith(t1, t2, t3)
    }
    else if (!previous) improve(t1, previous = true)
  }

  /*
   * This function returns the previous index for the tour, this typically should be x-1
   * but if x is zero, well, it is the last index.
   *
   * @param index the index of the node
   * @return      the previous index
   */
  private def previousIdx(index: Int): Int =
    if (index == 0) size - 1
    else index - 1

  /*
   * This function returns the next index for the tour, this typically should be x+1
   * but if x is the last index it should wrap to zero
   *
   * @param index the index of the node
   * @return      the next index
   */
  private def nextIdx(index: Int): Int = (index + 1) % size

  /*
   * This function returns the nearest neighbor for an specific node
   *
   * @param index the index of the node
   * @return      the index of the nearest node
   */
  private def getNearestNeighbor(index: Int): Int = {
    var minDistance = Double.MaxValue
    var nearestNode = -1
    val actualNode = tourVr(index)
    for (i <- 0 until size) {
      if (i != actualNode) {
        val distance = this.distanceTable(i)(actualNode)
        if (distance < minDistance) {
          nearestNode = indexOfNode(i)
          minDistance = distance
        }
      }
    }
    nearestNode
  }

  /*
   * This function retrieves the distance between two nodes given its indexes
   *
   * @param n1  index of the first node
   * @param n2  index of the second node
   * @return    the distance from node 1 to node 2
   */
  private def getDistance(n1: Int, n2: Int): Double = distanceTable(tourVr(n1))(tourVr(n2))

  /*
   * This function is actually the step four from Lin-Kernighan's original paper
   *
   * @param t1 the index that references the chosen t1 in the tour
   * @param t2 the index that references the chosen t2 in the tour
   * @param t3 the index that references the chosen t3 in the tour
   */
  private def improveWith(t1: Int, t2: Int, t3: Int): Unit = {
    var tIndex = Vec(-1, t1, t2, t3)  // Start with the index 1 to be consistent with Lin-Kernighan Paper

    val G0    = getDistance(t2, t1) - getDistance(t3, t2) // |x1| - |y1|
    var GStar = 0.0
    var Gi    = G0
    var k     = 3
    var i     = 4

    @tailrec
    def inner(): Unit = {
      val newT = selectNewT(tIndex)
      if (newT == -1) {
        return // break
        // This should not happen according to the paper

      }
      tIndex = tIndex.patch(i, newT :: Nil, 0)
      val tiPlus1 = nextPossibleY(tIndex)
      if (tiPlus1 == -1) return // break
      // Step 4.f from the paper
      Gi += getDistance(tIndex(tIndex.size - 2), newT)
      if (Gi - getDistance(newT, t1) > GStar) {
        GStar = Gi - getDistance(newT, t1)
        k = i
      }
      tIndex :+= tiPlus1
      Gi -= getDistance(newT, tiPlus1)

      i += 2
      inner()
    }

    inner()
    if (GStar > 0) {
      tIndex = tIndex.updated(k + 1, tIndex(1))
      tourVr = getTPrime(tIndex, k) // Update the tour
    }
  }

  /*
   * This function gets all the ys that fit the criterion for step 4
   *
   * @param tIndex  the list of t's
   * @return        an array with all the possible y's
   */
  private def nextPossibleY(tIndex: Vec[Int]): Int = {
    val ti = tIndex.last
    val ysB = Vector.newBuilder[Int]
    ysB.sizeHint(size)
    var i = 0
    while (i < size) {
      if (isDisjunctive(tIndex, i, ti) && isPositiveGain(tIndex, i) && isNextXPossible(tIndex, i)) {
        ysB += i
      }
      i += 1
    }
    val ys = ysB.result()
    // Get closest y
    var minDistance = Double.MaxValue
    var minNode = -1
    for (i <- ys) {
      if (getDistance(ti, i) < minDistance) {
        minNode = i
        minDistance = getDistance(ti, i)
      }
    }
    minNode
  }

  /*
   * This function implements the part e from the point 4 of the paper
   */
  private def isNextXPossible(tIndex: Vec[Int], i: Int): Boolean =
    isConnected(tIndex, i, nextIdx(i)) || isConnected(tIndex, i, previousIdx(i))

  private def isConnected(tIndex: Vec[Int], x: Int, y: Int): Boolean = {
    if (x == y) return false
    var i = 1
    while (i < tIndex.size - 1) {
      if ((tIndex(i) == x) && (tIndex(i + 1) == y)) return false
      if ((tIndex(i) == y) && (tIndex(i + 1) == x)) return false

      i += 2
    }
    true
  }

  /*
   * @return true if the gain would be positive
   */
  private def isPositiveGain(tIndex: Vec[Int], ti: Int): Boolean = {
    var gain = 0.0
    var i = 1
    val stop = tIndex.size - 2
    while (i < stop) {
      val t1 = tIndex(i)
      val t2 = tIndex(i + 1)
      val t3 = if (i == tIndex.size - 3) ti
      else tIndex(i + 2)
      gain += getDistance(t2, t3) - getDistance(t1, t2) // |yi| - |xi|
      i += 1
    }
    gain > 0
  }

  /*
   * This function gets a new t with the characteristics described in the paper in step 4.a.
   */
  private def selectNewT(tIndex: Vec[Int]): Int = {
    val lastIndex = tIndex.last
    val option1   = previousIdx(lastIndex)
    val tour1     = constructNewTour(tourVr, tIndex, option1)
    if (isTour(tour1)) {
      option1
    } else {
      val option2 = nextIdx(lastIndex)
      val tour2   = constructNewTour(tourVr, tIndex, option2)
      if (isTour(tour2)) option2 else -1
    }
  }

  private def constructNewTour(tour2: Array[Int], tIndex: Vec[Int], newItem: Int): Array[Int] = {
    val changes0  = tIndex :+ newItem
    val changes   = changes0 :+ changes0(1)
    constructNewTour(tour2, changes)
  }

  /*
   * This function validates whether a sequence of numbers constitutes a tour
   *
   * @param tr an array with the node numbers
   */
  private def isTour(tr: Array[Int]): Boolean = {
    if (tr.length != size) return false
    for (i <- 0 until size - 1) {
      for (j <- i + 1 until size) {
        if (tr(i) == tr(j)) return false
      }
    }
    true
  }

  /*
   * Constructs T'
   */
  private def getTPrime(tIndex: Vec[Int], k: Int): Array[Int] = {
    val al2 = tIndex.slice(0, k + 2)
    constructNewTour(tourVr, al2)
  }

  /*
   * This function constructs a new Tour deleting the X sets and adding the Y sets
   *
   * @param tour    The current tour
   * @param changes the list of t's to derive the X and Y sets
   * @return        an array with the node numbers
   */
  private def constructNewTour(tour: Array[Int], changes: Vec[Int]): Array[Int] = {
    var edges = deriveEdgesFromTour(tour)
    val X     = deriveX(changes)
    val Y     = deriveY(changes)
    var sz    = edges.size
    // Remove Xs
    for (e <- X) {
      var j = 0
      while (j < edges.size) {
        val m = edges(j)
        if (e == m) {
          sz -= 1
          edges = edges.updated(j, null)
          // break
          j = edges.size
        } else {
          j += 1
        }
      }
    }
    // Add Ys
    for (e <- Y) {
      sz += 1
      edges :+= e
    }
    createTourFromEdges(edges, sz)
  }

  /*
   * This function takes a list of edges and converts it into a tour
   *
   * @param edges The list of edges to convert
   * @return      the array representing the tour
   */
  private def createTourFromEdges(edges: Vec[Edge], sz: Int): Array[Int] = {
    val tour = new Array[Int](sz)
    var last = -1
    var currentEdges = edges

    val i = currentEdges.indexWhere(_ != null)
    tour(0) = currentEdges(i)._1
    tour(1) = currentEdges(i)._2
    last = tour(1)

    currentEdges = currentEdges.updated(i, null) // remove the edges

    var k = 2

    @tailrec
    def inner(): Unit = { // E = find()
      var j = 0
      var found = false
      while (!found && j < currentEdges.size) {
        val e = currentEdges(j)
        if (e != null && e._1 == last) {
          last = e._2
          // break
          found = true

        } else if (e != null && e._2 == last) {
          last = e._1
          // break
          found = true

        } else {
          j += 1
        }
      }
      // If the list is empty
      if (j == currentEdges.size) return // break
      // Remove new edge
      currentEdges = currentEdges.updated(j, null)
      if (k >= sz) return // break
      tour(k) = last
      k += 1
      inner()
    }

    inner()
    tour
  }

  /*
   * Gets the list of edges from the t index
   *
   * @param changes the list of changes proposed to the tour
   * @return        The list of edges that will be deleted
   */
  private def deriveX(changes: Vec[Int]): Vec[Edge] = {
    var i = 1
    val stop = changes.size - 2
    val es = Vector.newBuilder[Edge]
    es.sizeHint(stop - i)
    val _tour = tourVr
    while (i < stop) {
      val e = Edge(_tour(changes(i)), _tour(changes(i + 1)))
      es += e
      i += 2
    }
    es.result()
  }

  /*
   * Gets the list of edges from the t index
   *
   * @param changes the list of changes proposed to the tour
   * @return        The list of edges that will be added
   */
  private def deriveY(changes: Vec[Int]): Vec[Edge] = {
    var i = 2
    val stop = changes.size - 1
    val es = Vector.newBuilder[Edge]
    es.sizeHint(stop - i)
    val _tour = tourVr
    while (i < stop) {
      val e = Edge(_tour(changes(i)), _tour(changes(i + 1)))
      es += e
      i += 2
    }
    es.result()
  }

  /*
   * Gets the list of edges from the tour, it is basically a conversion from
   * a tour to an edge list
   *
   * @param tour  the array representing the tour
   * @return      The list of edges on the tour
   */
  private def deriveEdgesFromTour(tour: Array[Int]): Vec[Edge] = {
    val es = Vector.newBuilder[Edge]
    val sz = tour.length
    es.sizeHint(sz)
    var i = 0
    while (i < sz) {
      val e = Edge(tour(i), tour((i + 1) % sz))
      es += e
      i += 1
    }
    es.result()
  }

  /*
   * This function allows to check if an edge is already on either X or Y (disjunctivity criteria)
   *
   * @param tIndex  the index of the nodes in the tour
   * @param x       the index of one of the endpoints
   * @param y       the index of one of the endpoints
   * @return        true when it satisfy the criteria, false otherwise
   */
  private def isDisjunctive(tIndex: Vec[Int], x: Int, y: Int): Boolean = {
    if (x == y) return false
    for (i <- 0 until tIndex.size - 1) {
      if ((tIndex(i) == x) && (tIndex(i + 1) == y)) return false
      if ((tIndex(i) == y) && (tIndex(i + 1) == x)) return false
    }
    true
  }

  /*
   * This function gets the index of the node given the actual number of the node in the tour
   *
   * @param node  the node id
   * @return      the index on the tour
   */
  private def indexOfNode(node: Int): Int = tourVr.indexOf(node)

  /**
   * This function returns a string with the current tour and its distance
   *
   * @return String with the representation of the tour
   */
  override def toString: String = {
    val str = new StringBuilder("[" + this.tourDistance + "] : ")
    var add = false
    for (city <- this.tourVr) {
      if (add) str.append(" => ").append(city)
      else {
        str.append(city)
        add = true
      }
    }
    str.toString
  }
}