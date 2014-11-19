package ch.epfl.smartmap.cache;

/**
 * A point defined by two coordinates
 * 
 * @author ritterni
 */
@Deprecated
public class Point {

    private double x;
    private double y;

    public Point(double xCoord, double yCoord) {
        x = xCoord;
        y = yCoord;
    }

    /**
     * @return The x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @return The y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the value of x
     * 
     * @param newX
     *            The new value for x
     */
    public void setX(double newX) {
        x = newX;
    }

    /**
     * Sets the value of y
     * 
     * @param newY
     *            The new value for y
     */
    public void setY(double newY) {
        y = newY;
    }
}
