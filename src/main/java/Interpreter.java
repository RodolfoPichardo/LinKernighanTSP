import java.io.*;
import java.util.*;

/*
 * @author Rodolfo Pichardo
 * This class read a file in TSP format and converts it into a list of ids and points
 */
public class Interpreter {
    /* 
     * Class variables
     */
    private ArrayList<Integer> id;
    private ArrayList<Point> coordinates; 

    /**
     * Constructor:
     * This function takes the name of a file, opens it and parses it
     * @param String The name of the file
     */
	public Interpreter(File file) {
        // Initialize the class variables
        this.id = new ArrayList<Integer>();
        this.coordinates = new ArrayList<Point>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while((line = in.readLine()) != null) {
				try {
	                Token tokens = getTokens(line);
	                addId(tokens.getId());
	                addPoint(tokens.getPoint());
				} catch(IllegalArgumentException e) {}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * This funtion takes a string and tokenizes it. It expects the string to be have exactly 3 tokens
     * @param String the string one wants to tokenize
     * @return ArrayList<Array> the three tokens extracted.
     *
     * Expects data on this format "[id] [x coordinate] [y coordinate]",
     * Example: "1 587.2 323.1"
     */
    private Token getTokens(String line) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(line);
        try {
            int id = Integer.parseInt(tokenizer.nextToken());
            double x = Double.parseDouble(tokenizer.nextToken());
            double y = Double.parseDouble(tokenizer.nextToken());
            
            if(!tokenizer.hasMoreTokens()) {
                return new Token(id, x, y);
            }

        } catch(Exception e) {}
        throw new IllegalArgumentException();
    }

    /**
     * This function adds an id to the array of ids of cities
     * @param int the id to to be added
     * @return void
     */
    private void addId(int id) {
        this.id.add(id);
    }

    /**
     * This function adds an point to the array of coordinates for each city
     * @param Point the point with the coordinates for that city
     * @return void
     */
    private void addPoint(Point pt) {
        this.coordinates.add(pt);
    }

    public ArrayList<Integer> getIds() {
        return this.id;
    }

    public ArrayList<Point> getCoordinates() {
        return this.coordinates;
    }


}