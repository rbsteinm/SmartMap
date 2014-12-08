package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Describes a clientside, custom friend list (e.g. friends, family, etc.)
 * 
 * @author ritterni
 */
public class DefaultFilter implements Filter {

    private Set<Long> mIds;
    private String mName;
    private long mId;
    private boolean mIsActive;

    /**
     * @param name
     *            The name of the friend list
     * @param friendsDatabase
     *            Whole database of friends referenced by the friendlist
     */
    public DefaultFilter(ImmutableFilter filter) {
        mId = filter.getId();
        mName = filter.getName();
        mIds = new HashSet<Long>(filter.getIds());
        mIsActive = filter.isActive();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#addFriend(long)
     */
    @Override
    public void addFriend(long newFriend) {
        mIds.add(newFriend);

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#getFriendIds()
     */
    @Override
    public Set<Long> getFriendIds() {
        return new HashSet<Long>(mIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#getId()
     */
    @Override
    public long getId() {
        return mId;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getImage()
     */
    @Override
    public Bitmap getImage() {
        return Displayable.DEFAULT_IMAGE;
    }

    @Override
    public ImmutableFilter getImmutableCopy() {
        return new ImmutableFilter(mId, mName, mIds, mIsActive);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        // FIXME
        return new LatLng(0, 0);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLocation()
     */
    @Override
    public Location getLocation() {
        return Displayable.NO_LOCATION;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLocationString()
     */
    @Override
    public String getLocationString() {
        return Displayable.NO_LOCATION_STRING;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getMarkerOptions(android.content.Context)
     */
    @Override
    public BitmapDescriptor getMarkerIcon(Context context) {
        return Displayable.NO_MARKER_ICON;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#getName()
     */
    @Override
    public String getName() {
        return mName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        return "This is a filter";
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getTitle()
     */
    @Override
    public String getTitle() {
        return mName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#isActive()
     */
    @Override
    public boolean isActive() {
        return mIsActive;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#update(ch.epfl.smartmap.cache.ImmutableFilter)
     */
    @Override
    public void update(ImmutableFilter filter) {
        mId = filter.getId();
        mName = filter.getName();
        mIds = new HashSet<Long>(filter.getIds());
        mIsActive = filter.isActive();
    }
}