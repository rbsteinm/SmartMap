package ch.epfl.smartmap.servercom;

import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats concerning invitation process.
 *
 */

/**
 * @author marion-S
 * 
 */
public interface SmartMapInvitationsClient {

	/**
	 * Sends an invitation to the server; the invited friend is identified by
	 * his phone number
	 * 
	 * @param invite_num: the phone number of the friend to invite
	 * @return the id of the invite user 
	 * @throws SmartMapClientException
	 */
	int inviteFriend(String num) throws SmartMapClientException;
	
	/**
	 * Retrieve the invitations from the server
	 * @return the list of inviters
	 * @throws SmartMapClientException
	 */
	List<User> getInvitations() throws SmartMapClientException;
	
	/**
	 * Accepts the invitation of the friend with id id
	 * @param id : the id of the friend which invited the user
	 * @return the new Friend
	 * @throws SmartMapClientException
	 */
	User acceptInvitation(int id) throws SmartMapClientException;
	
	/**
	 * Asks to the server informations about the user with id id
	 * @param id : the id of the user for which we want infos
	 * @return the User for which we wanted for infos
	 * @throws SmartMapClientException
	 */
	User getUserInfo(int id) throws SmartMapClientException;
	
	

}
