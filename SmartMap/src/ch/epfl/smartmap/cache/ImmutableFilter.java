package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jfperren
 */
public class ImmutableFilter {

    private Set<Long> mIds;
    private String mName;
    private long mId;
    private boolean mIsActive;

    protected ImmutableFilter(long id, String name, Set<Long> ids, boolean isActive) {
        mId = id;
        mIds = new HashSet<Long>(ids);
        mName = name;
        mIsActive = isActive;
    }

    public long getId() {
        return mId;
    }

    public Set<Long> getIds() {
        return new HashSet<Long>(mIds);
    }

    public String getName() {
        return mName;
    }

    public boolean isActive() {
        return mIsActive;
    }
}
