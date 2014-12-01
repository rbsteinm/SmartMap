package ch.epfl.smartmap.cache;

import android.location.Location;

/**
 * Describes a rectangular zone
 * 
 * @author ritterni
 */
public interface Zone {

	/**
	 * @return The zone's first corner
	 */
	Location getPointA();

	/**
	 * @return The zone's second corner
	 */
	Location getPointB();
}
