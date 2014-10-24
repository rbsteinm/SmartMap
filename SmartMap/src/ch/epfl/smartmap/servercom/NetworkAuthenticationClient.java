package ch.epfl.smartmap.servercom;

import ch.epfl.smartmap.cache.User;

/**
 * A {@link AuthenticationClient} implementation that uses a {@link NetworkProvider} to
 * communicate with a SmartMap server.
 * 
 * @author marion-S
 */

public class NetworkAuthenticationClient extends SmartMapClient implements
		AuthenticationClient {

	public NetworkAuthenticationClient(String serverUrl,
			NetworkProvider networkProvider) {
		super(serverUrl, networkProvider);

	}

	/* (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.AuthenticationClient#authServer(ch.epfl.smartmap.cache.User, java.lang.String)
	 */
	@Override
	public void authServer(User user, String fbAccessToken)
			throws SmartMapClientException {
		// TODO Auto-generated method stub
		
		
	}

}
