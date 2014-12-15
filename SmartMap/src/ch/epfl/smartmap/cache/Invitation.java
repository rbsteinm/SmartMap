package ch.epfl.smartmap.cache;

/**
 * Describes a generic invitation on the application SmartMap
 * 
 * @author agpmilli
 */
public abstract class Invitation implements InvitationInterface, Comparable<Invitation> {

    public static final long NO_ID = -1;
    public static final long ALREADY_RECEIVED = -2;
    public static final User NO_USER = null;
    public static final Event NO_EVENT = null;
    public static final long NO_TIMESTAMP = -1;
    public static final int NO_STATUS = -1;
    public static final int NO_TYPE = -1;

    // Default Invitation
    public static final Invitation NO_INVITATION = new GenericInvitation(NO_ID, NO_TIMESTAMP, NO_STATUS,
        NO_USER, NO_EVENT, NO_TYPE);

    /*
     * int representing invitation status
     */
    public static final int UNREAD = 0;
    public static final int READ = 1;
    public static final int ACCEPTED = 2;
    public static final int DECLINED = 3;

    /*
     * int representing types of invitation
     */
    public static final int FRIEND_INVITATION = 0;
    public static final int EVENT_INVITATION = 1;
    public static final int ACCEPTED_FRIEND_INVITATION = 2;

    private int mStatus;

    private final long mId;
    private long mTimeStamp;

    /**
     * Constructor
     * 
     * @param id
     *            id of invitation
     * @param timeStamp
     *            timeStamp of invitation
     * @param status
     *            status of invitation
     */
    public Invitation(long id, long timeStamp, int status) {
        if (id < 0) {
            mId = Invitation.NO_ID;
        } else {
            mId = id;
        }

        if (timeStamp < 0) {
            mTimeStamp = Invitation.NO_TIMESTAMP;
        } else {
            mTimeStamp = timeStamp;
        }

        if ((status != Invitation.UNREAD) && (status != Invitation.READ) && (status != Invitation.DECLINED)
            && (status != Invitation.ACCEPTED)) {
            mStatus = Invitation.NO_STATUS;
        } else {
            mStatus = status;
        }
    }

    @Override
    public int compareTo(Invitation that) {
        return Long.valueOf(Long.valueOf(that.getTimeStamp())).compareTo(this.getTimeStamp());
    }

    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof GenericInvitation)
            && (this.getId() == ((GenericInvitation) that).getId());
    }

    @Override
    public InvitationContainer getContainerCopy() {
        return new InvitationContainer(mId, null, null, mStatus, mTimeStamp, NO_TYPE);
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public long getTimeStamp() {
        return mTimeStamp;
    }

    @Override
    public int hashCode() {
        return (int) this.getId();
    }

    @Override
    public String toString() {
        return "Invitation[type(" + this.getType() + "), status(" + this.getStatus() + "), timestamp("
            + this.getTimeStamp() + "), user(" + this.getUser() + "), event(" + this.getEvent() + ")]";
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#update(ch.epfl.smartmap.cache.
     * ImmutableInvitation)
     */
    @Override
    public boolean update(InvitationContainer invitation) {
        boolean hasChanged = false;
        if (invitation.getId() != mId) {
            throw new IllegalArgumentException("Cannot change Id of an invitation.");
        }

        if (invitation.getType() != this.getType()) {
            throw new IllegalArgumentException("Cannot change type of invitation");
        }

        if (invitation.getStatus() != mStatus) {
            this.isLegalStatus(invitation);
        }

        if (!(invitation.getTimeStamp() < 0) && (invitation.getTimeStamp() != mTimeStamp)) {
            mTimeStamp = invitation.getTimeStamp();
            hasChanged = true;
        }

        return hasChanged;
    }

    private boolean isLegalStatus(InvitationContainer invitation) {
        if ((invitation.getStatus() == Invitation.ACCEPTED)
            || (invitation.getStatus() == Invitation.DECLINED) || (invitation.getStatus() == Invitation.READ)
            || (invitation.getStatus() == Invitation.UNREAD)) {
            mStatus = invitation.getStatus();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create an invitation from a Container
     * 
     * @param container
     *            container used to build the invitation
     * @return
     *         built invitation
     */
    protected static Invitation createFromContainer(InvitationContainer container) {
        long id = container.getId();
        long timeStamp = container.getTimeStamp();
        int status = container.getStatus();
        User user = container.getUser();
        Event event = container.getEvent();
        int type = container.getType();

        return new GenericInvitation(id, timeStamp, status, user, event, type);
    }
}
