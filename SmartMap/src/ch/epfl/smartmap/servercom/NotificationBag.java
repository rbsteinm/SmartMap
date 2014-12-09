/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.Set;

import ch.epfl.smartmap.cache.ImmutableUser;

/**
 * An interface to encapsulate the informations given by the request
 * getInvitations of {@link SmartMapClient}.
 * 
 * @author Pamoi
 */
public interface NotificationBag {

    /**
     * Get a list of the users that sent an invitation request.
     * 
     * @return a list of the inviting users.
     */
    Set<ImmutableUser> getInvitingUsers();

    /**
     * Get a list of the friends that accepted the user's friend requests.
     * 
     * @return a list of the new friends.
     */
    Set<ImmutableUser> getNewFriends();

    /**
     * Get a list of the ids of friends that removed the user.
     * 
     * @return a list of ids.
     */
    Set<Long> getRemovedFriendsIds();
}
