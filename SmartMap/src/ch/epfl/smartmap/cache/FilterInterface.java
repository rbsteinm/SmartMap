package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * Describe methods that need to be implemented for any {@code Filter} subclass of the Application
 * 
 * @author ritterni
 */
public interface FilterInterface extends Displayable {

    /**
     * @return The whole list of IDs of the friends that are inside the {@code Filter}
     */
    Set<Long> getIds();

    /**
     * @return a {@code FilterContainer} instance containing all informations about this {@code Filter}
     */
    FilterContainer getContainerCopy();

    /**
     * @return The name of the {@code Filter}
     */
    String getName();

    /**
     *
     */
    // FIXME Difference with getIds?
    Set<Long> getVisibleFriends();

    /**
     * @return the isActive field of the {@code Filter}
     */
    boolean isActive();

    /**
     * Updates all values of the {@code Filter} with the values from the {@code FilterContainer}. If you don't
     * want to update a field, you can either set the "NO_VALUE" (i. e {@code NO_ID, NO_NAME, ...})
     * or {@code null}.
     * 
     * @param newValues
     *            Container with new values
     * @return {@code True} if a change was actually made.
     */
    boolean update(FilterContainer filterInfo);
}