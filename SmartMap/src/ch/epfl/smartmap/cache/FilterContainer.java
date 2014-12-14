package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.Set;

/**
 * This class only acts as a container for all the informations you may want to
 * pass to a filter. It doesn't
 * do any check for null/wrong values. You can use it to create a Filter (Beware
 * to set the required fields
 * then), or only to update one (in which case you can put null to indicate you
 * don't want to update the
 * corresponding value).
 * 
 * @author jfperren
 */
public class FilterContainer {

    private Set<Long> mIds;
    private String mName;
    private long mId;
    private boolean mIsActive;

    public FilterContainer(long id, String name, Set<Long> ids, boolean isActive) {
        mId = id;
        mIds = new HashSet<Long>(ids);
        mName = name;
        mIsActive = isActive;
    }

    public FilterContainer addId(long id) {
        mIds.add(id);
        return this;
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

    public FilterContainer removeId(long id) {
        mIds.remove(id);
        return this;
    }

    public FilterContainer setActive(boolean isActive) {
        mIsActive = isActive;
        return this;
    }

    public FilterContainer setId(long newId) {
        mId = newId;
        return this;
    }

    public FilterContainer setIds(Set<Long> newIds) {
        mIds = new HashSet<Long>(newIds);
        return this;
    }

    public FilterContainer setName(String newName) {
        mName = newName;
        return this;
    }
}
