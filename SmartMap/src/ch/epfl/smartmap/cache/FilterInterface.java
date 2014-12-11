package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * A list of user IDs
 * 
 * @author ritterni
 */
public interface FilterInterface extends Displayable {

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

    ImmutableFilter getImmutableCopy();

    /**
     * @return The name of the list
     */
    String getName();

    boolean isActive();

    boolean update(ImmutableFilter filterInfo);
}