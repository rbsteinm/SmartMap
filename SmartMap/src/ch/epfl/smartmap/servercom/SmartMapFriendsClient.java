package ch.epfl.smartmap.servercom;

import java.util.List;

/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats concerning friends relationships
 *
 */

/**
 * @author marion-S
 * 
 */
public interface SmartMapFriendsClient {

	/**
	 * Asks to the server the friends positions
	 * 
	 * @return ??? to discuss
	 * @throws SmartMapClientException
	 */
	void listFriendPos() throws SmartMapClientException;

	/**
	 * Asks the server to follow the friend with id id
	 * @param id : the id of the friend to follow
	 * @throws SmartMapClientException
	 */
	void followFriend(int id) throws SmartMapClientException;
	
	/**
	 * Asks the server to unfollow the friend with id id
	 * @param id : the id of the friend to unfollow
	 * @throws SmartMapClientException
	 */
	void unfollowFriend(int id) throws SmartMapClientException;
	
	/**
	 * Asks the server to allow the friend with id id to see the position
	 * @param id : the id of the friend to allow
	 * @throws SmartMapClientException
	 */
	void allowFriend(int id) throws SmartMapClientException;
	
	/**
	 * Asks the server to disallow the friend with id id to see the position
	 * @param id : the id of the friend to disallow
	 * @throws SmartMapClientException
	 */
	void disallowFriend(int id) throws SmartMapClientException; 
	
	/**
	 * Asks the server to allow the friends with ids in the list ids to see the position
	 * @param ids : the ids of the friends to allow
	 * @throws SmartMapClientException
	 */
	void allowFriendList(List<Integer> ids) throws SmartMapClientException; 
	
	/**
	 * Asks the server to disallow the friends with ids in the list ids to see the position
	 * @param ids : the ids of the friends to disallow
	 * @throws SmartMapClientException
	 */
	void disallowFriendList(List<Integer> ids) throws SmartMapClientException; 
	
	

}
