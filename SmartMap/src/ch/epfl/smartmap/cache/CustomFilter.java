package ch.epfl.smartmap.cache;

import java.util.Set;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.). Filters should not be instanciated
 * directly, but from the method {@code Filter.createFromContainer(...)}
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
        mName = name;
        mIsActive = isActive;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#getName()
     */
    @Override
    public String getName() {
        return mName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#getVisibleFriends()
     */
    @Override
    public Set<Long> getVisibleFriends() {
        return this.getIds();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#isActive()
     */
    @Override
    public boolean isActive() {
        return mIsActive;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#update(ch.epfl.smartmap.cache.FilterContainer)
     */
    @Override
    public boolean update(FilterContainer filterInfos) {
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