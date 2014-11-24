/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * An interface to encapsulate the informations given by the request
 * getInvitations of {@link SmartMapClient}.
 * It also offers a method to acknowledge the server that the list of new
 * friends was retrieved and one to
 * acknowledge that the list of removed friends was retrieved.
 * 
 * @author Pamoi
 */
public interface NotificationBag {

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
