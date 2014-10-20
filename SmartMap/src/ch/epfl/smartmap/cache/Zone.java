package ch.epfl.smartmap.cache;

/**
 * Describes a rectangular zone
 * @author ritterni
 */
public interface Zone {
    
    /**
     * @return The zone's first corner
     */
    Point getPointA();
    
    /**
     * @return The zone's second corner
     */
    Point getPointB();
}
