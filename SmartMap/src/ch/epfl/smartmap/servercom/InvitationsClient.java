package ch.epfl.smartmap.servercom;

import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats concerning invitation process.
 * 
 * @author marion-S
 */

public interface InvitationsClient {

	/**
	 * Sends an invitation to the server for the friend with id "id"
	 * 
	 * @param id the id of the frien to invite
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

}
