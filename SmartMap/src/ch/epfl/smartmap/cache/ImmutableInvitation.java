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

    // Invitation informations
    private final long mUserId;
    private int mStatus;

    /**
     * Constructor, put {@code null} (or {@code User.NO_ID} for id) if you dont
     * want the value to be taken
     * into account.
     */
    public ImmutableInvitation(long userId, int status) {
        mUserId = userId;
        mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public long getUser() {
        return mUserId;
    }

    public void setStatus(int newStatus) {
        mStatus = newStatus;
    }

}
