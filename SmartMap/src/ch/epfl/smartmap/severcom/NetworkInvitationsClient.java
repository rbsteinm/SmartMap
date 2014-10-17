package ch.epfl.smartmap.severcom;

import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * @author marion-S
 *
 */

/**
 * A {@link SmartMapInvitationsClient} implementation that uses a
 * {@link NetworkProvider} to communicate with a SmartMap server.
 * 
 */
public class NetworkInvitationsClient implements SmartMapInvitationsClient {

	private String mServerUrl;
	private NetworkProvider mNetworkProvider;

	/**
	 * Creates a new NetworkInvitationsClient instance that communicates with a
	 * SmartMap server at a given location, through a {@link NetworkProvider}
	 * object.
	 * 
	 * @param serverUrl
	 *            the base URL of the SmartMap
	 * @param networkProvider
	 *            a NetworkProvider object through which to channel the server
	 *            communication.
	 */
	public NetworkInvitationsClient(String serverUrl,
			NetworkProvider networkProvider) {
		this.mServerUrl = serverUrl;
		this.mNetworkProvider = networkProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.severcom.SmartMapInvitationsClient#inviteFriend(java
	 * .lang.String)
	 */
	@Override
	public int inviteFriend(String num) throws SmartMapClientException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getInvitations()
	 */
	@Override
	public List<User> getInvitations() throws SmartMapClientException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.epfl.smartmap.severcom.SmartMapInvitationsClient#acceptInvitation(int)
	 */
	@Override
	public User acceptInvitation(int id) throws SmartMapClientException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.smartmap.severcom.SmartMapInvitationsClient#getUserInfo(int)
	 */
	@Override
	public User getUserInfo(int id) throws SmartMapClientException {
		// TODO Auto-generated method stub
		return null;
	}

}
