package ch.epfl.smartmap.servercom;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.util.Utils;

/**
 * @author Pamoi
 */
public class NetworkFriendInvitationBag implements InvitationBag {
    private final Set<InvitationContainer> mInvitations;
    private final Set<Long> mRemovedFriendsIds;

    /**
     * The constructor takes List arguments for compliance with server communication code.
     * 
     * @param invitingUsers
     * @param newFriends
     * @param removedFriendsIds
     */
    public NetworkFriendInvitationBag(List<UserContainer> invitingUsers, List<UserContainer> newFriends,
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

        mInvitations = new HashSet<InvitationContainer>();
        long timeStamp = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND)).getTimeInMillis();

        for (UserContainer user : invitingUsers) {
            mInvitations.add(new InvitationContainer(Invitation.NO_ID, user, null, Invitation.UNREAD, timeStamp,
                    Invitation.FRIEND_INVITATION));
        }

        for (UserContainer user : newFriends) {
            mInvitations.add(new InvitationContainer(Invitation.NO_ID, user, null, Invitation.UNREAD, timeStamp,
                    Invitation.ACCEPTED_FRIEND_INVITATION));
        }

        mRemovedFriendsIds = new HashSet<Long>(removedFriendsIds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
     */
    @Override
    public Set<InvitationContainer> getInvitations() {
        return new HashSet<InvitationContainer>(mInvitations);
    }

    /**
     * Returns a set of the ids of the friends that removed the user.
     * 
     * @return
     */
    public Set<Long> getRemovedFriendsIds() {
        return new HashSet<Long>(mRemovedFriendsIds);
    }
}