package ch.epfl.smartmap.cache;

import java.util.Set;

import android.graphics.Bitmap;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author ritterni
 */
public class CustomFilter extends Filter {

    private static final String TAG = CustomFilter.class.getSimpleName();

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

    @Override
    public String getName() {
        return mName;
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#getVisibleFriends()
     */
    @Override
    public Set<Long> getVisibleFriends() {
        return this.getIds();
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