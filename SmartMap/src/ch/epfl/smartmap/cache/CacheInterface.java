package ch.epfl.smartmap.cache;

import java.util.Set;
import java.util.SortedSet;

import ch.epfl.smartmap.cache.Cache.SearchFilter;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.database.DatabaseHelperInterface;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * All methods that must be implemented by the Cache
 * 
 * @author jfperren
 */
interface CacheInterface {

    /**
     * @param invitation
     *            Invitation you want to accept
     * @param callback
     *            Callback that will be notified when something happens
     */
    void acceptInvitation(Invitation invitation, NetworkRequestCallback<Void> callback);

    /**
     * Adds a listener to the Cache
     * 
     * @param listener
     */
    void addOnCacheListener(CacheListener listener);

    /**
     * Adds participants to a given Event and notify listeners
     * 
     * @param ids
     *            ids of participants you want to add
     * @param event
     *            Event to which you add participants
     * @param callback
     *            Gets notified when something happens on the network
     */
    void addParticipantsToEvent(Set<Long> ids, Event event, NetworkRequestCallback<Void> callback);

    /**
     * Create a new Event with you as a creator, send it to the server and then add it to the cache while
     * calling listeners
     * 
     * @param createdEvent
     *            Container with informations about the Event
     * @param callback
     *            Gets notified when something happens on the network
     */
    void createEvent(EventContainer createdEvent, NetworkRequestCallback<Event> callback);

    /**
     * Decline a given invitation on the server an then sets the value in the Cache
     * 
     * @param invitation
     *            Invitation you want to decline
     * @param callback
     *            Gets notified when something happens on the network
     */
    void declineInvitation(Invitation invitation, NetworkRequestCallback<Void> callback);

    /**
     * @return all Filters currently activated
     */
    Set<Filter> getAllActiveFilters();

    /**
     * @return all Filters except the Default one
     */
    Set<Filter> getAllCustomFilters();

    /**
     * @return all Events contained in the Cache
     */
    Set<Event> getAllEvents();

    /**
     * @return all Filters contained in the Cache
     */
    Set<Filter> getAllFilters();

    /**
     * @return all Friends contained in the Cache
     */
    Set<User> getAllFriends();

    /**
     * @return all Invitations contained in the Cache
     */
    Set<Invitation> getAllInvitations();

    /**
     * @return All Users contained in the Cache
     */
    Set<User> getAllUsers();

    /**
     * @return all Events that should be displayed on the map
     */
    Set<Event> getAllVisibleEvents();

    /**
     * @return all Friends that should be displayed on the map
     */
    Set<User> getAllVisibleFriends();

    /**
     * @return the Default Filter
     */
    Filter getDefaultFilter();

    /**
     * @param id
     *            Id of the Event
     * @return the Event with given Id, or {@code null} if the Event is not in the Cache
     */
    Event getEvent(long id);

    /**
     * @param filter
     *            Allows you to narrow your search
     * @return Events that correspond to the condition on the filter
     */
    Set<Event> getEvents(SearchFilter<Event> filter);

    /**
     * @param ids
     * @return Events with given Ids (an Id corresponding to no Event is simply ignored)
     */
    Set<Event> getEvents(Set<Long> ids);

    /**
     * @param id
     *            Filter's id
     * @return the Filter with corresponding id, {@code null} if it is not in
     *         Cache
     */
    Filter getFilter(long id);

    /**
     * @param searchFilter
     *            Allows you to narrow down your search
     * @return Filters that correspond to the condition on the searchFilter
     */
    Set<Filter> getFilters(SearchFilter<Filter> searchFilter);

    /**
     * @param ids
     *            Set containing Filter ids
     * @return a Set containing the corresponding found Filters
     */
    Set<Filter> getFilters(Set<Long> ids);

    /**
     * @return a {@code Set} containing all our Friends' id
     */
    Set<Long> getFriendIds();

    /**
     * @param id
     * @return the Invitation with the given id, {@code null} if not found
     */
    Invitation getInvitation(long id);

    /**
     * @param filter
     *            Allows you to narrow down your search
     * @return Invitations that satisfy the condition on the searchFilter
     */
    SortedSet<Invitation> getInvitations(SearchFilter<Invitation> filter);

    /**
     * @param ids
     * @return Invitations with given ids (non existing are ignored)
     */
    SortedSet<Invitation> getInvitations(Set<Long> ids);

    /**
     * @return a {@code Set} with all Live Events in the Cache
     */
    Set<Event> getLiveEvents();

    /**
     * @return a {@code Set} with all events that we created
     */
    Set<Event> getMyEvents();

    /**
     * @return a {@code Set} with all near events
     */
    Set<Event> getNearEvents();

    /**
     * @return a {@code Set} with all events to which we participate
     */
    Set<Event> getParticipatingEvents();

    /**
     * @return an {@code User} with our informations
     */
    User getSelf();

    /**
     * @return a {@code Set} with all unanswered Invitations
     */
    SortedSet<Invitation> getUnansweredFriendInvitations();

    /**
     * @param id
     *            Stranger's id
     * @return the Stranger with corresponding id, {@code null} if it is not in
     *         Cache
     */
    User getUser(long id);

    /**
     * @param ids
     *            Set containing User ids
     * @return a Set containing the corresponding found Users.
     */
    Set<User> getUsers(Set<Long> ids);

