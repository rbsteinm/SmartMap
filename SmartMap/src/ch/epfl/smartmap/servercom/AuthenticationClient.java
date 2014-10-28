package ch.epfl.smartmap.servercom;


/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats concerning authentication process.
 *
 * @author marion-S
 *
 */

public interface AuthenticationClient {

	/**
	 * Send to the server the user's name, id and token
	 * @param user the user who authenticates
	 * @param fbAccessToken
	 * @throws SmartMapClientException
	 */
	void authServer(String name, long facebookId, String fbAccessToken) throws SmartMapClientException;

}
