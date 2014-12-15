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
 * Represents an User filter in the application SmartMap
 * 
 * @author agpmilli
 */
public abstract class Filter implements FilterInterface {

    private static final String TAG = Filter.class.getSimpleName();

    public static final long DEFAULT_FILTER_ID = 1;

    // Default values
    public static final long NO_ID = -1;
    public static final String NO_NAME = "Unknown filter";
    public static final Set<Long> NO_IDS = new HashSet<Long>();
    public static final Bitmap DEFAULT_WHITE_IMAGE = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_filter_white);
    public static final Bitmap DEFAULT_BLUE_IMAGE = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_filter_blue);

    private long mId;
    private final Set<Long> mIds;

    /**
     * Constructor
     * 
     * @param id
     *            Id of the Filter
     * @param ids
     *            Ids contained in the Filter
     */
    protected Filter(long id, Set<Long> ids) {
        if (id < 0) {
            throw new IllegalArgumentException();
        }
        mId = (id >= 0) ? id : Filter.NO_ID;
        mIds = (ids != null) ? new HashSet<Long>(ids) : Filter.NO_IDS;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getImage()
     */
    @Override
    public Bitmap getActionImage() {
        return Filter.DEFAULT_WHITE_IMAGE;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FilterInterface#getImmutableCopy()
     */
    @Override
    public FilterContainer getContainerCopy() {
        return new FilterContainer(this.getId(), this.getName(), this.getIds(), this.isActive());
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
     * @see ch.epfl.smartmap.cache.FilterInterface#getIds()
     */
    @Override
    public Set<Long> getIds() {
        return new HashSet<Long>(mIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        return new LatLng(Displayable.NO_LATITUDE, Displayable.NO_LONGITUDE);
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
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSearchImage()
     */
    @Override
    public Bitmap getSearchImage() {
        return DEFAULT_BLUE_IMAGE;
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
    public boolean update(FilterContainer filter) {
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

    /**
     * Does the conversion container -> live instance. DO NOT CALL THIS METHOD OUTSIDE CACHE.
     * 
     * @param filterInfos
     *            a Container that has all informations about the {@code Filter} you want to create.
     * @return the {@code Filter} live instance.
     */
    public static Filter createFromContainer(FilterContainer filterInfos) {
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
