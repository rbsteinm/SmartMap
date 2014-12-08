package ch.epfl.smartmap.cache;

import android.content.Intent;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.gui.Utils;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class FriendInvitation implements Invitation {

    private static final String TAG = FriendInvitation.class.getSimpleName();
    private static final int ID_MASK = 0;

    private final User mUser;
    private int mStatus;

    public FriendInvitation(ImmutableInvitation invitation) {
        assert invitation.getType() == ImmutableInvitation.FRIEND_INVITATION;
        mUser = invitation.getUser();
        mStatus = invitation.getStatus();
    }

    @Override
    public boolean equals(Object that) {
        return ((that != null) && (that instanceof FriendInvitation) && (this.getId() == ((FriendInvitation) that)
            .getId()));
    }

    @Override
    public long getId() {
        return (mUser.getId() << 2) | ID_MASK;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getImmutableCopy()
     */
    @Override
    public ImmutableInvitation getImmutableCopy() {
        // TODO
        return null;
    }

    @Override
    public Intent getIntent() {
        Intent intent = null;
        if ((mStatus == READ) || (mStatus == UNREAD)) {
            intent = new Intent(Utils.sContext, FriendsPagerActivity.class);
            intent.putExtra("INVITATION", true);
        } else if (mStatus == ACCEPTED) {
            intent = new Intent(Utils.sContext, UserInformationActivity.class);
            intent.putExtra("USER", mUser.getId());
        }
        return intent;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public String getText() {
        return Utils.sContext.getResources().getString(R.string.notification_open_friend_list);
    }

    @Override
    public String getTitle() {
        return Utils.sContext.getResources().getString(R.string.notification_open_friend_list) + " "
            + mUser.getName();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getType()
     */
    @Override
    public int getType() {
        return Invitation.FRIEND_INVITATION;
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public int hashCode() {
        return (int) this.getId();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#update(ch.epfl.smartmap.cache.ImmutableInvitation)
     */
    @Override
    public void update(ImmutableInvitation invitation) {
        // TODO Auto-generated method stub
    }
}