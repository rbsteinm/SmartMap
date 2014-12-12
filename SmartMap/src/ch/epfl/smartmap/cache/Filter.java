package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author agpmilli
 */
public abstract class Filter implements FilterInterface {

    private static final String TAG = Filter.class.getSimpleName();

    public static final long DEFAULT_FILTER_ID = 1;

    public static final long NO_ID = -1;

    public static final Bitmap DEFAULT_IMAGE = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_hashtag);

    private long mId;

    private Set<Long> mIds;

    protected Filter(long id, Set<Long> ids) {
        if (id < 0) {
            throw new IllegalArgumentException();
        }
        mId = id;
        mIds = new HashSet<Long>(ids);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Filter#getId()
     */
    @Override
    public long getId() {
        return mId;
    }

    @Override
    public Set<Long> getIds() {
        return new HashSet<Long>(mIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getImage()
     */
    @Override
    public Bitmap getImage() {
        return FilterInterface.DEFAULT_IMAGE;
    }

    @Override
    public ImmutableFilter getImmutableCopy() {
        return new ImmutableFilter(this.getId(), this.getName(), this.getIds(), this.isActive());
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
     * @see
     * ch.epfl.smartmap.cache.Displayable#getMarkerOptions(android.content.Context
     * )
     */
    @Override
    public BitmapDescriptor getMarkerIcon(Context context) {
        return Displayable.NO_MARKER_ICON;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        String subtitle = "";
        for (Long id : this.getIds()) {
            subtitle += ServiceContainer.getCache().getUser(id).getName() + " ";
        }
        return subtitle;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getTitle()
     */
    @Override
    public String getTitle() {
        return this.getName();
    }

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.cache.Filter#update(ch.epfl.smartmap.cache.ImmutableFilter
     * ()
     */
    @Override
    public boolean update(ImmutableFilter filter) {
        boolean hasChanged = false;

        if ((filter.getId() >= 0) && (filter.getId() != mId)) {
            mId = filter.getId();
            hasChanged = true;
        }

        if (filter.getIds() != null) {
            Log.d(TAG, "set ids : " + filter.getIds());
            mIds.clear();
            mIds.addAll(filter.getIds());
            hasChanged = true;
        }

        return hasChanged;
    }

    public static Filter createFromContainer(ImmutableFilter filterInfos) {
        long id = filterInfos.getId();
        Set<Long> ids = filterInfos.getIds();
        Boolean isActive = filterInfos.isActive();
        String name = filterInfos.getName();
        Log.d(TAG, "create filter with id " + filterInfos.getId());
        if (filterInfos.getId() == DEFAULT_FILTER_ID) {
            return new DefaultFilter(ids);
        } else if (filterInfos.getId() > 0) {
            return new CustomFilter(id, ids, name, isActive);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
