package ch.epfl.smartmap.severcom;

/**
 * @author marion-S
 *
 */

/**
 * A {@link SmartMapPositionClient} implementation that uses a
 * {@link NetworkProvider} to communicate with a SmartMap server.
 * 
 */
public class NetworkPositionClient implements SmartMapPositionClient {

	private String mServerUrl;
	private NetworkProvider mNetworkProvider;

	/**
	 * Creates a new NetworkPositionClient instance that communicates with a
	 * SmartMap server at a given location, through a {@link NetworkProvider}
	 * object.
	 * 
	 * @param serverUrl
	 *            the base URL of the SmartMap
	 * @param networkProvider
	 *            a NetworkProvider object through which to channel the server
	 *            communication.
	 */
	public NetworkPositionClient(String serverUrl,
			NetworkProvider networkProvider) {
		this.mServerUrl = serverUrl;
		this.mNetworkProvider = networkProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapPositionClient#updatePos()
	 */
	@Override
	public void updatePos() throws SmartMapClientException {
		// TODO Auto-generated method stub

	}

}
