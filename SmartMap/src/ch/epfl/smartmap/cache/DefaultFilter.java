package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author agpmilli
 * @author jfperren
 */
public class DefaultFilter extends Filter {

    private final Set<Long> mExcludedIds;

    /**
     * @param name
     *            The name of the friend list
     * @param friendsDatabase
     *            Whole database of friends referenced by the friendlist
     */
    protected DefaultFilter(Set<Long> excludedIds) {
        super(Filter.DEFAULT_FILTER_ID, null);
        mExcludedIds = new HashSet<Long>(excludedIds);
    }

    @Override
    public Set<Long> getFriendIds() {
        Set<Long> nonBlockedFriends = ServiceContainer.getCache().getFriendIds();
        nonBlockedFriends.removeAll(mExcludedIds);
        Log.d("Default", "visible : " + nonBlockedFriends);
        return nonBlockedFriends;
    }

    @Override
    public String getName() {
        return "Default Filter (this shouldn't be displayed)";
    }

    @Override
    public boolean isActive() {
        return true;
    }
}