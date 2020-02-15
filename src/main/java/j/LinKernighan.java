package j;

import java.util.ArrayList;
import java.util.Random;

public class LinKernighan {
    //The instance variables definitions

	// The coordinates of all the cities
    private ArrayList<Point> coordinates;

    // The number of cities of this instance
    private int size;
    
    // The current tour solution
    public int[] tour;

    // The distance table
    private double[][] distanceTable; 

    /**
     * Constructor that creates an instance of the Lin-Kerninghan problem without
     * the optimizations. (Basically the tour it has is the drunken sailor)
     *
	 * @param coordinates 	the coordinates of all the cities
     * @param ids 			the id of all the cities
	 * @param seed        	seed for the initial random tour
     */
    public LinKernighan(ArrayList<Point> coordinates, ArrayList<Integer> ids, long seed) {
		// The ids of all the cities (sorted)
		this.coordinates	= coordinates;
        this.size 			= ids.size();
        this.tour 			= createRandomTour(seed);
        this.distanceTable 	= initDistanceTable();
        
    }

    /*
     * This function create a random tour using the drunken sailor algorithm
     *
     * @return array with the list of nodes in the tour (sorted)
     */
    private int[] createRandomTour(long seed) {
    	// init array
    	int[] array = new int[size];
    	for (int i = 0; i < size; i++) {
    		array[i] = i;
    	}
    	
    	Random random = new Random(seed);
    	
    	for (int i = 0; i < size; ++i) {
    		int index = random.nextInt(i + 1);
    	    // Simple swap
    	    int a = array[index];
    	    array[index] = array[i];
    	    array[i] = a;
    	}
    	
        return array;
    }

    /*
     * This function creates a table with the distances of all the cities
     *
     * @return a two dimensional array with all the distances
     */
    private double[][] initDistanceTable() {
        double[][] res = new double[this.size][this.size];

        for (int i = 0; i < this.size - 1; ++i) {
            for (int j = i + 1; j < this.size; ++j) {
                Point p1 = this.coordinates.get(i);
                Point p2 = this.coordinates.get(j);

                res[i][j] = Math.sqrt(
                    Math.pow(p2.getX() - p1.getX(), 2) +
                    Math.pow(p2.getY() - p1.getY(), 2) 
                );

                res[j][i] = res[i][j];
            }
        }
        return res;
    }

    /**
     * This function returns the current tour distance
	 *
     * @return the distance of the tour
     */
    public double getTourDistance() {
        double sum = 0;

        for (int i = 0; i < this.size; i++) {
            int a = tour[i];                  	// <->
            int b = tour[(i + 1) % this.size];  // <->
            sum += this.distanceTable[a][b];
        }

        return sum;
    }

    /**
     * This function is the crown jewel of this class, it tries to optimize
     * the current tour
     */
    public void run() {
        double oldDistance = 0.0;
        double newDistance = getTourDistance();
        
        do {
        	oldDistance = newDistance;
        	improve();
        	newDistance = getTourDistance();
        } while (newDistance < oldDistance);
    }
    
    /**
     * This function tries to improve the tour
     */
    public void improve() {
    	for (int i = 0; i < size; ++i) {
    		improve(i);
    	}
    }
    
    /**
     * This function tries to improve by stating from a particular node
	 *
     * @param x the reference to the city to start with.
     */
    public void improve(int x) {
    	improve(x, false);
    }
    
    /**
     * This function attempts to improve the tour by stating from a particular node
	 *
     * @param t1 the reference to the city to start with.
     */
    public void improve(int t1, boolean previous) {
    	int t2 = previous? getPreviousIdx(t1) : getNextIdx(t1);
    	int t3 = getNearestNeighbor(t2);
    	
    	if (t3 != -1 && getTourDistance(t2, t3) < getTourDistance(t1, t2)) { // Implementing the gain criteria
    		improveWith(t1,t2,t3);
    	} else if (!previous) {
    		improve(t1, true);
    	}
    }
    
    /**
     * This function returns the previous index for the tour, this typically should be x-1
     * but if x is zero, well, it is the last index.
	 *
     * @param index the index of the node
     * @return 		the previous index
     */
    public int getPreviousIdx(int index) {
    	return index == 0 ? size - 1 : index - 1;
    }
    
