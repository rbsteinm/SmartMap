/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Pamoi
 */
public class NetworkNotificationBag implements NotificationBag {

	private final List<Long> mInvitingUsers;

	private final List<Long> mNewFriends;
	private final List<Long> mRemovedFriends;

	public NetworkNotificationBag(List<Long> invitingUsers, List<Long> newFriends,
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

		mInvitingUsers = new ArrayList<Long>(invitingUsers);
		mNewFriends = new ArrayList<Long>(newFriends);
		mRemovedFriends = new ArrayList<Long>(removedFriendsIds);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
	 */
	@Override
	public Set<Long> getInvitingUsers() {
		return new HashSet<Long>(mInvitingUsers);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.NotificationBag#getNewFriends()
	 */
	@Override
	public Set<Long> getNewFriends() {
		return new HashSet<Long>(mNewFriends);
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.servercom.NotificationBag#getRemovedFriendsIds()
	 */
	@Override
	public Set<Long> getRemovedFriendsIds() {
		return new HashSet<Long>(mRemovedFriends);
	}
}
