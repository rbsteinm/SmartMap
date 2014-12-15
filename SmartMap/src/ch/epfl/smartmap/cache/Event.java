package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.util.Utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Describes an event
 * 
 * @author jfperren
 * @author ritterni
 */

public abstract class Event implements Displayable, EventInterface {

    public static final String TAG = Event.class.getSimpleName();

    // Default values
    public static final String NO_NAME = "Unknown Event";
    public static final String NO_DESCRIPTION = "This event currently has no description";
    public static final Calendar NO_START_DATE = GregorianCalendar.getInstance(TimeZone.getDefault());
    public static final Calendar NO_END_DATE = GregorianCalendar.getInstance(TimeZone.getDefault());
    public static final Set<User> NO_PARTICIPANTS = new HashSet<User>();
    public static final Set<Long> NO_PARTICIPANTIDS = new HashSet<Long>();
    // Default image
    public static final Bitmap DEFAULT_WHITE_IMAGE = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_event_white);
    public static final Bitmap DEFAULT_BLUE_IMAGE = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_event_blue);

    private final long mId;
    private String mName;
    private User mCreator;
    private Set<Long> mParticipantIds;
    private Calendar mStartDate;
    private Calendar mEndDate;
    private Location mLocation;
    private String mDescription;
    private String mLocationString;

    /**
     * Constructor
     * 
     * @param id
     * @param name
     * @param creator
     * @param startDate
     * @param endDate
     * @param location
     * @param locationString
     * @param description
     * @param participantIds
     */
    public Event(long id, String name, User creator, Calendar startDate, Calendar endDate, Location location,
        String locationString, String description, Set<Long> participantIds) {
        // Set id
        if (id >= 0) {
            mId = id;
        } else {
            throw new IllegalArgumentException("Trying to create an Event with incorrect Id");
        }

        // Set other values
        mName = (name != null) ? name : NO_NAME;
        mCreator = (creator != null) ? creator : User.NOBODY;
        mStartDate = (startDate != null) ? (Calendar) startDate.clone() : NO_START_DATE;
        mEndDate = (endDate != null) ? (Calendar) endDate.clone() : NO_END_DATE;
        mLocation = (location != null) ? new Location(location) : NO_LOCATION;
        mLocationString = (locationString != null) ? locationString : NO_LOCATION_STRING;
        mDescription = (description != null) ? description : NO_DESCRIPTION;
        mParticipantIds = (participantIds != null) ? new HashSet<Long>(participantIds) : NO_PARTICIPANTIDS;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (this.getClass() == obj.getClass())
            && (this.getId() == ((Event) obj).getId());
    }

    @Override
    public Bitmap getActionImage() {
        return Event.DEFAULT_WHITE_IMAGE;
    }

    @Override
    public EventContainer getContainerCopy() {
        return new EventContainer(mId, mName, mCreator.getContainerCopy(), mDescription, mStartDate,
            mEndDate, mLocation, mLocationString, mParticipantIds);
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
        return ourLocation.distanceTo(mLocation) <= ServiceContainer.getSettingsManager()
            .getNearEventsMaxDistance();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#isOwn()
     */
    @Override
    public boolean isOwn() {
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
    public boolean update(EventContainer event) {
        boolean hasChanged = false;

        if (event.getId() != mId) {
            throw new IllegalArgumentException("Cannot update an Event with a different ID !");
        }

        if ((event.getName() != null) && !event.getName().equals("") && (event.getName() != Event.NO_NAME)
            && !event.getName().equals(mName)) {
            mName = event.getName();
            hasChanged = true;
        }

        if ((event.getCreator() != null) && (event.getCreator() != User.NOBODY)
            && (event.getCreatorContainer().getId() != mCreator.getId())) {
            mCreator = event.getCreator();
        }

        if ((event.getCreatorContainer() != null) && (event.getCreatorContainer().getId() != User.NO_ID)
            && (event.getCreatorContainer().getId() != mCreator.getId())) {
            ServiceContainer.getCache().putUser(event.getCreatorContainer());
            if (ServiceContainer.getCache().getUser(event.getCreatorContainer().getId()) != null) {
                mCreator = ServiceContainer.getCache().getUser(event.getCreatorContainer().getId());
                hasChanged = true;
            }
        }

        if ((event.getStartDate() != null)
            && (event.getStartDate().getTimeInMillis() != Event.NO_START_DATE.getTimeInMillis())
            && (event.getStartDate().getTimeInMillis() != mStartDate.getTimeInMillis())) {
            mStartDate = (Calendar) event.getStartDate().clone();
            hasChanged = true;
        }

        if ((event.getEndDate() != null)
            && (event.getEndDate().getTimeInMillis() != Event.NO_END_DATE.getTimeInMillis())
            && (event.getEndDate().getTimeInMillis() != mEndDate.getTimeInMillis())) {
            mEndDate = (Calendar) event.getEndDate().clone();
            hasChanged = true;
        }

        if ((event.getLocation() != null)
            && (event.getLocation() != Event.NO_LOCATION)
            && ((event.getLocation().getLatitude() != mLocation.getLatitude()) || (event.getLocation()
                .getLongitude() != mLocation.getLongitude()))) {
            mLocation = new Location(event.getLocation());
            hasChanged = true;
        }

        if ((event.getLocationString() != null) && (event.getLocationString() != Event.NO_LOCATION_STRING)
            && !event.getLocationString().equals(mLocationString)) {
            mLocationString = event.getLocationString();
            hasChanged = true;
        }

        if ((event.getDescription() == null) && (event.getDescription() != Event.NO_DESCRIPTION)
            && !event.getDescription().equals(mDescription)) {
            mDescription = event.getDescription();
            hasChanged = true;
        }

        if ((event.getParticipantIds() != null) && (event.getParticipantIds() != Event.NO_PARTICIPANTIDS)
            && !event.getParticipantIds().equals(mParticipantIds)) {
            Log.d("EVENT", "" + event.getParticipantIds());
            Log.d("EVENT", "" + mParticipantIds);
            mParticipantIds = new HashSet<Long>(event.getParticipantIds());
            hasChanged = false;
        }

        return hasChanged;
    }

    /**
     * Create a new Live instance of {@code Event} from the values inside the {@code EventContainer}. DO NOT
     * CALL THIS OUTSIDE CACHE !
     * 
     * @param container
     *            Informations about the Event
     * @return the live instance of the Event
     */
    protected static Event createFromContainer(EventContainer container) {
        long id = container.getId();
        String name = container.getName();
        User creator = container.getCreator();
        Calendar startDate = container.getStartDate();
        Calendar endDate = container.getEndDate();
        Location location = container.getLocation();
        String locationString = container.getLocationString();
        String description = container.getDescription();
        Set<Long> participantIds = container.getParticipantIds();

        return new PublicEvent(id, name, creator, startDate, endDate, location, locationString, description,
            participantIds);
    }
}