    /**
     * This function returns the next index for the tour, this typically should be x+1
     * but if x is the last index it should wrap to zero
	 *
     * @param index	the index of the node
     * @return 		the next index
     */
    public int getNextIdx(int index) {
    	return (index + 1) % size;
    }
    
    /**
     * This function returns the nearest neighbor for an specific node
	 *
     * @param index the index of the node
     * @return 		the index of the nearest node
     */
    public int getNearestNeighbor(int index) {
    	double minDistance = Double.MAX_VALUE;
    	int nearestNode = -1;
		int actualNode = tour[index];
    	for (int i = 0; i < size; ++i) {
    		if (i != actualNode) {
    			double distance = this.distanceTable[i][actualNode];
    			if (distance < minDistance) {
    				nearestNode = indexOfNode(i);
    				minDistance = distance; 
    			}
    		}
    	}
    	return nearestNode;
    }
    
    /**
     * This function retrieves the distance between two nodes given its indexes
	 *
     * @param n1 	index of the first node
     * @param n2 	index of the second node
     * @return 		the distance from node 1 to node 2
     */
    public double getTourDistance(int n1, int n2) {
    	return distanceTable[tour[n1]][tour[n2]];
    }
    
    /**
     * This function is actually the step four from the lin-kernighan's original paper
	 *
     * @param t1 the index that references the chosen t1 in the tour
     * @param t2 the index that references the chosen t2 in the tour
     * @param t3 the index that references the chosen t3 in the tour
     */
    public void improveWith(int t1, int t2, int t3) {
    	ArrayList<Integer> tIndex = new ArrayList<Integer>();
    	tIndex.add(0, -1); // Start with the index 1 to be consistent with Lin-Kernighan Paper
    	tIndex.add(1, t1);
    	tIndex.add(2, t2);
    	tIndex.add(3, t3);
    	final double G0 = getTourDistance(t2, t1) - getTourDistance(t3, t2); // |x1| - |y1|
    	double GStar 	= 0.0;
    	double Gi 		= G0;
    	int k = 3;
    	for (int i = 4;; i+=2) {
    		int newT = selectNewT(tIndex);
    		if (newT == -1) {
    			break; // This should not happen according to the paper
    		}
    		tIndex.add(i, newT);
    		int tiPlus1 = getNextPossibleY(tIndex);
    		if (tiPlus1 == -1) {
    			break;
    		}
    		
    		   		
    		// Step 4.f from the paper
    		Gi += getTourDistance(tIndex.get(tIndex.size() - 2), newT);
    		if (Gi - getTourDistance(newT, t1) > GStar) {
    			GStar = Gi - getTourDistance(newT, t1);
    			k = i;
    		}
    		
    		tIndex.add(tiPlus1);
    		Gi -= getTourDistance(newT, tiPlus1);
    	}
    	
    	if (GStar > 0) {
    		tIndex.set(k + 1, tIndex.get(1));
    		tour = getTPrime(tIndex, k); // Update the tour
    	}
    }
    
    /**
     * This function gets all the ys that fit the criterion for step 4
	 *
     * @param tIndex	the list of t's
     * @return 			an array with all the possible y's
     */
    public int getNextPossibleY(ArrayList<Integer> tIndex) {
    	int ti = tIndex.get(tIndex.size() - 1);
    	ArrayList<Integer> ys = new ArrayList<Integer>();
    	for (int i = 0; i < size; ++i) {
    		if (!isDisjunctive(tIndex, i, ti)) {
    			continue; // Disjunctive criteria
    		}
    		
    		if (!isPositiveGain(tIndex, i)) {
    			continue; // Gain criteria
    		}
			if (!isNextXPossible(tIndex, i)) {
    			continue; // Step 4.f.
    		}
    		ys.add(i);
    	}
    	
    	// Get closest y
    	double minDistance = Double.MAX_VALUE;
    	int minNode = -1;
    	for (int i : ys) {
    		if (getTourDistance(ti, i) < minDistance) {
    			minNode = i;
    			minDistance = getTourDistance(ti, i);
    		}
		}
    	
    	return minNode;
    	
    }
    
