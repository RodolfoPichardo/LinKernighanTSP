import java.util.ArrayList;
import java.util.Collections;

public class LinKernighan {
    //The instance variables definitions

    // The ids of all the cities (sorted)
    private ArrayList<Integer> ids;

    // The coordinates of all the cities
    private ArrayList<Point> coordinates;

    // The number of cities of this instance
    private int size;
    
    // The current tour solution
    private ArrayList<Integer> tour;

    // The edge flags
    private ArrayList<ArrayList<Integer>> edgeFlags;
    
    // The distance table
    private double[][] distanceTable;  


    /**
     * Constructor that creates an instance of the Lin-Kerninghan problem without
     * the optimizations. (Basically the tour it has is the drunken sailor)
     * @param ArrayList<Point> the coordinates of all the cities
     * @param ArrayList<Integer> the id of all the cities
     */ 
    public LinKernighan(ArrayList<Point> coordinates, ArrayList<Integer> ids) { 
        this.ids = ids;
        this.coordinates = coordinates;
        this.size = ids.size();
        this.tour = createRandomTour();
        this.distanceTable = initDistanceTable();
    }

    /**
     * This function create a random tour using the dunken sailor algorithm
     * @param nothing
     * @return ArrayList<Integer> array with the list of nodes in the tour (sorted)
     */
    private ArrayList<Integer> createRandomTour() {
        ArrayList<Integer> tour = new ArrayList<Integer>(this.ids);
        Collections.shuffle(tour);
        return tour;
    }

    /**
     * This functions creates a table with the distances of all the cities
     * @param nothing
     * @return a two dimensional array with all the distances
     */
    private double[][] initDistanceTable() {
        double[][] res = new double[this.size][this.size];

        for(int i = 0; i < this.size-1; ++i) {
            for(int j = i + 1; j < this.size; ++j) {
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
     * @param Nothing
     * @return double the distance of the tour
     */
    public double getDistance() {
        double sum = 0;

        for(int i = 0; i < this.size; i++) {
            int a = this.ids.indexOf(tour.get(i));
            int b = this.ids.indexOf(tour.get((i+1)%this.size));
            sum += this.distanceTable[a][b];
        }

        return sum;
    }

    /**
     * This function is the crown jewel of this class, it tries to optimize
     * the current tour
     * @param nothing
     * @returns nothing
     */
    public void runAlgorithm() {
        double gain,
               oldDistance = getDistance(),
               newDistance = 0;

        for(int i = 0; i < 100; ++i) {
            for(int j = 0; j < this.size; ++j) {
                optimize(j);
            }
            
            newDistance = getDistance();
            gain = oldDistance - newDistance;

            if(gain < 0) {
                throw new NegativeGainException();
            } else if(gain == 0) {
                break; // Convergence reached!
            }
        }
    }

    /**
     * This function iterates through the nodes looking for a better tour
     * @param int id of the starting node
     * @return void
     */
    private void optimize(int startNode) {
        System.out.println("Optimizing at " + startNode);
        // TODO ensure contains would return true if two edges have the same endpoints
        
        // The edges to be deleted and added respectively
        ArrayList<Edge> X = new ArrayList<Edge>(), Y = new ArrayList<Edge>();

        double gOpt = 0;
        double g = 0;
        double gLocal = 0;
        int lastNextV = startNode;
        int fromV, nextV, nextFromV, lastPossibleNextV = -1;
        Edge brokenEdge;
        double yOptLength;
        double brokenEdgeLength;
        double gOptLocal;

        fromV = this.tour.get(lastNextV);
        double initialTourDistance = getDistance();

        do {
            nextV = -1;
            brokenEdge = new Edge(lastNextV, fromV);
            brokenEdgeLength = this.distanceTable[brokenEdge.get1()][brokenEdge.get2()];
            
            if(Y.contains(brokenEdge)) break;

            for(int possibleNextV = tour.get(fromV); nextV == -1 && possibleNextV != startNode; possibleNextV = tour.get(possibleNextV)) {
                gLocal = brokenEdgeLength - this.distanceTable[fromV][possibleNextV];

                if(X.contains(new Edge(fromV, possibleNextV)) ||
                   g+gLocal <= 0 || 
                   Y.contains(new Edge(lastPossibleNextV, possibleNextV)) ||
                   this.tour.get(possibleNextV) == 0 ||
                   possibleNextV == this.tour.get(fromV)
                ) {
                    lastPossibleNextV = possibleNextV;
                    continue;
                }

                nextV = possibleNextV;
            }

            if(nextV != -1) {
                X.add(brokenEdge);
                Y.add(new Edge(fromV, nextV));
                yOptLength = this.distanceTable[fromV][startNode];
                gOptLocal = g + (brokenEdgeLength - yOptLength);

                if(gOptLocal > gOpt) {
                    gOpt = gOptLocal;
                    this.tour.set(startNode, fromV);
                }
                
                g += brokenEdgeLength - this.distanceTable[fromV][nextV];
                reverse(fromV, lastPossibleNextV);

                nextFromV = lastPossibleNextV;
                this.tour.set(fromV, nextV);
                lastNextV = nextV;
                fromV = nextFromV;
            }
        } while(nextV != -1);

        if(this.getDistance() > initialTourDistance) {
            throw new NegativeGainException(); 
        }
    
        // print tour;
        // TODO check if tour

    }

    /**
     * This function checks to see if the current tour solution is valid
     * @param Nothing
     * @return boolean whether it is a tour or not
     */
    public boolean isValidTour() {
        // TODO
        return true;
    }

    /**
     * Reverse the tour between indices start and end
     * @param int the start index of the path that is going to be reversed
     * @param int the end index of the path that is going to be reversed
     * @return nothing
     */
    private void reverse(int start, int end) {
        int current = start;
        int next = this.tour.get(start);
        int nextNext;
        do {
            nextNext = this.tour.get(next);
            this.tour.set(next, current);

            current = next;
            next = nextNext;
        } while(current != end);
    }

    /**
     * This function returns a string with the current tour and its distance
     * @param None
     * @return String with the representation of the tour
     */
    public String toString() {
        String str = "[" + this.getDistance() + "] : ";
        boolean add = false;
        for(int city: this.tour) {
            if(add) {
                str += " => " + city;
            } else {
                str += city;
                add = true;
            }
        }
        return str;
    }
}
