package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.location.Location;

/**
 * This class only acts as a container of all the informations we may want to pass to an event. It doesn't do
 * any check for null/wrong values. You can use this class to create an Event (Beware having set all the
 * required fields then), or just to update the infotmations on an Event (you can then use null values if you
 * don't want to update a field).
 * 
 * @author jfperren
 */
public class ImmutableEvent {

    private long mId;
    private String mName;
    private Long mCreatorId;
    private String mDescription;
    private Location mLocation;
    private String mLocationString;
    private Calendar mStartDate;
    private Calendar mEndDate;
    private List<Long> mParticipantIds;

    // These will be set by the cache
    private User mCreator;
    private List<User> mParticipants;

    /**
     * Constructor
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
        Calendar endDate, Location location, String locationString, List<Long> participantIds) {

        mId = id;
        mName = name;
        mCreatorId = creatorId;
        mDescription = description;
        mStartDate = startDate;
        mEndDate = endDate;
        mParticipantIds = participantIds;
        mLocation = location;
        mLocationString = locationString;
    }

    public User getCreator() {
        return mCreator;
    }

    public long getCreatorId() {
        return mCreatorId;
    }

    public String getDescription() {
        return mDescription;
    }

    public Calendar getEndDate() {
        return mEndDate;
    }

    public long getId() {
        return mId;
    }

    public Location getLocation() {
        return mLocation;
    }

    public String getLocationString() {
        return mLocationString;
    }

    public String getName() {
        return mName;
    }

    public List<Long> getParticipantIds() {
        return mParticipantIds;
    }

    public List<User> getParticipants() {
        return mParticipants;
    }

    public Calendar getStartDate() {
        return mStartDate;
    }

    public ImmutableEvent setCreator(User newCreator) {
        mCreator = newCreator;
        return this;
    }

    public ImmutableEvent setCreatorId(long creatorId) {
        mCreatorId = creatorId;
        return this;
    }

    public ImmutableEvent setDescription(String newDescription) {
        mDescription = newDescription;
        return this;
    }

    public ImmutableEvent setEndDate(GregorianCalendar newEndDate) {
        mEndDate = newEndDate;
        return this;
    }

    public ImmutableEvent setId(long newId) {
        mId = newId;
        return this;
    }

    public ImmutableEvent setLocation(Location newLocation) {
        mLocation = newLocation;
        return this;
    }

    public ImmutableEvent setLocationString(String newLocationString) {
        mLocationString = newLocationString;
        return this;
    }

    public ImmutableEvent setName(String newName) {
        mName = newName;
        return this;
    }

    public ImmutableEvent setParticipants(List<User> newParticipants) {
        mParticipants = newParticipants;
        return this;
    }

    public ImmutableEvent setStartDate(GregorianCalendar newStartDate) {
        mStartDate = newStartDate;
        return this;
    }
}