package ch.epfl.smartmap.servercom;

import ch.epfl.smartmap.cache.Point;

/**
 * @author marion-S
 *
 */

/**
 * A {@link SmartMapPositionClient} implementation that uses a
 * {@link NetworkProvider} to communicate with a SmartMap server.
 * 
 */
public class NetworkPositionClient extends SmartMapClient implements
		SmartMapPositionClient {

	public NetworkPositionClient(String serverUrl,
			NetworkProvider networkProvider) {
		super(serverUrl, networkProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapPositionClient#updatePos()
	 */
	@Override
	public void updatePos(Point position) throws SmartMapClientException {
		// TODO Auto-generated method stub

	}

}
