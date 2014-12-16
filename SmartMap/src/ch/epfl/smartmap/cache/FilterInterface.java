package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * Describe methods that need to be implemented for any {@code Filter} subclass of the Application
 * 
 * @author ritterni
 */
public interface FilterInterface extends Displayable {

    /**
     * @return a {@code FilterContainer} instance containing all informations about this {@code Filter}
     */
    FilterContainer getContainerCopy();

    /**
     * @return a {@code Set} of ids that is contained in the Filter. For a {@code CustomFilter}, it gives you
     *         the list of ids contained allowed, and for a {@code DefaultFilter}, it returns the ids that are
     *         excluded. This is the {@code Set} that should be stored in the {@code DatabaseHelper}, but if
     *         you want to see wether a {@code Filter} contains someone, you should use
     *         {@code getVisibleFriends}.
     */
    Set<Long> getIds();

    /**
     * @return The name of the {@code Filter}
     */
    String getName();

    /**
     * @return The type of Filter (Can be {@code Filter.CUSTOM} or {@code Filter.DEFAULT})
     */
    int getType();

    /**
     * @return a {@code Set} containing all ids that should be left visible when this filter is activated
     */
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