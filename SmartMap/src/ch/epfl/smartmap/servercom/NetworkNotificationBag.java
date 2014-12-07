package ch.epfl.smartmap.servercom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.smartmap.cache.ImmutableUser;

/**
 * @author Pamoi
 */
public class NetworkNotificationBag implements NotificationBag {
    private Set<ImmutableUser> mInvitingUsers;
    private Set<ImmutableUser> mNewFriends;
    private Set<Long> mRemovedFriends;

    /**
     * The constructor takes List arguments for compliance with server
     * communication code.
     * 
     * @param invitingUsers
     * @param newFriends
     * @param removedFriendsIds
     */
    public NetworkNotificationBag(List<ImmutableUser> invitingUsers, List<ImmutableUser> newFriends,
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
        mInvitingUsers = new HashSet<ImmutableUser>(invitingUsers);
        mNewFriends = new HashSet<ImmutableUser>(newFriends);
        mRemovedFriends = new HashSet<Long>(removedFriendsIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
     */
    @Override
    public Set<ImmutableUser> getInvitingUsers() {
        return new HashSet<ImmutableUser>(mInvitingUsers);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getNewFriends()
     */
    @Override
    public Set<ImmutableUser> getNewFriends() {
        return new HashSet<ImmutableUser>(mNewFriends);
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