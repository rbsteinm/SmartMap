package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author ritterni
 */
public class CustomFilter extends Filter {

    private String mName;
    private boolean mIsActive;

    /**
     * @param name
     *            The name of the friend list
     * @param friendsDatabase
     *            Whole database of friends referenced by the friendlist
     */
    protected CustomFilter(long id, Set<Long> ids, String name, boolean isActive) {
        super(id, ids);
        mName = this.getName();
        mIsActive = this.isActive();
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

        return super.update(filterInfos) || hasChanged;
    }

}