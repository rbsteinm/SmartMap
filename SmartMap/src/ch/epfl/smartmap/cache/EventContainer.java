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
 * required fields then), or just to update the informations on an Event (you
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
    private UserContainer mCreatorContainer;

    // This will be set by the Cache
    private User mCreator;

    /**
     * Constructor
     * 
     * @param id
     *            Event's id
     * @param name
     *            Event's name
     * @param creatorId
     *            Id of the creator
     * @param description
     *            Event's description
     * @param startDate
     *            Event's start date and time
     * @param endDate
     *            Event's end date and time
     * @param location
     *            Event's location
     * @param locationString
     *            a String about the Event's location
     * @param participants
     *            a {@code Set} with all participants' ids.
     */
    public EventContainer(long id, String name, UserContainer creator, String description,
        Calendar startDate, Calendar endDate, Location location, String locationString,
        Set<Long> participantIds) {

        mId = id;
        mName = name;
        mCreatorContainer = creator;
        mDescription = description;
        mStartDate = startDate;
        mEndDate = endDate;
        mParticipantIds = participantIds;
        mLocation = location;
        mLocationString = locationString;

    }

    /**
     * @return the Creator as live {@code User} instance (may return {@code null} if not set)
     */
    public User getCreator() {
        return mCreator;
    }

    /**
     * @return the Creator as {@code UserContainer} with his informations in case we need to add him in the
     *         {@code Cache}
     */
    public UserContainer getCreatorContainer() {
        return mCreatorContainer;
    }

    /**
     * @return the Event's description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @return the Event's start date
     */
    public Calendar getEndDate() {
        return mEndDate;
    }

    /**
     * @return the Event's id
     */
    public long getId() {
        return mId;
    }

    /**
     * @return the Event's location
     */
    public Location getLocation() {
        return mLocation;
    }

    /**
     * @return the Event's locationString
     */
    public String getLocationString() {
        return mLocationString;
    }

    /**
     * @return the Event's name
     */
    public String getName() {
        return mName;
    }

    /**
     * @return a {@code Set} with participant Ids
     */
    public Set<Long> getParticipantIds() {
        return mParticipantIds;
    }

    /**
     * @return the Event's start date
     */
    public Calendar getStartDate() {
        return mStartDate;
    }

    /**
     * @param newCreator
     *            new value for Creator
     * @return this
     */
    public EventContainer setCreator(User newCreator) {
        mCreator = newCreator;
        return this;
    }

    /**
     * @param newCreatorContainer
     *            new value for CreatorContainer
     * @return this
     */
    public EventContainer setCreatorContainer(UserContainer newCreatorContainer) {
        mCreatorContainer = newCreatorContainer;
        return this;
    }

    /**
     * @param newDescription
     *            new value for Description
     * @return this
     */
    public EventContainer setDescription(String newDescription) {
        mDescription = newDescription;
        return this;
    }

    /**
     * @param newEndDate
     *            new value for End Date
     * @return this
     */
    public EventContainer setEndDate(GregorianCalendar newEndDate) {
        mEndDate = newEndDate;
        return this;
    }

    /**
     * @param newId
     *            new value for Id
     * @return this
     */
    public EventContainer setId(long newId) {
        mId = newId;
        return this;
    }

    /**
     * @param newLocation
     *            new value for Location
     * @return this
     */
    public EventContainer setLocation(Location newLocation) {
        mLocation = newLocation;
        return this;
    }

    /**
     * @param newLocationString
     *            new value for Location String
     * @return this
     */
    public EventContainer setLocationString(String newLocationString) {
        mLocationString = newLocationString;
        return this;
    }

    /**
     * @param newName
     *            new value for Name
     * @return this
     */
    public EventContainer setName(String newName) {
        mName = newName;
        return this;
    }

    /**
     * @param newParticipantIds
     *            new {@code Set} of participant Ids
     * @return this
     */
    public EventContainer setParticipantIds(Set<Long> newParticipantIds) {
        mParticipantIds = newParticipantIds;
        return this;
    }

    /**
     * @param newStartDate
     *            new value for Start Date
     * @return this
     */
    public EventContainer setStartDate(GregorianCalendar newStartDate) {
        mStartDate = newStartDate;
        return this;
    }
}