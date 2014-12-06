package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * A list of user IDs
 * 
 * @author ritterni
 */
public interface Filter extends Displayable {

    // Id of the Default filter
    public long DEFAULT_FILTER_ID = 0;

    /**
     * Adds a user to the list
     * 
     * @param id
     *            The user's ID
     */
    void addFriend(long newFriend);

    /**
     * @return The whole list of IDs
     */
    Set<Long> getFriendIds();

    /**
     * @return The name of the list
     */
    String getName();

    boolean isActive();

    void update(ImmutableFilter filter);
}