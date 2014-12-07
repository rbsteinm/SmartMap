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
    private final ImmutableUser mUser;

    public AcceptedFriendInvitation(ImmutableInvitation invitation) {
        mUser = invitation.getUser();
    }

    @Override
    public long getId() {
        return mUser.getId();
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
        return Utils.sContext.getResources().getString(R.string.notification_open_friend_info1) + " " + mUser.getName()
            + " " + Utils.sContext.getResources().getString(R.string.notification_open_friend_info2);
    }

    @Override
    public String getTitle() {
        return mUser.getName() + " "
            + Utils.sContext.getResources().getString(R.string.notification_accepted_friend_title);
    }

    @Override
    public ImmutableUser getUser() {
        return mUser;
    }

    @Override
    public void setStatus(int newStatus) {
        // nothing to do
    }

}