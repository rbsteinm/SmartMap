package ch.epfl.smartmap.cache;

import java.util.Set;

import android.graphics.Bitmap;
import android.util.Log;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author agpmilli
 * @author jfperren
 */
public class DefaultFilter extends Filter {

    /**
     * @param name
     *            The name of the friend list
     * @param friendsDatabase
     *            Whole database of friends referenced by the friendlist
     */
    protected DefaultFilter(Set<Long> excludedIds) {
        super(Filter.DEFAULT_FILTER_ID, excludedIds);
    }

    @Override
    public String getName() {
        Log.d("Filter", "Return default name");
        return "Default Filter (this shouldn't be displayed)";
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSearchImage()
     */
    @Override
    public Bitmap getSearchImage() {
        // TODO
        return null;
    }

    @Override
    public Set<Long> getVisibleFriends() {
        Set<Long> nonBlockedFriends = ServiceContainer.getCache().getFriendIds();
        nonBlockedFriends.removeAll(this.getIds());
        Log.d("Default", "excluded : " + this.getIds());
        Log.d("Default", "visible : " + nonBlockedFriends);
        return nonBlockedFriends;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}