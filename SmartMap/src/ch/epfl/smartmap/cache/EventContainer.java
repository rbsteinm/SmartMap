package ch.epfl.smartmap.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import android.location.Location;

/**
 * This class only acts as a container of all the informations we may want to
 * pass to an event. It doesn't do
 * any check for null/wrong values. You can use this class to create an Event
 * (Beware having set all the
 * required fields then), or just to update the infotmations on an Event (you
 * can then use null values if you
 * don't want to update a field).
 * 
 * @author jfperren
 */
public class EventContainer {

    private long mId;
    private String mName;
    private String mDescription;
    private Location mLocation;
    private String mLocationString;
    private Calendar mStartDate;
    private Calendar mEndDate;
    private Set<Long> mParticipantIds;

    // An event contains informations about his creator
    private UserContainer mImmCreator;

    // These will be set by the cache
    private User mCreator;
    private Set<User> mParticipants;

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
    public EventContainer(long id, String name, UserContainer creator, String description,
        Calendar startDate, Calendar endDate, Location location, String locationString,
        Set<Long> participantIds) {

        mId = id;
        mName = name;
        mImmCreator = creator;
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
        return mImmCreator.getId();
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

    public UserContainer getImmCreator() {
        return mImmCreator;
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

    public Set<Long> getParticipantIds() {
        return mParticipantIds;
    }

    public Set<User> getParticipants() {
        return mParticipants;
    }

    public Calendar getStartDate() {
        return mStartDate;
    }

    public EventContainer setCreator(User newCreator) {
        mCreator = newCreator;
        return this;
    }

    public EventContainer setDescription(String newDescription) {
        mDescription = newDescription;
        return this;
    }

    public EventContainer setEndDate(GregorianCalendar newEndDate) {
        mEndDate = newEndDate;
        return this;
    }

    public EventContainer setId(long newId) {
        mId = newId;
        return this;
    }

    public EventContainer setImmutableCreator(UserContainer newCreator) {
        mImmCreator = newCreator;
        return this;
    }

    public EventContainer setLocation(Location newLocation) {
        mLocation = newLocation;
        return this;
    }

    public EventContainer setLocationString(String newLocationString) {
        mLocationString = newLocationString;
        return this;
    }

    public EventContainer setName(String newName) {
        mName = newName;
        return this;
    }

    public EventContainer setParticipantIds(Set<Long> newParticipantIds) {
        mParticipantIds = newParticipantIds;
        return this;
    }

    public EventContainer setStartDate(GregorianCalendar newStartDate) {
        mStartDate = newStartDate;
        return this;
    }
}