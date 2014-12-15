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

    // Filter informations
    private Set<Long> mIds;
    private String mName;
    private long mId;
    private boolean mIsActive;

    /**
     * Constructor, no check on any of the values
     * 
     * @param id
     * @param name
     * @param ids
     * @param isActive
     */
    public FilterContainer(long id, String name, Set<Long> ids, boolean isActive) {
        mId = id;
        mIds = new HashSet<Long>(ids);
        mName = name;
        mIsActive = isActive;
    }

    /**
     * @param id
     *            the id of the friend to add to the filter
     * @return this
     */
    public FilterContainer addId(long id) {
        mIds.add(id);
        return this;
    }

    /**
     * @return id field
     */
    public long getId() {
        return mId;
    }

    /**
     * @return ids field
     */
    public Set<Long> getIds() {
        return mIds;
    }

    /**
     * @return name field
     */
    public String getName() {
        return mName;
    }

    /**
     * @return isActive field
     */
    public boolean isActive() {
        return mIsActive;
    }

    /**
     * @param id
     *            the id of the friend to remove from the filter
     * @return this
     */
    public FilterContainer removeId(long id) {
        mIds.remove(id);
        return this;
    }

    /**
     * @param isActive
     *            the new isActive value
     * @return this
     */
    public FilterContainer setActive(boolean isActive) {
        mIsActive = isActive;
        return this;
    }

    /**
     * @param newId
     *            new value for Id
     * @return this
     */
    public FilterContainer setId(long newId) {
        mId = newId;
        return this;
    }

    /**
     * @param newIds
     *            new {@code Set} of contained ids
     * @return this
     */
    public FilterContainer setIds(Set<Long> newIds) {
        mIds = newIds;
        return this;
    }

    /**
     * @param newName
     *            new value for Name
     * @return this
     */
    public FilterContainer setName(String newName) {
        mName = newName;
        return this;
    }
}