    /**
     * Completely wipes values and fill the Cache with what is contained in the
     * database
     */
    void initFromDatabase(DatabaseHelperInterface database);

    /**
     * Invite friends to a given event
     * 
     * @param eventId
     * @param usersIds
     * @param callback
     */
    void inviteFriendsToEvent(long eventId, Set<Long> usersIds, NetworkRequestCallback<Void> callback);

    void inviteUser(long id, NetworkRequestCallback<Void> callback);

    void modifyOwnEvent(EventContainer createdEvent, NetworkRequestCallback<Void> callback);

    void notifyEventListeners();

    /**
     * Creates a live instance with values from the EventContainer. Update
     * previous instance if it was already
     * in the Cache.
     * 
     * @param newEvent
     *            Event's informations
     */
    void putEvent(EventContainer newEvent);

    /**
     * Creates for each EventContainer a new live Event instance with
     * corresponding values. Update those that
     * were already in the Cache.
     * 
     * @param newEvents
     *            Set with Events' informations
     */
    void putEvents(Set<EventContainer> newEvents);

    /**
     * Creates a live instance with values from the FilterContainer. Update previous instance if it was
     * already in the Cache. Assigns a new unique Id. Notify listeners.
     * 
     * @param newFilter
     *            Filter's informations
     */
    void putFilter(FilterContainer newFilter);

    /**
     * Creates for each {@code FilterContainer} a new live Filter instance with corresponding values. Update
     * those that were already in the Cache. Assigns a new unique Id for each filter. Notify listeners.
     * 
     * @param newFilters
     *            Set with Filters' informations
     */
    void putFilters(Set<FilterContainer> newFilters);

    /**
     * Creates a new live instance with the informations from the {@code InvitationContainer}. Update those
     * that were already in the Cache. Notify listeners.
     * 
     * @param invitationInfo
     *            Invitation's informations
     */
    void putInvitation(InvitationContainer invitationInfo);

    /**
     * Creates for each {@code InvitationContainer} a new live {@code Invitation} instance with corresponding
     * values. Update those that were already in the Cache. Notify listeners.
     * 
     * @param invitationInfos
     *            {@code Set} with Invitations' informations
     */
    void putInvitations(Set<InvitationContainer> invitationInfos);

    /**
     * Create a new live instance with the informations from the {@code UserContainer}. Update if already
     * created. Notify listeners.
     * 
     * @param newUser
     *            User's informations
     */
    void putUser(UserContainer newUser);

    /**
     * Creates for each {@code UserContainer} a new live {@code User} instance with corrresponding values.
     * Update those that were already in the Cache. Notify listeners.
     * 
     * @param newUsers
     *            {@code Set} with Users' informations
     */
    void putUsers(Set<UserContainer> newUsers);

    /**
     * Mark all {@code Invitation}s as read.
     */
    void readAllInvitations();

    /**
     * Remove {@code Event} with given id from Cache.
     * 
     * @param id
     *            Id of the {@code Event}.
     */
    void removeEvent(long id);

    /**
     * Remove {@code Event}s with given ids from Cache.
     * 
     * @param ids
     *            Ids of the {@code Event}s.
     */
    void removeEvents(Set<Long> ids);

    /**
     * Remove {@code Filter} with given id from Cache.
     * 
     * @param id
     *            Id of the {@code Filter}.
     */
    void removeFilter(long id);

    /**
     * Remove {@code Filter}s with given id from Cache.
     * 
     * @param ids
     *            Ids of the {@code Filter}s.
     */
    void removeFilters(Set<Long> ids);

    /**
     * Unfriend someone on the server then remove him from the Cache
     * 
     * @param id
     *            Id of the friend
     * @param callback
     *            Gets notified with network informations
     */
    void removeFriend(long id, NetworkRequestCallback<Void> callback);

    /**
     * Unfriend people on the server then remove them from the Cache
     * 
     * @param ids
     *            Ids of the friends
     * @param callback
     *            Gets notified with network informations
     */
    void removeFriends(Set<Long> ids, NetworkRequestCallback<Void> callback);

    /**
     * Remove participants to an Event
     * 
     * @param ids
     *            Ids of the participants you want to remove
     * @param event
     *            Event to which you want to remove them
     * @param callback
     *            Gets notified with network informations
     */
    void removeParticipantsFromEvent(Set<Long> ids, Event event, NetworkRequestCallback<Void> callback);

    /**
     * Remove given Users from the Cache
     * 
     * @param userIds
     *            ids of the Users
     */
    void removeUsers(Set<Long> userIds);

    /**
     * Blocks/Unblocks someone on the Server and then update the value in the Cache
     * 
     * @param user
     *            User of which you want to change BlockStatus
     * @param callback
     *            Used to get feedback from the NetworkClient
     */
    void setBlockedStatus(UserContainer user, NetworkRequestCallback<Void> callback);

    /**
     * Updates all values contained in the Cache with the ones from the Network
     * 
     * @param networkClient
     *            Interface between Application and Server
     * @throws SmartMapClientException
     *             If there is a connectivity issue
     */
    void updateFromNetwork(SmartMapClient networkClient) throws SmartMapClientException;

    /**
     * Asks the NetworkClient for new informations and then updates the values of the User in the Cache.
     * 
     * @param id
     */
    void updateUserInfos(long id);
}