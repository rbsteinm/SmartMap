package ch.epfl.smartmap.servercom;

import java.util.List;

import android.graphics.Bitmap;
import android.location.Location;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.UserContainer;

/**
 * A client object to a SmartMap server that abstracts the underlying
 * communication protocol and data formats.
 * 
 * @author marion-S
 * @author Pamoi (code reviewed : 9.11.2014)
 */

public interface SmartMapClient {

    /**
     * Notify the server that accepted the invitation of the friend with id id
     * 
     * @param id
     *            : the id of the friend which invited the user
     * @return the new Friend
     * @throws SmartMapClientException
     */
    UserContainer acceptInvitation(long id) throws SmartMapClientException;

    /**
     * Confirm the server that the acceptation of the given friend was received. The list of accepted
     * invitations can be obtained with the {@link getInvitations} method
     * 
     * @param id
     *            the id of the friend whose accepted invitation was received
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void ackAcceptedInvitation(long id) throws SmartMapClientException;

    /**
     * Acknowledges the server that the invitation to the given event was
     * received. The event's invitations list can be retrieved with the {@link getEventInvitations} method
     * 
     * @param eventId
     *            the id of the event for which the invitation was received
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void ackEventInvitation(long eventId) throws SmartMapClientException;

    /**
     * Confirm the server that the removed friend was received. The list of friends who removed the user form
     * their friend list can be obtained with the {@link getInvitations} method
     * 
     * @param id
     *            the id of the friend who removed the user from his friends
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void ackRemovedFriend(long id) throws SmartMapClientException;

    /**
     * Send to the server the user's name, id and token. This request is sent when authenticating with
     * facebook when the app is launched
     * 
     * @param name
     *            the name of the user who authenticates
     * @param facebookId
     *            the facebook id of the user
     * @param fbAccessToken
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void authServer(String name, long facebookId, String fbAccessToken) throws SmartMapClientException;

    /**
     * Asks the server to block the friend with the given id. It means that this friend will no longer receive
     * our position, and we will no longer receive his position too
     * 
     * @param id
     *            the id of the friend we want to block
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void blockFriend(long id) throws SmartMapClientException;

    /**
     * Creates an event to be sent to the server
     * 
     * @param event
     *            the event to create, encapsulated in an {@link EventContainer} object
     * @return the event's id, set by the server
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    long createPublicEvent(EventContainer event) throws SmartMapClientException;

    /**
     * Notify the server that the invitation of the user with the given id was declined
     * 
     * @param id
     *            the id of the user whose the invitation was declined
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void declineInvitation(long id) throws SmartMapClientException;

    /**
     * Asks the server for the users whose name begin with the given text
     * 
     * @param text
     *            the text for which we want to find the users whose name begin with it
     * @return the list of users whose name begin with the given text
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    List<UserContainer> findUsers(String text) throws SmartMapClientException;

    /**
     * Asks to the server for the detailed informations about the given event
     * 
     * @param eventId
     *            the id of the event for which informations are wanted
     * @return the event for which we wanted informations
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    EventContainer getEventInfo(long eventId) throws SmartMapClientException;

    /**
     * @return an object of type {@link InvitationBag} that encapsulates the received informations
     *         For each retrieved invitation, must call {@link ackEventInvitation}
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    InvitationBag getEventInvitations() throws SmartMapClientException;

    /**
     * Retrieve the friend invitations from the server, and also the list of users that
     * accepted an invitation from us, and the list of users that removed us
     * from their friends. These informations are encapsulated in a {@link INvitationBag} object. After
     * executing this request, the methods {@link ackAcceptedInvotations} and {@link AckRemovedFriends} must
     * be called
     * 
     * @return an object of type {@link InvitationBag} that encapsulates the received informations
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    InvitationBag getFriendInvitations() throws SmartMapClientException;

    /**
     * @return the list of the friends ids
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    List<Long> getFriendsIds() throws SmartMapClientException;

    /**
     * Return the profile picture of the user with the given id, in an object of type {@link Bitmap}
     * 
     * @param id
     *            the id of the user whose profile picture is needed
     * @return the profile picture of the friend with the given id
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    Bitmap getProfilePicture(long id) throws SmartMapClientException;

    /**
     * Retrieve all the public events in the given area
     * 
     * @param latitude
     * @param longitude
     * @param radius
     * @return the public events in the given radius centered at the given point
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    List<Long> getPublicEvents(double latitude, double longitude, double radius)
        throws SmartMapClientException;

    /**
     * Retrieve informations about the user with the given id
     * 
     * @param id
     *            the id of the user whose informations are needed
     * @return the informations about the user, encapsulated in an {@link UserContainer} object
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    UserContainer getUserInfo(long id) throws SmartMapClientException;

    /**
     * Sends an invitation to the server for the user with the given id
     * 
     * @param id
     *            the id of the user to invite
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void inviteFriend(long id) throws SmartMapClientException;

    /**
     * Sends an invitation request for the given event to the given friends
     * 
     * @param eventId
     *            the id of the event to which we want to invite the given friends
     * @param friendsIds
     *            the ids of the friends we want to invite to the given event
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void inviteUsersToEvent(long eventId, List<Long> friendsIds) throws SmartMapClientException;

    /**
     * Asks the server to add the user to the event with the given id
     * 
     * @param eventId
     *            the id the user wants to join
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void joinEvent(long eventId) throws SmartMapClientException;

    /**
     * Asks the server to remove the user from the event with the given id
     * 
     * @param eventId
     *            the id of the event the user wants to leave
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void leaveEvent(long eventId) throws SmartMapClientException;

    /**
     * Asks to the server the friends positions
     * 
     * @return the list of friends, encapsulated in a {@link UserContainer} object, with their updated
     *         positions
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */

    List<UserContainer> listFriendsPos() throws SmartMapClientException;

    /**
     * Asks the server to remove the given friend from the user's list of friends
     * 
     * @param id
     *            the id of the friend we want to remove
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void removeFriend(long id) throws SmartMapClientException;

    /**
     * Asks the server to unblock the friend with the given id, so we can see its position and he can see ours
     * 
     * @param id
     *            the id of the fried we want to unblock
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void unblockFriend(long id) throws SmartMapClientException;

    /**
     * Updates the given event in the server database
     * 
     * @param event
     *            the event we want to updated, its informations are encapsulated in an {@link EventContainer}
     *            object
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void updateEvent(EventContainer event) throws SmartMapClientException;

    /**
     * Sends the user's own latitude and longitude to the server
     * 
     * @param location
     *            the location of the user, in a {@link Location} object
     * @throws SmartMapClientException
     *             in case the request could not be sent for any reason
     *             external to the application (network failure etc.)
     */
    void updatePos(Location location) throws SmartMapClientException;
}