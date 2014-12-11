package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author ritterni
 */
public class CustomFilter extends Filter {

    private static final String TAG = CustomFilter.class.getSimpleName();

    private String mName;
    private boolean mIsActive;
    private final Set<Long> mIds;

    /**
     * @param name
     *            The name of the friend list
     * @param friendsDatabase
     *            Whole database of friends referenced by the friendlist
     */
    protected CustomFilter(long id, Set<Long> ids, String name, boolean isActive) {
        super(id);
        mIds = ids;
        mName = name;
        mIsActive = isActive;
    }

    @Override
    public void addFriend(long newFriend) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<Long> getFriendIds() {
        return new HashSet<Long>(mIds);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public boolean update(ImmutableFilter filterInfos) {
        boolean hasChanged = false;

        if ((filterInfos.getName() != null) && !filterInfos.getName().equals(mName)) {
            mName = filterInfos.getName();
            hasChanged = true;
        }
        if (filterInfos.isActive() != mIsActive) {
            mIsActive = filterInfos.isActive();
            hasChanged = true;
        }

        if (filterInfos.getIds() != null) {
            Log.d(TAG, "set ids : " + filterInfos.getIds());
            mIds.clear();
            mIds.addAll(filterInfos.getIds());
            hasChanged = true;
        }

        return super.update(filterInfos) || hasChanged;
    }

}