    /**
     * This function implements the part e from the point 4 of the paper
     */
    private boolean isNextXPossible(ArrayList<Integer> tIndex, int i) {
    	return isConnected(tIndex, i, getNextIdx(i)) || isConnected(tIndex, i, getPreviousIdx(i));
	}

	private boolean isConnected(ArrayList<Integer> tIndex, int x, int y) {
		if (x == y) return false;
		for (int i = 1; i < tIndex.size() -1 ; i += 2) {
			if (tIndex.get(i) == x && tIndex.get(i + 1) == y) return false;
			if (tIndex.get(i) == y && tIndex.get(i + 1) == x) return false;
		}
		return true;
	}

	/**
     * @return true if the gain would be positive
     */
    private boolean isPositiveGain(ArrayList<Integer> tIndex, int ti) {
		int gain = 0;
    	for (int i = 1; i < tIndex.size() - 2; ++i) {
			int t1 = tIndex.get(i);
			int t2 = tIndex.get(i + 1);
			int t3 = i == tIndex.size() - 3 ? ti : tIndex.get(i + 2);
			
			gain += getTourDistance(t2, t3) - getTourDistance(t1, t2); // |yi| - |xi|
			
			
		}
		return gain > 0;
	}

	/**
     * This function gets a new t with the characteristics described in the paper in step 4.a.
     */
    public int selectNewT(ArrayList<Integer> tIndex) {
    	final int lastIndex	= tIndex.get(tIndex.size() - 1);
    	final int option1	= getPreviousIdx(lastIndex);

		final int[] tour1 = constructNewTour(tour, tIndex, option1);
    	  	
    	if (isTour(tour1)) {
    		return option1;
    	} else {
			final int option2 = getNextIdx(lastIndex);
			final int[] tour2 = constructNewTour(tour, tIndex, option2);
        	if (isTour(tour2)) {
        		return option2;
        	}
    	}
    	return -1;
    }
    
    private int[] constructNewTour(int[] tour2, ArrayList<Integer> tIndex, int newItem) {
    	ArrayList<Integer> changes = new ArrayList<Integer>(tIndex);
    	
    	changes.add(newItem);
    	changes.add(changes.get(1));
		return constructNewTour(tour2, changes);
	}

