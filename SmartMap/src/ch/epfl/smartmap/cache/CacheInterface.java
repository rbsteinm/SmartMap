package ch.epfl.smartmap.cache;

import java.util.Set;
import java.util.SortedSet;

import ch.epfl.smartmap.cache.Cache.SearchFilter;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.servercom.SmartMapClient;

/**
 * All methods that must be implemented by the Cache
 * 
 * @author jfperren
 */
interface CacheInterface {

    /**
     * @param invitation
     * @param callback
     */
    void acceptInvitation(Invitation invitation, NetworkRequestCallback callback);

    void addOnCacheListener(CacheListener listener);

    void addParticipantsToEvent(Set<Long> ids, Event event, NetworkRequestCallback callback);

    void createEvent(EventContainer createdEvent, NetworkRequestCallback callback);

    void declineInvitation(Invitation invitation, NetworkRequestCallback callback);

    /**
     * @return all Filters currently activated
     */
    Set<Filter> getAllActiveFilters();

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

    Set<User> getAllUsers();

    /**
     * @return all Events that should be displayed on the map
     */
    Set<Event> getAllVisibleEvents();

    /**
     * @return all Friends that should be displayed on the map
     */
    Set<User> getAllVisibleFriends();

    Filter getDefaultFilter();

    Event getEvent(long id);

    Set<Event> getEvents(SearchFilter<Event> filter);

    Set<Event> getEvents(Set<Long> ids);

    /**
     * @param id
     *            Filter's id
     * @return the Filter with corresponding id, {@code null} if it is not in
     *         Cache
     */
    Filter getFilter(long id);

    Set<Filter> getFilters(SearchFilter<Filter> searchFilter);

    /**
     * @param ids
     *            Set containing Filter ids
     * @return a Set containing the corresponding found Filters
     */
    Set<Filter> getFilters(Set<Long> ids);

    Set<Long> getFriendIds();

    Invitation getInvitation(long id);

    SortedSet<Invitation> getInvitations(SearchFilter<Invitation> filter);

    SortedSet<Invitation> getInvitations(Set<Long> ids);

    Set<Event> getLiveEvents();

    Set<Event> getMyEvents();

    Set<Event> getNearEvents();

    Set<Event> getParticipatingEvents();

    User getSelf();

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
    void initFromDatabase(DatabaseHelper database);

    void inviteFriendsToEvent(long eventId, Set<Long> usersIds, NetworkRequestCallback callback);

    void inviteUser(long id, NetworkRequestCallback callback);

    void logState();

    void modifyOwnEvent(EventContainer createdEvent, NetworkRequestCallback callback);

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
     * Creates a live instance with values from the FilterContainer. Update
     * previous instance if it was
     * already
     * in the Cache. Assigns a new unique Id.
     * 
     * @param newFilter
     *            Filter's informations
     */
    long putFilter(FilterContainer newFilter);

    /**
     * Creates for each FilterContainer a new live Filter instance with
     * corresponding values. Update those
     * that
     * were already in the Cache. Assigns a new unique Id for each filter.
     * 
     * @param newFilters
     *            Set with Filters' informations
     */
    void putFilters(Set<FilterContainer> newFilters);

    void putInvitation(InvitationContainer invitationInfo);

    void putInvitations(Set<InvitationContainer> invitationInfos);

    void putUser(UserContainer newFriend);

    void putUsers(Set<UserContainer> newUsers);

    void readAllInvitations();

    void removeEvent(long id);

    void removeEvents(Set<Long> ids);

    void removeFilter(long id);

    void removeFilters(Set<Long> ids);

    void removeFriend(long id, NetworkRequestCallback callback);

    void removeFriends(Set<Long> ids, NetworkRequestCallback callback);

    void removeParticipantsFromEvent(Set<Long> ids, Event event, NetworkRequestCallback callback);

    boolean removeUsers(Set<Long> userIds);

    void setBlockedStatus(UserContainer user, NetworkRequestCallback callback);

    void setBlockedStatus(UserContainer user, User.BlockStatus newBlockedStatus,
        NetworkRequestCallback callback);

    void updateFromNetwork(SmartMapClient networkClient, NetworkRequestCallback callback);

    void updateUserInfos(long id);
}