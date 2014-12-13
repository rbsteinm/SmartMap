package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * A list of user IDs
 * 
 * @author ritterni
 */
public interface FilterInterface extends Displayable {

    /**
     * @return The whole list of IDs
     */
    Set<Long> getIds();

    FilterContainer getImmutableCopy();

    /**
     * @return The name of the list
     */
    String getName();

    Set<Long> getVisibleFriends();

    boolean isActive();

    boolean update(FilterContainer filterInfo);
}