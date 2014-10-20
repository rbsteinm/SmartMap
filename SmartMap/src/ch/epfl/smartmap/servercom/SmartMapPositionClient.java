package ch.epfl.smartmap.servercom;

import ch.epfl.smartmap.cache.Point;

/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats concerning the position's update.
 *
 */

/**
 * @author marion-S
 * 
 */
public interface SmartMapPositionClient {

	/**
	 * Sends the latitude and longitude to the server
	 * @throws SmartMapClientException
	 */
	void updatePos(Point position) throws SmartMapClientException;

}
