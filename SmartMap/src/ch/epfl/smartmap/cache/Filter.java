package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * A list of user IDs
 * 
 * @author ritterni
 */
public interface Filter extends Displayable {

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
     * Returns the list's ID for storing/loading purposes. Only gets a value
     * when the UserList is stored.
     * 
     * @return The ID
     */
    @Override
    long getId();

    /**
     * @return The name of the list
     */
    String getName();

    boolean isActive();

    void removeFriend(long newFriend);

    void setActive(boolean isActive);

    /**
     * Renames the list
     * 
     * @param newName
     *            The new name of the list
     */
    void setName(String newName);

    void update(ImmutableFilter filter);
}