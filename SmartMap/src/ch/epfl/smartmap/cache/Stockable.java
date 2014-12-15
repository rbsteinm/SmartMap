package ch.epfl.smartmap.cache;

/**
 * Every Object that needs to be stockable in the Cache, in the Database or on the Server needs to implement
 * this.
 * 
 * @author jfperren
 */
public interface Stockable {

    // Default value
    long NO_ID = -1;

    /**
     * @return the unique ID of this object, as set on the Server (or inside the Database for Filters and
     *         Invitations)
     */
    long getId();
}
