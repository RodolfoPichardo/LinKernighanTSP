package de.sciss.tsp

import scala.annotation.tailrec
import scala.collection.immutable.{IndexedSeq => Vec}

/*

IMPROVE_WITH             took 33689 ms (97 %); count    4755, avg 7.1 ms
SELECT_NEW_T             took 32728 ms (95 %); count   56237, avg 0.6 ms
CONSTRUCT_NEW_TOUR       took 28588 ms (83 %); count   85856, avg 0.3 ms
CONSTRUCT_NEW_TOUR_WITH  took 28357 ms (82 %); count   85232, avg 0.3 ms
CREATE_TOUR_FROM_EDGES   took 26169 ms (76 %); count   85856, avg 0.3 ms
IS_TOUR                  took  4362 ms (12 %); count   75911, avg 0.1 ms
GET_NEAREST_NEIGHBOUR    took   713 ms ( 2 %); count    8394, avg 0.1 ms
NEXT_POSSIBLE_Y          took   702 ms ( 2 %); count   51483, avg 0.0 ms
DERIVE_EDGES_FROM_TOUR   took   640 ms ( 1 %); count   85856, avg 0.0 ms
INDEX_OF_NODE            took   633 ms ( 1 %); count  524111, avg 0.0 ms
IS_POSITIVE_GAIN         took   198 ms ( 0 %); count 2178838, avg 0.0 ms
IS_DISJUNCTIVE           took    79 ms ( 0 %); count 2243130, avg 0.0 ms
IS_CONNECTED             took    47 ms ( 0 %); count 1677027, avg 0.0 ms
DERIVE_X                 took    13 ms ( 0 %); count   85856, avg 0.0 ms
DERIVE_Y                 took     8 ms ( 0 %); count   85856, avg 0.0 ms

 */

/**
 * Constructor that creates an instance of the Lin-Kerninghan problem without
 * the optimizations. (Basically the tour it has is the drunken sailor)
 *
 * @param size        the number of cities of this instance
 * @param coordinates the coordinates of all the cities
 * @param seed        seed for the initial random tour
 */
final class LinKernighan(size: Int, coordinates: Vec[Point], seed: Long) {
  require (size == coordinates.size)

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
    val T0 = T()
    var oldDistance = 0.0
    var newDistance = tourDistance
    do {
      oldDistance = newDistance
      improveAll()
      newDistance = tourDistance
    } while (newDistance < oldDistance)
    val T_TOTAL = T() - T0

    val timingData = Seq(
      ("GET_NEAREST_NEIGHBOUR"  , T_GET_NEAREST_NEIGHBOUR  , C_GET_NEAREST_NEIGHBOUR  ),
      ("IMPROVE_WITH"           , T_IMPROVE_WITH           , C_IMPROVE_WITH           ),
      ("NEXT_POSSIBLE_Y"        , T_NEXT_POSSIBLE_Y        , C_NEXT_POSSIBLE_Y        ),
      ("IS_CONNECTED"           , T_IS_CONNECTED           , C_IS_CONNECTED           ),
      ("IS_POSITIVE_GAIN"       , T_IS_POSITIVE_GAIN       , C_IS_POSITIVE_GAIN       ),
      ("SELECT_NEW_T"           , T_SELECT_NEW_T           , C_SELECT_NEW_T           ),
      ("CONSTRUCT_NEW_TOUR_WITH", T_CONSTRUCT_NEW_TOUR_WITH, C_CONSTRUCT_NEW_TOUR_WITH),
      ("IS_TOUR"                , T_IS_TOUR                , C_IS_TOUR                ),
      ("CONSTRUCT_NEW_TOUR"     , T_CONSTRUCT_NEW_TOUR     , C_CONSTRUCT_NEW_TOUR     ),
      ("CREATE_TOUR_FROM_EDGES" , T_CREATE_TOUR_FROM_EDGES , C_CREATE_TOUR_FROM_EDGES ),
      ("DERIVE_X"               , T_DERIVE_X               , C_DERIVE_X               ),
      ("DERIVE_Y"               , T_DERIVE_Y               , C_DERIVE_Y               ),
      ("DERIVE_EDGES_FROM_TOUR" , T_DERIVE_EDGES_FROM_TOUR , C_DERIVE_EDGES_FROM_TOUR ),
      ("IS_DISJUNCTIVE"         , T_IS_DISJUNCTIVE         , C_IS_DISJUNCTIVE         ),
      ("INDEX_OF_NODE"          , T_INDEX_OF_NODE          , C_INDEX_OF_NODE          ),
    )

