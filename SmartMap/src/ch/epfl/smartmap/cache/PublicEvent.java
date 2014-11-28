package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.listeners.OnDisplayableUpdateListener;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An event that can be seen on the map
 * 
 * @author ritterni
 */
public class PublicEvent implements Event {

    private long mId;
    private String mName;
    private User mCreator;
    private String mDescription;
    private Location mLocation;
    private String mLocationString;
    private GregorianCalendar mStartDate;
    private GregorianCalendar mEndDate;
    private List<User> mParticipants;

    public final static int DEFAULT_ICON = R.drawable.default_event;

    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;

    /**
     * UserEvent constructor
     * 
     * @param name
     *            The name of the event
     * @param creatorId
     *            The id of the user who created the event
     * @param creatorName
     *            The name of the user who created the event
     * @param startDate
     *            The date at which the event starts
     * @param endDate
     *            The date at which the event ends
     * @param p
     *            The event's location on the map
     */
    public PublicEvent(String name, long creatorId, Calendar startDate, Calendar endDate, Location p) {
        if (name.isEmpty() || (name == null)) {
            throw new IllegalArgumentException("Invalid event name!");
        }
        if (creatorName == null) {
            throw new IllegalArgumentException("Invalid creator name!");
        }
        if (creatorId < 0) {
            throw new IllegalArgumentException("Invalid creator ID!");
        }
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("Invalid event dates!");
        }
        mEvtName = name;
        mEvtCreator = creatorId;
        mStartDate =
            new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DATE), startDate.get(Calendar.HOUR), startDate.get(Calendar.MINUTE));

        mStartDate.set(GregorianCalendar.HOUR_OF_DAY, startDate.get(GregorianCalendar.HOUR_OF_DAY));
        mStartDate.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        mStartDate.setTimeInMillis(startDate.getTimeInMillis());
        mEndDate =
            new GregorianCalendar(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DATE), endDate.get(Calendar.HOUR), endDate.get(Calendar.MINUTE));

        mEndDate.set(GregorianCalendar.HOUR_OF_DAY, endDate.get(GregorianCalendar.HOUR_OF_DAY));
        mEndDate.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        mEndDate.setTimeInMillis(endDate.getTimeInMillis());
        mLocation = new Location(p);
        mPositionName = "";
        mCreatorName = creatorName;
        mDescription = "Tomorrow near Lausanne";
        mID = NO_ID;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#addOnDisplayableUpdateListener(ch.epfl.smartmap.listeners.
     * OnDisplayableUpdateListener)
     */
    @Override
    public void addOnDisplayableUpdateListener(OnDisplayableUpdateListener listener) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ((obj != null) && (this.getClass() == obj.getClass()) && (this.getID() == ((PublicEvent) obj)
            .getID()));
    }

    @Override
    public long getCreatorId() {
        return mCreator.getID();
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
    public long getID() {
        return mId;
    }

    @Override
    public Bitmap getImage(Context context) {
        // Returns a generic event picture
        return BitmapFactory.decodeResource(context.getResources(), DEFAULT_ICON);
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

    /*
     * (non-Javadoc)
     * @see
     * ch.epfl.smartmap.cache.Displayable#getMarkerOptions(android.content.Context
     * )
     * @author hugo-S
     */
    @Override
    public MarkerOptions getMarkerOptions(Context context) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
            .title(this.getName())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y);
        return markerOptions;

    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public GregorianCalendar getStartDate() {
        return mStartDate;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        return mDescription;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Displayable#getTitle()
     */
    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
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
     * @see ch.epfl.smartmap.cache.Displayable#removeOnDisplayableUpdateListener(ch.epfl.smartmap.listeners.
     * OnDisplayableUpdateListener)
     */
    @Override
    public void removeOnDisplayableUpdateListener(OnDisplayableUpdateListener listener) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#setCreator(long)
     */
    @Override
    public void setCreator(long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCreatorName(String name) {
        if (name.isEmpty() || (name == null)) {
            throw new IllegalArgumentException("Invalid creator name!");
        }
        mCreatorName = name;
    }

    @Override
    public void setDescription(String desc) {
        mDescription = desc;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Event#setEndDate(java.util.Calendar)
     */
    @Override
    public void setEndDate(Calendar newDate) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEndDate(GregorianCalendar newDate) {
        if (newDate.before(mStartDate)) {
            throw new IllegalArgumentException("Invalid event dates!");
        }
        mEndDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DATE),
            newDate.get(Calendar.HOUR), newDate.get(Calendar.MINUTE));
    }

    @Override
    public void setID(long newID) {
        mID = newID;
    }

    @Override
    public void setLatitude(double y) {
        mLocation.setLatitude(y);
    }

    @Override
    public void setLocation(Location p) {
        mLocation.set(p);
    }

    @Override
    public void setLongitude(double x) {
        mLocation.setLongitude(x);

    }

    @Override
    public void setName(String newName) {
        if (newName.isEmpty() || (newName == null)) {
            throw new IllegalArgumentException("Invalid event name!");
        }
        mEvtName = newName;
    }

    @Override
    public void setPositionName(String posName) {
        mPositionName = posName;
    }

    @Override
    public void setStartDate(GregorianCalendar newDate) {
        if (mEndDate.before(newDate)) {
            throw new IllegalArgumentException("Invalid event dates!");
        }
        mStartDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DATE),
            newDate.get(Calendar.HOUR), newDate.get(Calendar.MINUTE));

    }
}
