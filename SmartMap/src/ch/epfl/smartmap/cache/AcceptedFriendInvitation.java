package ch.epfl.smartmap.cache;

import android.content.Intent;
import android.graphics.Bitmap;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.gui.Utils;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class AcceptedFriendInvitation implements Invitation {
    private final long mInvitationId;
    private final long mUserId;
    private final String mUserName;
    private final Bitmap mImage;

    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";

    public AcceptedFriendInvitation(long invitationId, long userId, String userName, Bitmap image) {
        mInvitationId = invitationId;
        mUserId = userId;
        mUserName = userName;
        mImage = image;
    }

    @Override
    public long getId() {
        return mInvitationId;
    }

    @Override
    public Bitmap getImage() {
        return mImage;
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
            + mUserName + " "
            + Utils.sContext.getResources().getString(R.string.notification_open_friend_info2);
    }

    @Override
    public String getTitle() {
        return mUserName + " "
            + Utils.sContext.getResources().getString(R.string.notification_accepted_friend_title);
    }

    @Override
    public long getUserId() {
        return mUserId;
    }

    @Override
    public String getUserName() {
        return mUserName;
    }

    @Override
    public void setStatus(int newStatus) {
        // nothing to do
    }

}