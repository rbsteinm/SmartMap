/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.ArrayList;
import java.util.List;

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
    public List<Long> getInvitingUsers() {
        return new ArrayList<Long>(mInvitingUsers);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getNewFriends()
     */
    @Override
    public List<Long> getNewFriends() {
        return new ArrayList<Long>(mNewFriends);
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
