package ch.epfl.smartmap.cache;

/**
 * Immutable implementation of Invitation, which serves only as container
 * purposes,
 * therefore all set methods are
 * disabled.
 * 
 * @author agpmilli
 */
public final class ImmutableInvitation {

    public static final int FRIEND_INVITATION = 0;
    public static final int EVENT_INVITATION = 1;
    public static final int ACCEPTED_FRIEND_INVITATION = 2;

    // Invitation informations
    private long mId;
    private int mStatus;
    private long mTimeStamp;
    private int mType;

    private final ImmutableUser mUserInfos;
    private final ImmutableEvent mEventInfos;

    // These will be instanciated and put here by the Cache
    private Event mEvent;
    private User mUser;

    /**
     * Constructor, put {@code null} (or {@code User.NO_ID} for id) if you dont
     * want the value to be taken
     * into account.
     */
    public ImmutableInvitation(long id, ImmutableUser userInfos, ImmutableEvent eventInfos, int status,
        long timeStamp, int type) {
        mId = id;
        mUserInfos = userInfos;
        mEventInfos = eventInfos;
        mStatus = status;
        mType = type;
        mTimeStamp = timeStamp;
    }

    public Event getEvent() {
        return mEvent;
    }

    public long getEventId() {
        return mEventInfos != null ? mEventInfos.getId() : Event.NO_ID;
    }

    public ImmutableEvent getEventInfos() {
        return mEventInfos;
    }

    public long getHash() {
        return (mUserInfos.getId() << 2) & mType;
    }

    public long getId() {
        return mId;
    }

    public int getStatus() {
        return mStatus;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public int getType() {
        return mType;
    }

    public User getUser() {
        return mUser;
    }

    public long getUserId() {
        return mUserInfos != null ? mUserInfos.getId() : User.NO_ID;
    }

    public ImmutableUser getUserInfos() {
        return mUserInfos;
    }

    public ImmutableInvitation setEvent(Event newEvent) {
        mEvent = newEvent;
        return this;
    }

    public ImmutableInvitation setId(long newId) {
        mId = newId;
        return this;
    }

    public ImmutableInvitation setStatus(int newStatus) {
        mStatus = newStatus;
        return this;
    }

    public ImmutableInvitation setTimeStamp(long newTimeStamp) {
        mTimeStamp = newTimeStamp;
        return this;
    }

    public ImmutableInvitation setType(int newType) {
        mType = newType;
        return this;
    }

    public ImmutableInvitation setUser(User newUser) {
        mUser = newUser;
        return this;
    }
}
