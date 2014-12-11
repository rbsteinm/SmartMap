package ch.epfl.smartmap.servercom;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import ch.epfl.smartmap.cache.ImmutableInvitation;
import ch.epfl.smartmap.cache.ImmutableUser;
import ch.epfl.smartmap.cache.Invitation;

/**
 * @author Pamoi
 */
public class NetworkFriendInvitationBag implements InvitationBag {
    private final Set<ImmutableInvitation> mInvitations;
    private final Set<Long> mRemovedFriendsIds;

    /**
     * The constructor takes List arguments for compliance with server
     * communication code.
     * 
     * @param invitingUsers
     * @param newFriends
     * @param removedFriendsIds
     */
    public NetworkFriendInvitationBag(List<ImmutableUser> invitingUsers, List<ImmutableUser> newFriends,
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

        mInvitations = new HashSet<ImmutableInvitation>();
        long timeStamp = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00")).getTimeInMillis();

        for (ImmutableUser user : invitingUsers) {
            mInvitations.add(new ImmutableInvitation(Invitation.NO_ID, user, null, Invitation.UNREAD,
                timeStamp, Invitation.FRIEND_INVITATION));
        }

        for (ImmutableUser user : newFriends) {
            mInvitations.add(new ImmutableInvitation(Invitation.NO_ID, user, null, Invitation.UNREAD,
                timeStamp, Invitation.ACCEPTED_FRIEND_INVITATION));
        }

        mRemovedFriendsIds = new HashSet<Long>(removedFriendsIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
     */
    @Override
    public Set<ImmutableInvitation> getInvitations() {
        return new HashSet<ImmutableInvitation>(mInvitations);
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