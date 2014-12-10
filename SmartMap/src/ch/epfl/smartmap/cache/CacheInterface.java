package ch.epfl.smartmap.cache;

import java.util.List;
import java.util.Set;

/**
 * @author jfperren
 */
public interface CacheInterface {

    /**
     * @return all Filters currently activated
     */
    Set<Filter> getAllActivatedFilters();

    /**
     * @return all EventInvitations contained in the Cache
     */
    Set<GenericInvitation> getAllEventInvitations();

    /**
     * @return all Events contained in the Cache
     */
    Set<Event> getAllEvents();

    /**
     * @return all Filters contained in the Cache
     */
    Set<Filter> getAllFilters();

    /**
     * @return all FriendInvitations contained in the Cache
     */
    Set<GenericInvitation> getAllFriendInvitations();

    /**
     * @return all Friends contained in the Cache
     */
    Set<UserInterface> getAllFriends();

    /**
     * @return all Events to which we are participating
     */
    Set<Event> getAllGoingEvents();

    /**
     * @return all Invitations contained in the Cache
     */
    Set<Invitation> getAllInvitations();

    /**
     * @return a List where all Invitations are sorted in chronological order
     */
    List<Invitation> getAllInvitationsChronologically();

    /**
     * @return all Events that have invited us
     */
    Set<Event> getAllInvitingEvents();

    /**
     * @return all Events near our location
     */
    Set<Event> getAllNearEvents();

    /**
     * @return all Events that we created
     */
    Set<Event> getAllOwnEvents();

    /**
     * @return all Events that should be displayed on the map
     */
    Set<Event> getAllVisibleEvents();

    /**
     * @return all Friends that should be displayed on the map
     */
    Set<UserInterface> getAllVisibleFriends();

    /**
     * @param id
     *            Filter's id
     * @return the Filter with corresponding id, {@code null} if it is not in
     *         Cache
     */
    Filter getFilter(long id);

    /**
     * @param ids
     *            Set containing Filter ids
     * @return a Set containing the corresponding found Filters
     */
    Set<Filter> getFilters(Set<Long> ids);

    /**
     * @param id
     *            Friend's id
     * @return the Friend with corresponding id, {@code null} if it is not in
     *         Cache
     */
    UserInterface getFriend(long id);

    /**
     * @param ids
     *            Set containing Friend ids
     * @return a Set containing the corresponding found Friends.
     */
    UserInterface getFriends(Set<Long> ids);

    /**
     * @param id
     *            Stranger's id
     * @return the Stranger with corresponding id, {@code null} if it is not in
     *         Cache
     */
    UserInterface getStranger(long id);

    /**
     * @param ids
     *            Set containing Stranger ids
     * @return a Set containing the corresponding found Strangers.
     */
    UserInterface getStrangers(Set<Long> ids);

    /**
     * @param id
     *            Stranger's id
     * @return the Stranger with corresponding id, {@code null} if it is not in
     *         Cache
     */
    UserInterface getUser(long id);

    /**
     * @param ids
     *            Set containing User ids
     * @return a Set containing the corresponding found Users.
     */
    UserInterface getUsers(Set<Long> ids);

    /**
     * Completely wipes values and fill the Cache with what is contained in the
     * database
     */
    void initFromDatabase();

    /**
     * Creates a live instance with values from the EventContainer. Update
     * previous instance if it was already
     * in the Cache.
     * 
     * @param newEvent
     *            Event's informations
     */
    void putEvent(ImmutableEvent newEvent);

    /**
     * Creates for each EventContainer a new live Event instance with
     * corresponding values. Update those that
     * were already in the Cache.
     * 
     * @param newEvents
     *            Set with Events' informations
     */
    void putEvents(Set<ImmutableEvent> newEvents);

    /**
     * Creates a live instance with values from the FilterContainer. Update
     * previous instance if it was
     * already
     * in the Cache. Assigns a new unique Id.
     * 
     * @param newFilter
     *            Filter's informations
     */
    void putFilter(ImmutableFilter newFilter);

    /**
     * Creates for each FilterContainer a new live Filter instance with
     * corresponding values. Update those
     * that
     * were already in the Cache. Assigns a new unique Id for each filter.
     * 
     * @param newFilters
     *            Set with Filters' informations
     */
    void putFilters(Set<ImmutableFilter> newFilters);

    /**
     * Creates a live instance with values from the UserContainer. Update
     * previous instance if it was already
     * in the Cache.
     * 
     * @param newFriend
     *            Friend's informations
     */
    void putFriend(ImmutableUser newFriend);

    /**
     * Creates for each UserContainer a new live Friend instance with
     * corresponding values. Update those
     * that were already in the Cache.
     * 
     * @param newFriends
     *            Set with Friends' informations
     */
    void putFriends(Set<ImmutableUser> newFriend);

    /**
     * Creates a live instance with values from the UserContainer. Update
     * previous instance if it was already
     * in the Cache.
     * 
     * @param newStranger
     *            Stranger's informations
     */
    void putStranger(ImmutableUser newStranger);

    /**
     * Creates for each UserContainer a new live Stranger instance with
     * corresponding values. Update those
     * that were already in the Cache.
     * 
     * @param newFilters
     *            Set with Strangers' informations
     */
    void putStrangers(Set<ImmutableUser> newStrangers);
}