	/**
     * This function validates whether a sequence of numbers constitutes a tour
	 *
     * @param tour an array with the node numbers
     */
    public boolean isTour(int[] tour) {
    	if (tour.length != size) {
    		return false;
    	}
    	
    	for (int i =0; i < size - 1; ++i) {
    		for (int j = i + 1; j < size; ++j) {
    			if (tour[i] == tour[j]) {
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
    /**
     * Constructs T'
     */
    private int[] getTPrime(ArrayList<Integer> tIndex, int k) {
    	ArrayList<Integer> al2 = new ArrayList<Integer>(tIndex.subList(0, k + 2 ));
    	return constructNewTour(tour, al2);
    }
    
    /**
     * This function constructs a new Tour deleting the X sets and adding the Y sets
	 *
     * @param tour 		The current tour
     * @param changes 	the list of t's to derive the X and Y sets
     * @return 			an array with the node numbers
     */
    public int[] constructNewTour(int[] tour, ArrayList<Integer> changes) {
		final ArrayList<Edge> edges = deriveEdgesFromTour(tour);
    	final ArrayList<Edge> X 	= deriveX(changes);
		final ArrayList<Edge> Y 	= deriveY(changes);
    	int sz = edges.size();
    	
    	// Remove Xs
    	for (Edge e : X) {
    		for (int j = 0; j < edges.size(); ++j) {
    			Edge m = edges.get(j);
    			if (e.equals(m)) {
    				sz--;
    				edges.set(j, null);
    				break;
    			}
    		}
    	}
    	
    	// Add Ys
    	for (Edge e : Y) {
    		sz++;
    		edges.add(e);
    	}

    	return createTourFromEdges(edges, sz);
    }
    
    /**
     * This function takes a list of edges and converts it into a tour
	 *
     * @param currentEdges 	The list of edges to convert
     * @return 				the array representing the tour
     */
    private int[] createTourFromEdges(ArrayList<Edge> currentEdges, int sz) {
		int[] tour = new int[sz];
    	
		int i = 0;
		int last = -1;
		
		for (; i < currentEdges.size(); ++i) {
			if (currentEdges.get(i) != null) {
				tour[0] = currentEdges.get(i).get1();
				tour[1] = currentEdges.get(i).get2();
				last = tour[1];
				break;
			}
		}
		
		currentEdges.set(i, null); // remove the edges
		
		int k = 2;
		while (true) {
			// E = find()
			int j = 0;
			for (; j < currentEdges.size(); ++j) {
				Edge e = currentEdges.get(j);
				if (e != null && e.get1() == last) {
					last = e.get2();
					break;
				} else if (e != null && e.get2() == last) {
					last = e.get1();
					break;
				}
			}
			// If the list is empty
			if (j == currentEdges.size()) break;
			
			// Remove new edge
			currentEdges.set(j, null);
			if (k >= sz) break;
			tour[k] = last;
			k++;
		}
		
		return tour;
	}

    /**
     * Gets the list of edges from the t index
	 *
     * @param changes 	the list of changes proposed to the tour
     * @return 			The list of edges that will be deleted
     */
	public ArrayList<Edge> deriveX(ArrayList<Integer> changes) {
		ArrayList<Edge> es = new ArrayList<Edge>();
		for (int i = 1; i < changes.size() - 2; i += 2) {
			Edge e = new Edge(tour[changes.get(i)], tour[changes.get(i+1)]);
			es.add(e);
		}
    	return es;
	}

    /**
     * Gets the list of edges from the t index
	 *
     * @param changes 	the list of changes proposed to the tour
     * @return 			The list of edges that will be added
     */
    ArrayList<Edge> deriveY(ArrayList<Integer> changes) {
		ArrayList<Edge> es = new ArrayList<Edge>();
		for (int i = 2; i < changes.size() - 1; i += 2) {
			Edge e = new Edge(tour[changes.get(i)], tour[changes.get(i + 1)]);
			es.add(e);
		}
    	return es;
	}
    

    /**
     * Gets the list of edges from the tour, it is basically a conversion from
     * a tour to an edge list
     *
	 * @param tour 	the array representing the tour
     * @return 		The list of edges on the tour
     */
	public ArrayList<Edge> deriveEdgesFromTour(int[] tour) {
    	ArrayList<Edge> es = new ArrayList<Edge>();
    	for (int i = 0; i < tour.length ; ++i) {
    		Edge e = new Edge(tour[i], tour[(i + 1) % tour.length]);
    		es.add(e);
    	}
    	
    	return es;
    }
	
	/**
	 * This function allows to check if an edge is already on either X or Y (disjunctivity criteria)
	 *
	 * @param tIndex 	the index of the nodes in the tour
	 * @param x 		the index of one of the endpoints
	 * @param y 		the index of one of the endpoints
	 * @return 			true when it satisfy the criteria, false otherwise
	 */
	private boolean isDisjunctive(ArrayList<Integer> tIndex, int x, int y) {
		if (x == y) return false;
		for (int i = 0; i < tIndex.size() -1 ; i++) {
			if (tIndex.get(i) == x && tIndex.get(i + 1) == y) return false;
			if (tIndex.get(i) == y && tIndex.get(i + 1) == x) return false;
		}
		return true;
	}
    
    
    /**
     * This function gets the index of the node given the actual number of the node in the tour
	 *
     * @param node 	the node id
     * @return 		the index on the tour
     */
    private int indexOfNode(int node) {
    	int i = 0;
    	for (int t : tour) {
    		if (node == t) {
    			return i;
    		}
    		i++;
    	}
    	return -1;
    }
    
    /**
     * This function returns a string with the current tour and its distance
	 *
     * @return String with the representation of the tour
     */
    public String toString() {
        StringBuilder str = new StringBuilder("[" + this.getTourDistance() + "] : ");
        boolean add = false;
        for (int city : this.tour) {
            if (add) {
                str.append(" => ").append(city);
            } else {
                str.append(city);
                add = true;
            }
        }
        return str.toString();
    }
}