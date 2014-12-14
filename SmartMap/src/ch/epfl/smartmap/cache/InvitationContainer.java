package ch.epfl.smartmap.cache;

/**
 * This only serves as a container to transfer {@code Invitation} informations
 * between different parts of the Application.
 * 
 * @author agpmilli
 */
public final class InvitationContainer {

    public static final int FRIEND_INVITATION = 0;
    public static final int EVENT_INVITATION = 1;
    public static final int ACCEPTED_FRIEND_INVITATION = 2;

    // Invitation informations
    private long mId;
    private int mStatus;
    private long mTimeStamp;
    private int mType;

    // Use of user container and event container
    private UserContainer mUserInfos;
    private EventContainer mEventInfos;

    // These will be instanciated and put here by the Cache
    private Event mEvent;
    private User mUser;

    /**
     * Constructor, put {@code null} (or {@code User.NO_ID} for id) if you dont
     * want the value to be taken into account.
     */
    public InvitationContainer(long id, UserContainer userInfos, EventContainer eventInfos, int status, long timeStamp,
        int type) {
        mId = id;
        mUserInfos = userInfos;
        mEventInfos = eventInfos;
        mStatus = status;
        mType = type;
        mTimeStamp = timeStamp;
    }

    /**
     * @return event field
     */
    public Event getEvent() {
        return mEvent;
    }

    /**
     * @return id of event
     */
    public long getEventId() {
        return mEventInfos != null ? mEventInfos.getId() : Event.NO_ID;
    }

    /**
     * @return event container
     */
    public EventContainer getEventInfos() {
        return mEventInfos;
    }

    /**
     * @return id of invitation
     */
    public long getId() {
        return mId;
    }

    /**
     * @return status of invitation
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * @return timeStamp of invitation
     */
    public long getTimeStamp() {
        return mTimeStamp;
    }

    /**
     * @return type of invitation (represented by int)
     */
    public int getType() {
        return mType;
    }

    /**
     * @return user field
     */
    public User getUser() {
        return mUser;
    }

    /**
     * @return id of user
     */
    public long getUserId() {
        return mUserInfos != null ? mUserInfos.getId() : User.NO_ID;
    }

    /**
     * @return user container
     */
    public UserContainer getUserInfos() {
        return mUserInfos;
    }

    /**
     * Set event field
     * 
     * @param newEvent
     *            event to be set
     * @return new Invitation with newEvent
     */
    public InvitationContainer setEvent(Event newEvent) {
        mEvent = newEvent;
        return this;
    }

    /**
     * Set user container field
     * 
     * @param newUserContainer
     *            user Container to be set
     * @return new Invitation with newUserContainer
     */
    public InvitationContainer setEventContainer(EventContainer newEventContainer) {
        mEventInfos = newEventContainer;
        return this;
    }

    /**
     * @param newId
     *            id to be set
     * @return new Invitation Container with newId
     */
    public InvitationContainer setId(long newId) {
        mId = newId;
        return this;
    }

    /**
     * Set Status field
     * 
     * @param newStatus
     *            status to be set
     * @return new Invitation with newStatus
     */
    public InvitationContainer setStatus(int newStatus) {
        mStatus = newStatus;
        return this;
    }

    /**
     * Set TimeStamp field
     * 
     * @param newTimeStamp
     *            TimeStamp to be set
     * @return new Invitation with newTimeStamp
     */
    public InvitationContainer setTimeStamp(long newTimeStamp) {
        mTimeStamp = newTimeStamp;
        return this;
    }

    /**
     * Set Type field
     * 
     * @param newType
     *            Type to be set
     * @return new Invitation with newType
     */
    public InvitationContainer setType(int newType) {
        mType = newType;
        return this;
    }

    /**
     * Set user field
     * 
     * @param newUser
     *            user to be set
     * @return new Invitation with newUser
     */
    public InvitationContainer setUser(User newUser) {
        mUser = newUser;
        return this;
    }

    /**
     * Set event container field
     * 
     * @param newEventContainer
     *            event Container to be set
     * @return new Invitation with newEventContainer
     */
    public InvitationContainer setUserContainer(UserContainer newUserContainer) {
        mUserInfos = newUserContainer;
        return this;
    }
}
