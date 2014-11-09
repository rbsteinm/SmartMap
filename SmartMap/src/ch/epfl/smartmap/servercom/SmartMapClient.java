package ch.epfl.smartmap.servercom;

import java.util.List;
import java.util.Map;

import android.location.Location;

import ch.epfl.smartmap.cache.Point;
import ch.epfl.smartmap.cache.User;

/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats.
 * 
 * @author marion-S
 * 
 * @author Pamoi (code reviewed : 9.11.2014)
 */

public interface SmartMapClient {

	/**
	 * Send to the server the user's name, id and token
	 * 
	 * @param name
	 *            the name of the user who authenticates
	 * @param facebookId
	 *            the facebook id of the user
	 * @param fbAccessToken
	 * @throws SmartMapClientException
	 */
	void authServer(String name, long facebookId, String fbAccessToken)
			throws SmartMapClientException;

	/**
	 * Asks to the server the friends positions
	 * 
	 * @return a map that maps each friend id to a position
	 * @throws SmartMapClientException
	 */
	Map<Long, Location> listFriendPos() throws SmartMapClientException;

	/**
	 * Asks the server to follow the friend with id id
	 * 
	 * @param id
	 *            : the id of the friend to follow
	 * @throws SmartMapClientException
	 */
	void followFriend(int id) throws SmartMapClientException;

	/**
	 * Asks the server to unfollow the friend with id id
	 * 
	 * @param id
	 *            : the id of the friend to unfollow
	 * @throws SmartMapClientException
	 */
	void unfollowFriend(int id) throws SmartMapClientException;

	/**
	 * Asks the server to allow the friend with id id to see the position
	 * 
	 * @param id
	 *            : the id of the friend to allow
	 * @throws SmartMapClientException
	 */
	void allowFriend(int id) throws SmartMapClientException;

	/**
	 * Asks the server to disallow the friend with id id to see the position
	 * 
	 * @param id
	 *            : the id of the friend to disallow
	 * @throws SmartMapClientException
	 */
	void disallowFriend(int id) throws SmartMapClientException;

	/**
	 * Asks the server to allow the friends with ids in the list ids to see the
	 * position
	 * 
	 * @param ids
	 *            : the ids of the friends to allow
	 * @throws SmartMapClientException
	 */
	void allowFriendList(List<Integer> ids) throws SmartMapClientException;

	/**
	 * Asks the server to disallow the friends with ids in the list ids to see
	 * the position
	 * 
	 * @param ids
	 *            : the ids of the friends to disallow
	 * @throws SmartMapClientException
	 */
	void disallowFriendList(List<Integer> ids) throws SmartMapClientException;

	/**
	 * Sends an invitation to the server for the friend with id "id"
	 * 
	 * @param id
	 *            the id of the frien to invite
	 * @throws SmartMapClientException
	 */
	void inviteFriend(int id) throws SmartMapClientException;

	/**
	 * Retrieve the invitations from the server
	 * 
	 * @return the list of inviters
	 * @throws SmartMapClientException
	 */
	List<User> getInvitations() throws SmartMapClientException;

	/**
	 * Accepts the invitation of the friend with id id
	 * 
	 * @param id
	 *            : the id of the friend which invited the user
	 * @return the new Friend
	 * @throws SmartMapClientException
	 */
	User acceptInvitation(int id) throws SmartMapClientException;

	/**
	 * Asks to the server informations about the user with id id
	 * 
	 * @param id
	 *            : the id of the user for which we want infos
	 * @return the User for which we wanted for infos
	 * @throws SmartMapClientException
	 */
	User getUserInfo(int id) throws SmartMapClientException;

	/**
	 * Sends the latitude and longitude to the server
	 * 
	 * @throws SmartMapClientException
	 */
	void updatePos(Location location) throws SmartMapClientException;

}
