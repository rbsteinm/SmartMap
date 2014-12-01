package ch.epfl.smartmap.servercom;

import java.util.List;

import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.cache.ImmutableEvent;
import ch.epfl.smartmap.cache.ImmutableUser;

/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats.
 * 
 * @author marion-S
 * @author Pamoi (code reviewed : 9.11.2014)
 */

public interface SmartMapClient {

	/**
	 * Accepts the invitation of the friend with id id
	 * 
	 * @param id
	 *            : the id of the friend which invited the user
	 * @return the new Friend
	 * @throws SmartMapClientException
	 */
	ImmutableUser acceptInvitation(long id) throws SmartMapClientException;

	/**
	 * Confirm the server that the acceptation of the given friend was received
	 * 
	 * @param id
	 * @throws SmartMapClientException
	 */
	void ackAcceptedInvitation(long id) throws SmartMapClientException;

	/**
	 * Acknowledges the server that the invitation to the given event was received.
	 * 
	 * @param eventId
	 */
	void ackEventInvitation(long eventId) throws SmartMapClientException;

	/**
	 * Confirm the server that the removed friend was received
	 * 
	 * @param id
	 * @throws SmartMapClientException
	 */
	void ackRemovedFriend(long id) throws SmartMapClientException;

	/**
	 * Asks the server to allow the friend with id id to see the position
	 * 
	 * @param id
	 *            : the id of the friend to allow
	 * @throws SmartMapClientException
	 */
	void allowFriend(long id) throws SmartMapClientException;

	/**
	 * Asks the server to allow the friends with ids in the list ids to see the
	 * position
	 * 
	 * @param ids
	 *            : the ids of the friends to allow
	 * @throws SmartMapClientException
	 */
	void allowFriendList(List<Long> ids) throws SmartMapClientException;

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
	void authServer(String name, long facebookId, String fbAccessToken) throws SmartMapClientException;

	/**
	 * Creates an event to be sent to the server
	 * 
	 * @param event
	 *            the event to create
	 * @return the event's id
	 * @throws SmartMapClientException
	 */
	long createPublicEvent(ImmutableEvent event) throws SmartMapClientException;

	/**
	 * Decline the invitation of the user with the given id
	 * 
	 * @param id
	 * @throws SmartMapClientException
	 */
	void declineInvitation(long id) throws SmartMapClientException;

	/**
	 * Asks the server to disallow the friend with id id to see the position
	 * 
	 * @param id
	 *            : the id of the friend to disallow
	 * @throws SmartMapClientException
	 */
	void disallowFriend(long id) throws SmartMapClientException;

	/**
	 * Asks the server to disallow the friends with ids in the list ids to see
	 * the position
	 * 
	 * @param ids
	 *            : the ids of the friends to disallow
	 * @throws SmartMapClientException
	 */
	void disallowFriendList(List<Long> ids) throws SmartMapClientException;

	/**
	 * Asks the server for the friends whose name begin with the given text
	 * 
	 * @param text
	 * @return the list of friends
	 * @throws SmartMapClientException
	 */
	List<ImmutableUser> findUsers(String text) throws SmartMapClientException;

	/**
	 * Asks the server to follow the friend with id id
	 * 
	 * @param id
	 *            : the id of the friend to follow
	 * @throws SmartMapClientException
	 */
	void followFriend(long id) throws SmartMapClientException;

	/**
	 * @param eventId
	 * @return the event for which we wanted informations
	 */
	ImmutableEvent getEventInfo(long eventId) throws SmartMapClientException;

	/**
	 * @return the events to which the user is invited.
	 *         For each retrieved invitation, must call {@code ackEventInvitation}
	 */
	List<ImmutableEvent> getEventInvitations() throws SmartMapClientException;

	/**
	 * @return the list of the friends ids
	 * @throws SmartMapClientException
	 */
	List<Long> getFriendsIds() throws SmartMapClientException;

	/**
	 * Retrieve the invitations from the server, and also the list of users that
	 * accepted an invitation from us, and the list of users that removed us from their friends
	 * 
	 * @return an object of type {@link NotificationBag} that encapsulates the 3 lists and offers methods
	 *         to ack the removed friends and the new friends. Must call this two methods to ack the server
	 *         that the new friends and the removed friends were retrieved
	 * @throws SmartMapClientException
	 */
	NotificationBag getInvitations() throws SmartMapClientException;

	/**
	 * @param id
	 * @return the profile picture of the friend with the given id
	 * @throws SmartMapClientException
	 */
	Bitmap getProfilePicture(long id) throws SmartMapClientException;

	/**
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return the public events in the given radius centered at the given point
	 */
	List<ImmutableEvent> getPublicEvents(double latitude, double longitude, double radius)
	    throws SmartMapClientException;

	ImmutableUser getUserInfo(long id) throws SmartMapClientException;

	/**
	 * Sends an invitation to the server for the friend with id "id"
	 * 
	 * @param id
	 *            the id of the frien to invite
	 * @throws SmartMapClientException
	 */
	void inviteFriend(long id) throws SmartMapClientException;

	/**
	 * Sends an invitation request for the given event to the given friends
	 * 
	 * @param eventId
	 * @param usersIds
	 */
	void inviteUsersToEvent(long eventId, List<Long> usersIds) throws SmartMapClientException;

	/**
	 * Asks the server to add the user to the event with the given id
	 * 
	 * @param eventId
	 */
	void joinEvent(long eventId) throws SmartMapClientException;

	/**
	 * Asks the server to remove the user from the event with the given id
	 * 
	 * @param eventId
	 */
	void leaveEvent(long eventId) throws SmartMapClientException;

	/**
	 * Asks to the server the friends positions
	 * 
	 * @return a map that maps each friend id to a position
	 * @throws SmartMapClientException
	 */

	List<ImmutableUser> listFriendsPos() throws SmartMapClientException;

	/**
	 * Asks the server to remove the given friend
	 * 
	 * @param id
	 * @throws SmartMapClientException
	 */
	void removeFriend(long id) throws SmartMapClientException;

	/**
	 * Asks the server to unfollow the friend with id id
	 * 
	 * @param id
	 *            : the id of the friend to unfollow
	 * @throws SmartMapClientException
	 */
	void unfollowFriend(long id) throws SmartMapClientException;

	/**
	 * Updates the given event in the server database
	 * 
	 * @param event
	 * @throws SmartMapClientException
	 */
	void updateEvent(ImmutableEvent event) throws SmartMapClientException;

	/**
	 * Sends the latitude and longitude to the server
	 * 
	 * @throws SmartMapClientException
	 */
	void updatePos(Location location) throws SmartMapClientException;
}