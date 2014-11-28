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
     * This class only represents a container of informations
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
     * @param location
     *            The event's location on the map
     */
    protected ImmutableEvent(long id, String name, long creatorId, String description, Calendar startDate,
        Calendar endDate, Location location, String locationString, List<Long> participants) {

        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (creatorId < 0) {
            throw new IllegalArgumentException("creatorId not valid");
        }
        if (description == null) {
            throw new IllegalArgumentException("description is null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate is null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate is null");
        }
        if (location == null) {
            throw new IllegalArgumentException("location is null");
        }
        if (locationString == null) {
            throw new IllegalArgumentException("locationString is null");
        }
        if (participants == null) {
            throw new IllegalArgumentException("participants is null");
        }

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