package ch.epfl.smartmap.cache;

/**
 * Every Object that needs to be stockable in the Cache, in the Database or on the Server needs to implement
 * this.
 * 
 * @author jfperren
 */
public interface Stockable {

    long NO_ID = -1;

    long getId();
}
