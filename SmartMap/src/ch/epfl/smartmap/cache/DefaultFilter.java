package ch.epfl.smartmap.cache;

import java.util.Set;

import ch.epfl.smartmap.background.ServiceContainer;

/**
 * Class to represent the default filter, that contains all the friends for whom the user enabled the show on
 * map feature in {@code UserInformationActivity} . This filter is not visible to the user
 * and is used for programming
 * purposes. This should not be instanciated directly, but from the method
 * {@code Filter.createFromContainer(...)}
 * 
 * @author agpmilli
 * @author jfperren
 */
public class DefaultFilter extends Filter {

    /**
     * @param excludedIds
     */
    protected DefaultFilter(Set<Long> excludedIds) {
        super(Filter.DEFAULT_FILTER_ID, excludedIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#getName()
     */
    @Override
    public String getName() {
        return "Default Filter (this shouldn't be displayed)";
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#getVisibleFriends()
     */
    @Override
    public Set<Long> getVisibleFriends() {
        Set<Long> nonBlockedFriends = ServiceContainer.getCache().getFriendIds();
        nonBlockedFriends.removeAll(this.getIds());
        return nonBlockedFriends;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#isActive()
     * The default filter is always active
     */
    @Override
    public boolean isActive() {
        return true;
    }
}