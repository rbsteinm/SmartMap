package ch.epfl.smartmap.cache;

/**
 * Describes a generic invitation of the app
 * 
 * @author agpmilli
 */
public abstract class Invitation implements InvitationInterface, Comparable<Invitation> {

    public static final long NO_ID = -1;
    public static final long ALREADY_RECEIVED = -2;

    /**
     * int representing invitation status
     */
    public static final int UNREAD = 0;
    public static final int READ = 1;
    public static final int ACCEPTED = 2;
    public static final int DECLINED = 3;

    public static final int FRIEND_INVITATION = 0;
    public static final int EVENT_INVITATION = 1;
    public static final int ACCEPTED_FRIEND_INVITATION = 2;

    public static Invitation createFromContainer(InvitationContainer container) {
        long id = container.getId();
        long timeStamp = container.getTimeStamp();
        int status = container.getStatus();
        User user = container.getUser();
        Event event = container.getEvent();
        int type = container.getType();

        return new GenericInvitation(id, timeStamp, status, user, event, type);
    }

    private int mStatus;
    private final long mId;

    private long mTimeStamp;

    public Invitation(long id, long timeStamp, int status) {
        if (id < 0) {
            throw new IllegalArgumentException();
        } else {
            mId = id;
        }

        if (timeStamp < 0) {
            throw new IllegalArgumentException();
        } else {
            mTimeStamp = timeStamp;
        }

        if ((status != Invitation.UNREAD) && (status != Invitation.READ) && (status != Invitation.DECLINED)
            && (status != Invitation.ACCEPTED)) {
            throw new IllegalArgumentException("Unknown status : " + status);
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

    /**
     * @return immutable copy of this invitation
     */
    @Override
    public InvitationContainer getContainerCopy() {
        return new InvitationContainer(mId, null, null, mStatus, mTimeStamp, -1);
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

        if ((invitation.getStatus() == Invitation.ACCEPTED) || (invitation.getStatus() == Invitation.DECLINED)
            || (invitation.getStatus() == Invitation.READ) || (invitation.getStatus() == Invitation.UNREAD)) {
            mStatus = invitation.getStatus();
            hasChanged = true;
        }
        if (invitation.getTimeStamp() >= 0) {
            mTimeStamp = invitation.getTimeStamp();
            hasChanged = true;
        }

        return hasChanged;
    }
}
