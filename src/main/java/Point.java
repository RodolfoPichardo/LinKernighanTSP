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
     * @param x the x coordinates
     * @param y the y coordinates
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A getter that returns the x coordinates
     * @return the x coordinates
     */
    public double getX() {
        return this.x;
    }

    /**
     * A getter that returns the y coordinates
     * @return the y coordinates
     */ 
    public double getY() {
        return this.y;
    }


}