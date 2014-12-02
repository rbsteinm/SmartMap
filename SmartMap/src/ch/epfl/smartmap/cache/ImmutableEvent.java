package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.location.Location;

/**
 * An event that can be seen on the map
 * 
 * @author jfperren
 */
public class ImmutableEvent {

    private final long mId;
    private final String mName;
    private final Long mCreatorId;
    private final String mDescription;
    private final Location mLocation;
    private final String mLocationString;
    private final GregorianCalendar mStartDate;
    private final GregorianCalendar mEndDate;
    private final List<Long> mParticipants;

    /**
     * Constructor, put {@code null} (or {@code User.NO_ID} for id) if you dont want the value to be taken
     * into account.
     * 
     * @param id
     * @param name
     * @param creatorId
     * @param description
     * @param startDate
     * @param endDate
     * @param location
     * @param locationString
     * @param participants
     */
    public ImmutableEvent(long id, String name, long creatorId, String description, Calendar startDate,
        Calendar endDate, Location location, String locationString, List<Long> participants) {

        mId = id;
        mName = name;
        mCreatorId = creatorId;
        mDescription = description;
        mStartDate = new GregorianCalendar(TimeZone.getDefault());
        mEndDate = new GregorianCalendar(TimeZone.getDefault());
        mStartDate.setTime(startDate.getTime());
        mEndDate.setTime(endDate.getTime());
        mParticipants = new ArrayList<Long>(participants);
        mLocation = new Location(location);
        mLocationString = locationString;
    }

    public long getCreatorId() {
        return mCreatorId;
    }

    public String getDescription() {
        return mDescription;
    }

    public Calendar getEndDate() {
        return (Calendar) mEndDate.clone();
    }

    public long getID() {
        return mId;
    }

    public Location getLocation() {
        return new Location(mLocation);
    }

    public String getLocationString() {
        return mLocationString;
    }

    public String getName() {
        return mName;
    }

    public GregorianCalendar getStartDate() {
        return mStartDate;
    }
}