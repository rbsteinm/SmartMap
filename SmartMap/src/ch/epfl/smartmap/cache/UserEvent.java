package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import ch.epfl.smartmap.R;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An event that can be seen on the map
 * 
 * @author ritterni
 */
public class UserEvent implements Event, Searchable, Displayable {
    private String mEvtName;
    private final long mEvtCreator; // the user who created the event
    private final GregorianCalendar mStartDate;
    private final GregorianCalendar mEndDate;
    private long mID;
    private final Location mLocation;
    private String mPositionName;
    private String mCreatorName;
    private String mDescription;
    public final static long DEFAULT_ID = -1;
    public final static int EVENT_ICON = R.drawable.ic_launcher;
    private final static int RIGHT_SHIFT_COUNT = 32;

    public static final float MARKER_ANCHOR_X = (float) 0.5;
    public static final float MARKER_ANCHOR_Y = 1;

    /**
     * UserEvent constructor
     * 
     * @param name
     *            The name of the event
     * @param creator
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
    public UserEvent(String name, long creator, String creatorName, GregorianCalendar startDate,
        GregorianCalendar endDate, Location p) {
        mEvtName = name;
        mEvtCreator = creator;
        mStartDate =
            new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DATE), startDate.get(Calendar.HOUR), startDate.get(Calendar.MINUTE));

        mStartDate.set(GregorianCalendar.HOUR_OF_DAY, startDate.get(GregorianCalendar.HOUR_OF_DAY));
        mEndDate =
            new GregorianCalendar(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DATE), endDate.get(Calendar.HOUR), endDate.get(Calendar.MINUTE));

        mEndDate.set(GregorianCalendar.HOUR_OF_DAY, endDate.get(GregorianCalendar.HOUR_OF_DAY));

        mLocation = new Location(p);
        mPositionName = "";
        mCreatorName = creatorName;
        mDescription = "";
        mID = DEFAULT_ID;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        UserEvent other = (UserEvent) obj;
        if (mEvtName == null) {
            if (other.mEvtName != null) {
                return false;
            }
        } else if (!mEvtName.equals(other.mEvtName)) {
            return false;
        }
        if (mID != other.mID) {
            return false;
        }
        return true;
    }

    @Override
    public long getCreator() {
        return mEvtCreator;
    }

    @Override
    public String getCreatorName() {
        return mCreatorName;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public GregorianCalendar getEndDate() {
        return mEndDate;
    }

    @Override
    public long getID() {
        return mID;
    }

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
     * @see
     * ch.epfl.smartmap.cache.Displayable#getMarkerOptions(android.content.Context
     * )
     * @author hugo-S
     */
    @Override
    public MarkerOptions getMarkerOptions(Context context) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(this.getLatLng()).title(this.getName())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .anchor(MARKER_ANCHOR_X, MARKER_ANCHOR_Y);
        return markerOptions;

    }

    @Override
    public String getName() {
        return mEvtName;
    }

    @Override
    public Bitmap getPicture(Context context) {
        // Returns a generic event picture
        return BitmapFactory.decodeResource(context.getResources(), EVENT_ICON);
    }

    @Override
    public String getPositionName() {
        return mPositionName;
    }

    @Override
    public String getShortInfos() {
        return mDescription;
    }

    @Override
    public GregorianCalendar getStartDate() {
        return mStartDate;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((mEvtName == null) ? 0 : mEvtName.hashCode());
        result = (prime * result) + (int) (mID ^ (mID >>> RIGHT_SHIFT_COUNT));
        return result;
    }

    @Override
    public void setCreatorName(String name) {
        mCreatorName = name;
    }

    @Override
    public void setDescription(String desc) {
        mDescription = desc;
    }

    @Override
    public void setEndDate(GregorianCalendar newDate) {
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
        mEvtName = newName;
    }

    @Override
    public void setPositionName(String posName) {
        mPositionName = posName;
    }

    @Override
    public void setStartDate(GregorianCalendar newDate) {
        mStartDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DATE),
            newDate.get(Calendar.HOUR), newDate.get(Calendar.MINUTE));

    }
}
