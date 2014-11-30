/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.smartmap.cache.ImmutableUser;

/**
 * @author Pamoi
 */
public class NetworkNotificationBag implements NotificationBag {

    private final List<ImmutableUser> mInvitingUsers;

    private final List<ImmutableUser> mNewFriends;
    private final List<Long> mRemovedFriends;

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

        mInvitingUsers = new ArrayList<ImmutableUser>(invitingUsers);
        mNewFriends = new ArrayList<ImmutableUser>(newFriends);
        mRemovedFriends = new ArrayList<Long>(removedFriendsIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
     */
    @Override
    public List<ImmutableUser> getInvitingUsers() {
        return new ArrayList<ImmutableUser>(mInvitingUsers);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getNewFriends()
     */
    @Override
    public List<ImmutableUser> getNewFriends() {
        return new ArrayList<ImmutableUser>(mNewFriends);
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
