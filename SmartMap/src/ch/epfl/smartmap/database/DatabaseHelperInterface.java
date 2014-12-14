package ch.epfl.smartmap.database;

import java.util.List;
import java.util.Set;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.FilterContainer;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.UserContainer;

/**
 * @author jfperren
 */
public interface DatabaseHelperInterface {

    /**
     * Stores an event in the database. If there's already an event with the
     * same ID, updates that event instead
     * The event must have an ID (given by the server)!
     * 
     * @param event
     *            The event to store
     */
    void addEvent(EventContainer event) throws IllegalArgumentException;

    /**
     * Adds a filter/userlist/friendlist to the database, and gives an ID to the
     * filter
     * 
     * @param filter
     *            The filter/list to add
     * @return The ID of the newly added filter in the filter database
     */
    long addFilter(FilterContainer filter);

    /**
     * Adds an invitation to the database
     * 
     * @param invitation
     *            The {@code ImmutableInvitation} to add to the database
     */
    long addInvitation(InvitationContainer invitation);

    /**
     * Adds a pending friend who invited the user to the database.
     * 
     * @param user
     *            The user who was sent a request
     */
    void addPendingFriend(long id);

    /**
     * Adds a user to the internal database. If an user with the same ID already
     * exists, updates that user instead.
     * 
     * @param user
     *            The user to add to the database
     */
    void addUser(UserContainer user);

    /**
     * Clears the database. Mainly for testing purposes.
     */
    void clearAll();

    /**
     * Deletes an event from the database
     * 
     * @param event
     *            The event to delete
     */
    void deleteEvent(long id);

    /**
     * Deletes a filter from the database
     * 
     * @param filter
     *            The filter to delete
     */
    void deleteFilter(long id);

    /**
     * Deletes an invitation from the database (call this when accepting or
     * declining an invitation)
     * 
     * @param id
     *            The inviter's id
     */
    void deleteInvitation(long id);

    /**
     * Deletes a pending friend request from the database
     * 
     * @param id
     *            The invited user's id
     */
    void deletePendingFriend(long id);

    /**
     * Deletes a user from the database
     * 
     * @param id
     *            The user's id
     */
    void deleteUser(long id);

    /**
     * @return the {@code List} of all events
     */
    Set<EventContainer> getAllEvents();

    /**
     * @return the {@code List} of all Filters
     */
    Set<FilterContainer> getAllFilters();

    /**
     * Returns all invitations (friend requests, event invitations, etc)
     * 
     * @return a {@code List} of {@code ImmutableInvitation}s, sorted by ID
     */
    Set<InvitationContainer> getAllInvitations();

    /**
     * @return the {@code Set} of all users
     */
    Set<UserContainer> getAllUsers();

    /**
     * @param id
     *            The event's ID
     * @return The event associated to this ID
     */
    EventContainer getEvent(long id);

    /**
     * Gets a specific filter by its id
     * 
     * @param name
     *            The filter's id
     * @return The filter as a FriendList object
     */
    FilterContainer getFilter(long id);

    /**
     * @return a {@code List} containing the IDs of all stored filters
     */
    List<Long> getFilterIds();

    /**
     * @return the {@code List} containing all friend ids
     */
    List<Long> getFriendIds();

    /**
     * Returns a list of all pending friends
     * 
     * @return A list of users who were sent friend requests
     */
    Set<Long> getPendingFriends();

    /**
     * Gets a user from the database
     * 
     * @param userId
     *            The user's ID
     * @return The user's profile picture if it exists, a default picture
     *         otherwise
     */
    Bitmap getPictureById(long userId);

    /**
     * Gets a user from the database
     * 
     * @param id
     *            The user's unique ID
     * @return The user as a Friend object
     */
    UserContainer getUser(long id);

    void onCreate(SQLiteDatabase db);

    void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * Stores a profile picture
     * 
     * @param picture
     *            The picture to store
     * @param userId
     *            The user's ID
     */
    void setUserPicture(Bitmap picture, long userId);

    /**
     * Updates an event
     * 
     * @param event
     *            The event to update
     * @return The number of rows that were affected
     */
    int updateEvent(EventContainer event);

    /**
     * Updates a filter
     * 
     * @param filter
     *            The updated filter
     */
    void updateFilter(FilterContainer filter);

    /**
     * Updates a user's values
     * 
     * @param friend
     *            The user to update
     * @return The number of rows that were updated
     */
    int updateFriend(UserContainer friend);

    /**
     * Updates the database contents to be up-to-date with the cache
     */
    void updateFromCache();

    /**
     * Updates a {@code ImmutableInvitation} in the database
     * 
     * @param invitation
     *            The {@code ImmutableInvitation} to update
     * @return The number of rows that were updated
     */
    int updateInvitation(InvitationContainer invitation);

}