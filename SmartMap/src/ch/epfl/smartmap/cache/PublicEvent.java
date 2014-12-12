package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.util.Utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * An event that can be seen on the map
 * 
 * @author ritterni
 */

public class PublicEvent implements Event {

    private static final String TAG = PublicEvent.class.getSimpleName();

    // Mandatory fields
    private final long mId;
    private String mName;
    private Set<Long> mParticipantIds;
    private Calendar mStartDate;
    private Calendar mEndDate;
    private Location mLocation;
    // Optional fields
    private String mDescription;
    private String mLocationString;

    private final User mCreator;

    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;

    protected PublicEvent(ImmutableEvent event) {
        // Set id
        if (event.getId() >= 0) {
            mId = event.getId();
        } else {
            throw new IllegalArgumentException("Trying to create an Event with incorrect Id");
        }

        // Set other values
        mName = (event.getName() != null) ? event.getName() : NO_NAME;
        mCreator = (event.getCreator() != null) ? event.getCreator() : User.NOBODY;
        mStartDate = (event.getStartDate() != null) ? (Calendar) event.getStartDate().clone() : NO_START_DATE;
        mEndDate = (event.getEndDate() != null) ? (Calendar) event.getEndDate().clone() : NO_END_DATE;
        mLocation = (event.getLocation() != null) ? new Location(event.getLocation()) : NO_LOCATION;
        mLocationString = (event.getLocationString() != null) ? event.getLocationString() : NO_LOCATION_STRING;
        mDescription = (event.getDescription() != null) ? event.getDescription() : NO_DESCRIPTION;
        mParticipantIds =
            (event.getParticipantIds() != null) ? new HashSet<Long>(event.getParticipantIds()) : NO_PARTICIPANTIDS;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (this.getClass() == obj.getClass()) && (this.getId() == ((PublicEvent) obj).getId());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#getCreator()
     */
    @Override
    public User getCreator() {
        return mCreator;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public Calendar getEndDate() {
        return (Calendar) mEndDate.clone();
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public Bitmap getImage() {
        return Event.DEFAULT_WHITE_IMAGE;
    }

    @Override
    public ImmutableEvent getImmutableCopy() {
        return new ImmutableEvent(mId, mName, mCreator.getImmutableCopy(), mDescription, mStartDate, mEndDate,
            mLocation, mLocationString, mParticipantIds);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getLatLng()
     */
    @Override
    public LatLng getLatLng() {
        return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
    }

    @Override
    public Location getLocation() {
        return mLocation;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#getLocationString()
     */
    @Override
    public String getLocationString() {
        return mLocationString;
    }

    @Override
    public BitmapDescriptor getMarkerIcon(Context context) {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
    }

    @Override
    public String getName() {
        return mName;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#getParticipants()
     */
    @Override
    public Set<Long> getParticipantIds() {
        return mParticipantIds;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSearchImage()
     */
    @Override
    public Bitmap getSearchImage() {
        return Event.DEFAULT_BLUE_IMAGE;
    }

    @Override
    public Calendar getStartDate() {
        return mStartDate;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        return Utils.getDateString(mStartDate) + " at " + Utils.getTimeString(mStartDate) + ", near "
            + Utils.getCityFromLocation(mLocation);
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) mId;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#isGoing()
     */
    @Override
    public boolean isGoing() {
        return mParticipantIds.contains(ServiceContainer.getSettingsManager().getUserId());
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#isLive()
     */
    @Override
    public boolean isLive() {
        Calendar now = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        // Maybe it is the other way around
        return (mStartDate.compareTo(now) <= 0) && (mEndDate.compareTo(now) >= 0);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#isNear()
     */
    @Override
    public boolean isNear() {
        Location ourLocation = ServiceContainer.getSettingsManager().getLocation();
        return ourLocation.distanceTo(mLocation) <= ServiceContainer.getSettingsManager().getNearEventsMaxDistance();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#isOwn()
     */
    @Override
    public boolean isOwn() {
        Log.d(TAG, "creator id : " + mCreator.getId() + "  my id : "
            + ServiceContainer.getSettingsManager().getUserId());
        return mCreator.getId() == ServiceContainer.getSettingsManager().getUserId();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Localisable#isShown()
     */
    @Override
    public boolean isVisible() {
        return ServiceContainer.getSettingsManager().showPublicEvents();
    }

    @Override
    public boolean update(ImmutableEvent event) {
        // TODO Modify hasChanged to work correctly
        boolean hasChanged = false;

        if ((event.getName() != null) && !event.getName().equals("") && (event.getName() != Event.NO_NAME)) {
            hasChanged = true;
            mName = event.getName();
        }

        if (event.getStartDate() != null) {
            mStartDate = (Calendar) event.getStartDate().clone();
        }

        if (event.getStartDate() != null) {
            mEndDate = (Calendar) event.getEndDate().clone();
        }

        if ((event.getLocation() != null) && (event.getLocation() != Event.NO_LOCATION)) {
            mLocation = new Location(event.getLocation());
        }

        if ((event.getLocationString() != null) && (event.getLocationString() != Event.NO_LOCATION_STRING)) {
            mLocationString = event.getLocationString();
        }

        if ((event.getDescription() == null) && (event.getDescription() != Event.NO_DESCRIPTION)) {
            mDescription = event.getDescription();
        }

        if ((event.getParticipantIds() != null) && (event.getParticipantIds() != Event.NO_PARTICIPANTIDS)) {
            mParticipantIds = event.getParticipantIds();
        }

        return true;
    }
}