    println(s"Took $T_TOTAL ms")
    timingData.sortBy(-_._2).foreach { case (label, t, c) =>
      val labelP = (label + " " * 20).take(24)
      val avg    = if (c == 0) 0.0 else t.toDouble / c
      println(f"$labelP took $t%5d ms (${t * 100 / T_TOTAL}%2d %%); count $c%7d, avg $avg%1.1f ms")
    }
  }

  /*
   * This function create a random tour using the drunken sailor algorithm
   *
   * @return array with the list of nodes in the tour (sorted)
   */
  private def createRandomTour(seed: Long): Array[Int] = { // init array
    val t = new Array[Int](size)
    var i = 0
    while (i < size) {
      t(i) = i
      i += 1
    }
    val random = new scala.util.Random(seed)
    i = 0
    while (i < size) {
      val j = random.nextInt(i + 1)
      // Simple swap
      val a = t(j)
      t(j) = t(i)
      t(i) = a
      i += 1
    }
    t
  }

  /*
   * This function creates a table with the distances of all the cities
   *
   * @return a two dimensional array with all the distances
   */
  private def initDistanceTable(): Array[Array[Double]] = {
    val res = Array.ofDim[Double](size, size)
    var i = 0
    val sizeM1 = size - 1
    while (i < sizeM1) {
      val p1 = coordinates(i)
      var j = i + 1
      while (j < size) {
        val p2    = coordinates(j)
        val dx    = p2.x - p1.x
        val dy    = p2.y - p1.y
        val cost  = math.sqrt(dx * dx + dy * dy)
        res(i)(j) = cost
        res(j)(i) = cost
        j += 1
      }
      i += 1
    }
    res
  }

  /**
   * This function returns the current tour distance
   *
   * @return the distance of the tour
   */
  def tourDistance: Double = {  // not in a hot loop
    var sum = 0.0
    val _tour = tourVr
    var i = 0
    while (i < size) {
      val a = _tour(i) // <->
      val b = _tour((i + 1) % size)
      sum += distanceTable(a)(b)
      i += 1
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

  private[this] var T_GET_NEAREST_NEIGHBOUR = 0L
  private[this] var C_GET_NEAREST_NEIGHBOUR = 0

  private def T(): Long = System.currentTimeMillis()

  /*
   * This function returns the nearest neighbor for an specific node
   *
   * @param index the index of the node
   * @return      the index of the nearest node
   */
  private def getNearestNeighbor(index: Int): Int = {
    val T0 = T()
    var minDistance = Double.MaxValue
    var nearestNode = -1
    val actualNode  = tourVr(index)
    var i = 0
    while (i < size) {
      if (i != actualNode) {
        val distance = this.distanceTable(i)(actualNode)
        if (distance < minDistance) {
          nearestNode = indexOfNode(i)
          minDistance = distance
        }
      }
      i += 1
    }
    T_GET_NEAREST_NEIGHBOUR += T() - T0
    C_GET_NEAREST_NEIGHBOUR += 1
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

  private[this] var T_IMPROVE_WITH = 0L
  private[this] var C_IMPROVE_WITH = 0

  /*
   * This function is actually the step four from Lin-Kernighan's original paper
   *
   * @param t1 the index that references the chosen t1 in the tour
   * @param t2 the index that references the chosen t2 in the tour
   * @param t3 the index that references the chosen t3 in the tour
   */
  private def improveWith(t1: Int, t2: Int, t3: Int): Unit = {
    val T0 = T()
    // Start with the index 1 to be consistent with Lin-Kernighan Paper
//    var tIndex = Vector.empty[Int] :+ -1 :+ t1 :+ t2 :+ t3
    val tIndex = new Array[Int](size)
    tIndex(0) = -1
    tIndex(1) = t1
    tIndex(2) = t2
    tIndex(3) = t3
    var tSz   = 4

    val G0    = getDistance(t2, t1) - getDistance(t3, t2) // |x1| - |y1|
    var GStar = 0.0
    var Gi    = G0
    var k     = 3
    var i     = 4

    @tailrec
    def inner(): Unit = {
      val newT = selectNewT(tIndex, tSz = tSz)
      if (newT == -1) {
        return // break
        // This should not happen according to the paper

      }
      tIndex(tSz) = newT // = tIndex.patch(i, newT :: Nil, 0)
      tSz += 1

      val tiPlus1 = nextPossibleY(tIndex, tSz = tSz)
      if (tiPlus1 == -1) return // break
      // Step 4.f from the paper
      Gi += getDistance(tIndex(tSz - 2), newT)
      if (Gi - getDistance(newT, t1) > GStar) {
        GStar = Gi - getDistance(newT, t1)
        k = i
      }
      tIndex(tSz) = tiPlus1
      tSz += 1
      Gi -= getDistance(newT, tiPlus1)

      i += 2
      inner()
    }

    inner()
    if (GStar > 0) {
      tIndex(k + 1) = tIndex(1)
      tourVr = getTPrime(tIndex, k = k) // Update the tour
    }
    T_IMPROVE_WITH += T() - T0
    C_IMPROVE_WITH += 1
  }

  private[this] var T_NEXT_POSSIBLE_Y = 0L
  private[this] var C_NEXT_POSSIBLE_Y = 0

  /*
   * This function gets all the ys that fit the criterion for step 4
   *
   * @param tIndex  the list of t's
   * @return        an array with all the possible y's
   */
  private def nextPossibleY(tIndex: Array[Int], tSz: Int): Int = {
    val T0 = T()
    val ti          = tIndex(tSz - 1) // tIndex.last
    var minDistance = Double.MaxValue
    var minNode     = -1
    var i = 0
    while (i < size) {
      val d = getDistance(ti, i)
      if (d < minDistance &&
          isDisjunctive   (tIndex, tSz = tSz, x  = i, y = ti) &&
          isPositiveGain  (tIndex, tSz = tSz, ti = i) &&
          isNextXPossible (tIndex, tSz = tSz, ti = i)) {

        minNode     = i
        minDistance = d
      }
      i += 1
    }
    T_NEXT_POSSIBLE_Y += T() - T0
    C_NEXT_POSSIBLE_Y += 1
    minNode
  }

  /*
   * This function implements the part e from the point 4 of the paper
   */
  private def isNextXPossible(tIndex: Array[Int], tSz: Int, ti: Int): Boolean =
    isConnected(tIndex, tSz = tSz, x = ti, y = nextIdx    (ti)) ||
    isConnected(tIndex, tSz = tSz, x = ti, y = previousIdx(ti))

  private[this] var T_IS_CONNECTED = 0L
  private[this] var C_IS_CONNECTED = 0

  private def isConnected(tIndex: Array[Int], tSz: Int, x: Int, y: Int): Boolean = {
    if (x == y) return false
    var i = 1
    val stop = tSz - 1 // tIndex.length - 1
    val T0 = T()
    while (i < stop) {
      val ta = tIndex(i); i += 1
      val tb = tIndex(i); i += 1
      if (((ta == x) && (tb == y)) || ((ta == y) && (tb == x))) {
        T_IS_CONNECTED += T() - T0
        C_IS_CONNECTED += 1
        return false
      }
    }
    T_IS_CONNECTED += T() - T0
    C_IS_CONNECTED += 1
    true
  }

  private[this] var T_IS_POSITIVE_GAIN = 0L
  private[this] var C_IS_POSITIVE_GAIN = 0

  /*
   * @return true if the gain would be positive
   */
  private def isPositiveGain(tIndex: Array[Int], tSz: Int, ti: Int): Boolean = {
    val T0 = T()
    var gain = 0.0
    var i = 1
    val tSzM3 = tSz - 3
    val stop  = tSz - 2 // tIndex.length - 2
    while (i < stop) {
      val t1 = tIndex(i)
      val t2 = tIndex(i + 1)
      val t3 = if (i == tSzM3) ti else tIndex(i + 2)
      gain += getDistance(t2, t3) - getDistance(t1, t2) // |yi| - |xi|
      i += 1
    }
    T_IS_POSITIVE_GAIN += T() - T0
    C_IS_POSITIVE_GAIN += 1
    gain > 0.0
  }

  private[this] var T_SELECT_NEW_T = 0L
  private[this] var C_SELECT_NEW_T = 0

  /*
   * This function gets a new t with the characteristics described in the paper in step 4.a.
   */
  private def selectNewT(tIndex: Array[Int], tSz: Int): Int = { // HOT
    val T0 = T()
    val lastIndex = tIndex(tSz - 1) // tIndex.last
    val option1   = previousIdx(lastIndex)
    val tour1     = constructNewTourWith(tIndex, tSz = tSz, newItem = option1)
    val res       = if (isTour(tour1)) {
      option1
    } else {
      val option2 = nextIdx(lastIndex)
      val tour2   = constructNewTourWith(tIndex, tSz = tSz, newItem = option2)
      if (isTour(tour2)) option2 else -1
    }
    T_SELECT_NEW_T += T() - T0
    C_SELECT_NEW_T += 1
    res
  }

  private[this] var T_CONSTRUCT_NEW_TOUR_WITH = 0L
  private[this] var C_CONSTRUCT_NEW_TOUR_WITH = 0

  private def constructNewTourWith(tIndex: Array[Int], tSz: Int, newItem: Int): Array[Int] = { // HOT
    val T0 = T()
    val changes   = new Array[Int](tSz + 2)
    System.arraycopy(tIndex, 0, changes, 0, tSz)
    changes(tSz)      = newItem
    changes(tSz + 1)  = changes(1)
    val res = constructNewTour(changes, tSz + 2)
    T_CONSTRUCT_NEW_TOUR_WITH += T() - T0
    C_CONSTRUCT_NEW_TOUR_WITH += 1
    res
  }

  private[this] var T_IS_TOUR = 0L
  private[this] var C_IS_TOUR = 0

  /*
   * This function validates whether a sequence of numbers constitutes a tour
   *
   * @param tr an array with the node numbers
   */
  private def isTour(tr: Array[Int]): Boolean = {
    if (tr.length != size) return false
    val T0 = T()
    var i = 0
    val sizeM1 = size - 1
    while (i < sizeM1) {
      var j   = i + 1
      val tri = tr(i)
      while (j < size) {
        if (tri == tr(j)) {
          T_IS_TOUR += T() - T0
          C_IS_TOUR += 1
          return false
        }
        j += 1
      }
      i += 1
    }
    T_IS_TOUR += T() - T0
    C_IS_TOUR += 1
    true
  }

  /*
   * Constructs T'
   */
  private def getTPrime(tIndex: Array[Int], /*tSz: Int,*/ k: Int): Array[Int] =
    constructNewTour(changes = tIndex, cSz = k + 2)

  private[this] var T_CONSTRUCT_NEW_TOUR = 0L
  private[this] var C_CONSTRUCT_NEW_TOUR = 0

  /*
   * This function constructs a new Tour deleting the X sets and adding the Y sets
   *
   * @param tour    The current tour
   * @param changes the list of t's to derive the X and Y sets
   * @return        an array with the node numbers
   */
  private def constructNewTour(changes: Array[Int], cSz: Int): Array[Int] = { // HOT
    val T0 = T()
    val edges0  = deriveEdgesFromTour()
    val X       = deriveX(changes, cSz = cSz)
    val Y       = deriveY(changes, cSz = cSz)
    val edges1  = edges0 diff X
    val edges   = edges1 ++   Y
    val res     = createTourFromEdges(edges)
    T_CONSTRUCT_NEW_TOUR += T() - T0
    C_CONSTRUCT_NEW_TOUR += 1
    res
  }

  private[this] var T_CREATE_TOUR_FROM_EDGES = 0L
  private[this] var C_CREATE_TOUR_FROM_EDGES = 0

  /*
   * This function takes a list of edges and converts it into a tour
   *
   * @param edges The list of edges to convert
   * @return      the array representing the tour
   */
  private def createTourFromEdges(edges: Vec[Edge]): Array[Int] = { // HOT
    val T0 = T()
    val sz      = edges.size
    val _tour   = new Array[Int](sz)
    var last    = -1
    val removed = new Array[Boolean](sz)

    val edge0   = edges.head
    _tour(0)    = edge0._1
    _tour(1)    = edge0._2
    last        = edge0._2
    removed(0)  = true

    var k = 2

    @tailrec
    def inner(): Unit = {
      var j = 1 // note: removed(0) == true
      var found = false
      while (!found && j < sz) {
        if (!removed(j)) {
          val e = edges(j)
          if (e._1 == last) {
            last = e._2
            // break
            found = true

          } else if (e._2 == last) {
            last = e._1
            // break
            found = true

          } else {
            j += 1
          }
        } else {
          j += 1
        }
      }

      // If the list is empty
      if (!found) return // break
      // Remove new edge
      removed(j) = true
      if (k >= sz) return // break
      _tour(k) = last
      k += 1
      inner()
    }

    inner()
    T_CREATE_TOUR_FROM_EDGES += T() - T0
    C_CREATE_TOUR_FROM_EDGES += 1
    _tour
  }

  private[this] var T_DERIVE_X = 0L
  private[this] var C_DERIVE_X = 0

  /*
   * Gets the list of edges from the t index
   *
   * @param changes the list of changes proposed to the tour
   * @return        The list of edges that will be deleted
   */
  private def deriveX(changes: Array[Int], cSz: Int): Vec[Edge] = {
    val T0 = T()
    var i     = 1
    val stop  = cSz - 2
    val es = Vector.newBuilder[Edge]
    es.sizeHint((cSz - 1) / 2) // verified
    val _tour = tourVr
    while (i < stop) {
      val e = Edge(_tour(changes(i)), _tour(changes(i + 1)))
      es += e
      i += 2
    }
    val res = es.result()
    T_DERIVE_X += T() - T0
    C_DERIVE_X += 1
    res
  }

  private[this] var T_DERIVE_Y = 0L
  private[this] var C_DERIVE_Y = 0

  /*
   * Gets the list of edges from the t index
   *
   * @param changes the list of changes proposed to the tour
   * @return        The list of edges that will be added
   */
  private def deriveY(changes: Array[Int], cSz: Int): Vec[Edge] = {
    val T0 = T()
    var i     = 2
    val stop  = cSz - 1
    val es = Vector.newBuilder[Edge]
    es.sizeHint((cSz - i) / 2) // verified
    val _tour = tourVr
    while (i < stop) {
      val e = Edge(_tour(changes(i)), _tour(changes(i + 1)))
      es += e
      i += 2
    }
    val res = es.result()
    T_DERIVE_Y += T() - T0
    C_DERIVE_Y += 1
    res
  }

  private[this] var T_DERIVE_EDGES_FROM_TOUR = 0L
  private[this] var C_DERIVE_EDGES_FROM_TOUR = 0

  /*
   * Gets the list of edges from the tour, it is basically a conversion from
   * a tour to an edge list
   *
   * @param tour  the array representing the tour
   * @return      The list of edges on the tour
   */
  private def deriveEdgesFromTour(): Vec[Edge] = {
    val T0 = T()
    val es    = Vector.newBuilder[Edge]
    val sz    = size
    val _tour = tourVr
    es.sizeHint(sz) // verified
    var i = 0
    while (i < sz) {
      val e = Edge(_tour(i), _tour((i + 1) % sz))
      es += e
      i += 1
    }
    val res = es.result()
    T_DERIVE_EDGES_FROM_TOUR += T() - T0
    C_DERIVE_EDGES_FROM_TOUR += 1
    res
  }

  private[this] var T_IS_DISJUNCTIVE = 0L
  private[this] var C_IS_DISJUNCTIVE = 0

  /*
   * This function allows to check if an edge is already on either X or Y (disjunctivity criteria)
   *
   * @param tIndex  the index of the nodes in the tour
   * @param x       the index of one of the endpoints
   * @param y       the index of one of the endpoints
   * @return        true when it satisfy the criteria, false otherwise
   */
  private def isDisjunctive(tIndex: Array[Int], tSz: Int, x: Int, y: Int): Boolean = {
    if (x == y) return false
    val T0 = T()
    var i = 0
    val stop = tSz - 1 // tIndex.length - 1
    while (i < stop) {
      val ta = tIndex(i); i += 1
      val tb = tIndex(i)
      if (((ta == x) && (tb == y)) || ((ta == y) && (tb == x))) {
        T_IS_DISJUNCTIVE += T() - T0
        C_IS_DISJUNCTIVE += 1
        return false
      }
    }
    T_IS_DISJUNCTIVE += T() - T0
    C_IS_DISJUNCTIVE += 1
    true
  }

  private[this] var T_INDEX_OF_NODE = 0L
  private[this] var C_INDEX_OF_NODE = 0

  /*
   * This function gets the index of the node given the actual number of the node in the tour
   *
   * @param node  the node id
   * @return      the index on the tour
   */
  private def indexOfNode(node: Int): Int = {
    val T0 = T()
    val res = tourVr.indexOf(node)
    T_INDEX_OF_NODE += T() - T0
    C_INDEX_OF_NODE += 1
    res
  }

  /**
   * This function returns a string with the current tour and its distance
   *
   * @return String with the representation of the tour
   */
  override def toString: String = {
    val str   = new StringBuilder("[" + this.tourDistance + "] : ")
    val _tour = tourVr
    var ci    = 0
    while (ci < _tour.length) {
      val city = _tour(ci)
      if (ci > 0) {
        str.append(" => ").append(city)
      } else {
        str.append(city)
      }
      ci += 1
    }
    str.toString
  }
}