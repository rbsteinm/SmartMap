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
    Set<EventInvitation> getAllEventInvitations();

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
    Set<FriendInvitation> getAllFriendInvitations();

    /**
     * @return all Friends contained in the Cache
     */
    Set<User> getAllFriends();

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
    Set<User> getAllVisibleFriends();

    /**
     * @param id
     *            Filter's id
     * @return the Filter with corresponding id, {@code null} if it is not in Cache
     */
    Filter getFilter(long id);

    /**
     * @param ids
     *            Set containing all Filter ids
     * @return a Set containing the corresponding Filters (not Found filters are not in the Set)
     */
    Set<Filter> getFilters(Set<Long> ids);
}
