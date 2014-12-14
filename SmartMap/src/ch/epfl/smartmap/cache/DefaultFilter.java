package ch.epfl.smartmap.cache;

import java.util.Set;

import android.util.Log;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * Class to represent the default filter, that contains all the friends for whom the user enabled the show on
 * map feature in {@link UserInformationActivity} . This filter is not visible to the user
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
        Log.d("Filter", "Return default name");
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
        Log.d("Default", "excluded : " + this.getIds());
        Log.d("Default", "visible : " + nonBlockedFriends);
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