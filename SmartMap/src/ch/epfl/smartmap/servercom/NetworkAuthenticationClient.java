package ch.epfl.smartmap.servercom;

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

}
