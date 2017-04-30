/**
 * This class represents a cartesian 2D point using its x and y coordinates
 */
class Point {
    /*
     * Instance variables
     */

    // The x coordinate
    private double x;

    // The y coordinate
    private double y;

    /**
     * This is the constructor that takes both coordinates and creates a point
     * @param double the x coordinates
     * @param double the y coordinates
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A getter that returns the x coordinates
     * @param None
     * @return double the x coordinates
     */
    public double getX() {
        return this.x;
    }

    /**
     * A getter that returns the y coordinates
     * @param None
     * @return double the y coordinates
     */ 
    public double getY() {
        return this.y;
    }


}