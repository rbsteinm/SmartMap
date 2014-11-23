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
     * Get a list of the users that sent an invitation request.
     * 
     * @return a list of the inviting users.
     */
    public List<User> getInvitingUsers();

    /**
     * Get a list of the friends that accepted the user's friend requests.
     * 
     * @return a list of the new friends.
     */
    public List<User> getNewFriends();

    /**
     * Get a list of the ids of friends that removed the user.
     * 
     * @return a list of ids.
     */
    public List<Long> getRemovedFriendsIds();

    /**
     * Acknowledges a new friend so it will not be returned by the server
     * anymore.
     * 
     * @param id
     */
    public void ackNewFriend(long id);

    /**
     * Acknowledges a removed friend so it will not be returned by the server
     * anymore.
     * 
     * @param id
     */
    public void ackRemovedFriend(long id);
}
