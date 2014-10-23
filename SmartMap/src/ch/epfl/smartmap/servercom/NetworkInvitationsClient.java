package ch.epfl.smartmap.servercom;

import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * A {@link InvitationsClient} implementation that uses a
 * {@link NetworkProvider} to communicate with a SmartMap server.
 * 
 * @author marion-S
 * 
 */
public class NetworkInvitationsClient extends SmartMapClient implements
		InvitationsClient {

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
