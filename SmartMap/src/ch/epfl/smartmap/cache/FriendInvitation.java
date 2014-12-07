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

    private final ImmutableUser mUser;
    private int mStatus;

    public FriendInvitation(ImmutableInvitation invitation) {
        super();
        mUser = invitation.getUser();
        mStatus = invitation.getStatus();
    }

    @Override
    public long getId() {
        return mUser.getId();
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
        return Utils.sContext.getResources().getString(R.string.notification_open_friend_list) + " " + mUser.getName();
    }

    @Override
    public ImmutableUser getUser() {
        return mUser;
    }

    @Override
    public void setStatus(int newStatus) {
        mStatus = newStatus;
    }

}