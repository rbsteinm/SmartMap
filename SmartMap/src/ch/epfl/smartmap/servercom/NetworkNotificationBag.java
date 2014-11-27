/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * @author Pamoi
 */
public class NetworkNotificationBag implements NotificationBag {

	private List<User> mInvitingUsers;

	private List<User> mNewFriends;
	private List<Long> mRemovedFriends;

	public NetworkNotificationBag(List<User> invitingUsers, List<User> newFriends,
	    List<Long> removedFriendsIds) {
		if (invitingUsers == null) {
			throw new IllegalArgumentException("invitingUsers list is null.");
		}
		if (newFriends == null) {
			throw new IllegalArgumentException("newFriends list is null.");
		}
		if (removedFriendsIds == null) {
			throw new IllegalArgumentException("removedFriendsIds list is null.");
		}

		mInvitingUsers = new ArrayList<User>(invitingUsers);
		mNewFriends = new ArrayList<User>(newFriends);
		mRemovedFriends = new ArrayList<Long>(removedFriendsIds);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
	 */
	@Override
	public List<User> getInvitingUsers() {
		return new ArrayList<User>(mInvitingUsers);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.NotificationBag#getNewFriends()
	 */
	@Override
	public List<User> getNewFriends() {
		return new ArrayList<User>(mNewFriends);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.NotificationBag#getRemovedFriendsIds()
	 */
	@Override
	public List<Long> getRemovedFriendsIds() {
		return new ArrayList<Long>(mRemovedFriends);
	}
}
