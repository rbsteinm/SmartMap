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
public class NetworkInvitationsClient extends SmartMapClient implements
		SmartMapInvitationsClient {

	public NetworkInvitationsClient(String serverUrl,
			NetworkProvider networkProvider) {
		super(serverUrl, networkProvider);
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
