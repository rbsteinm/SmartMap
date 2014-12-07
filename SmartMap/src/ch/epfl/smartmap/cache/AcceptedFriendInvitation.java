package ch.epfl.smartmap.cache;

import android.content.Intent;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.gui.Utils;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class AcceptedFriendInvitation implements Invitation {
    private final long mUserId;

    public AcceptedFriendInvitation(ImmutableInvitation invitation) {
        mUserId = invitation.getUser();
    }

    @Override
    public long getId() {
        return mUserId;
    }

    @Override
    public Intent getIntent() {
        Intent intent = new Intent(Utils.sContext, UserInformationActivity.class);
        intent.putExtra("USER", mUserId);
        return intent;
    }

    @Override
    public int getStatus() {
        return ACCEPTED;
    }

    @Override
    public String getText() {
        return Utils.sContext.getResources().getString(R.string.notification_open_friend_info1) + " "
            + ServiceContainer.getCache().getUser(mUserId).getName() + " "
            + Utils.sContext.getResources().getString(R.string.notification_open_friend_info2);
    }

    @Override
    public String getTitle() {
        return ServiceContainer.getCache().getUser(mUserId).getName() + " "
            + Utils.sContext.getResources().getString(R.string.notification_accepted_friend_title);
    }

    @Override
    public ImmutableUser getUser() {
        return ServiceContainer.getCache().getUser(mUserId).getImmutableCopy();
    }

    @Override
    public void setStatus(int newStatus) {
        // nothing to do
    }

}