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
	private final long mInvitationId;
	private final long mUserId;
	private final String mUserName;

	public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
	public static final int IMAGE_QUALITY = 100;
	public static final String PROVIDER_NAME = "SmartMapServers";

	public AcceptedFriendInvitation(long invitationId, long userId, String userName) {
		mInvitationId = invitationId;
		mUserId = userId;
		mUserName = userName;
	}

	@Override
	public int getStatus() {
		return ACCEPTED;
	}

	@Override
	public Intent getIntent() {
		Intent intent = new Intent(Utils.context, UserInformationActivity.class);
		intent.putExtra("USER", mUserId);
		return intent;
	}

	@Override
	public long getID() {
		return mInvitationId;
	}

	@Override
	public String getText() {
		return Utils.context.getResources().getString(R.string.notification_open_friend_info1) + " "
		    + mUserName + " "
		    + Utils.context.getResources().getString(R.string.notification_open_friend_info2);
	}

	@Override
	public String getTitle() {
		return mUserName + " "
		    + Utils.context.getResources().getString(R.string.notification_accepted_friend_title);
	}

	@Override
	public String getUserName() {
		return mUserName;
	}

	@Override
	public long getUserId() {
		return mUserId;
	}

	@Override
	public void setStatus(int status) {
		// Status of an accepted friend invitation cannot be set
	}

}