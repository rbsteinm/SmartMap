/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * @author Pamoi
 */
public interface NotificationBag {

    /**
     * Acknowledges a new friend so it will not be returned by the server
     * anymore.
     * 
     * @param id
     */
    void ackNewFriend(long id);

    /**
     * Acknowledges a removed friend so it will not be returned by the server
     * anymore.
     * 
     * @param id
     */
    void ackRemovedFriend(long id);

    /**
     * Get a list of the users that sent an invitation request.
     * 
     * @return a list of the inviting users.
     */
    List<User> getInvitingUsers();

    /**
     * Get a list of the friends that accepted the user's friend requests.
     * 
     * @return a list of the new friends.
     */
    List<User> getNewFriends();

    /**
     * Get a list of the ids of friends that removed the user.
     * 
     * @return a list of ids.
     */
    List<Long> getRemovedFriendsIds();
}
