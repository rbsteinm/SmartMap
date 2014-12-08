package ch.epfl.smartmap.cache;

import android.content.Intent;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.gui.Utils;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class AcceptedFriendInvitation implements Invitation {

    private final static String TAG = AcceptedFriendInvitation.class.getSimpleName();
    private static final int ID_MASK = 2;
    private User mUser;

    public AcceptedFriendInvitation(ImmutableInvitation invitation) {
        assert invitation.getType() == ImmutableInvitation.ACCEPTED_FRIEND_INVITATION;
        mUser = invitation.getUser();
    }

    @Override
    public boolean equals(Object that) {
        return ((that != null) && (that instanceof AcceptedFriendInvitation) && (this.getId() == ((AcceptedFriendInvitation) that)
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
        Intent intent = new Intent(Utils.sContext, UserInformationActivity.class);
        intent.putExtra("USER", mUser.getId());
        return intent;
    }

    @Override
    public int getStatus() {
        return ACCEPTED;
    }

    @Override
    public String getText() {
        return Utils.sContext.getResources().getString(R.string.notification_open_friend_info1) + " "
            + mUser.getName() + " "
            + Utils.sContext.getResources().getString(R.string.notification_open_friend_info2);
    }

    @Override
    public String getTitle() {
        return mUser.getName() + " "
            + Utils.sContext.getResources().getString(R.string.notification_accepted_friend_title);
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
        // TODO
    }
}