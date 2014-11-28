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
	private final long mInvitationId;
	private final long mUserId;
	private final String mUserName;
	private int mStatus;

	public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
	public static final int IMAGE_QUALITY = 100;
	public static final String PROVIDER_NAME = "SmartMapServers";

	public FriendInvitation(long invitationId, long userId, String userName, int status) {
		mInvitationId = invitationId;
		mUserId = userId;
		mUserName = userName;
		mStatus = status;

	}

	@Override
	public int getStatus() {
		return mStatus;
	}

	@Override
	public Intent getIntent() {
		Intent intent = null;
		if ((mStatus == READ) || (mStatus == UNREAD)) {
			intent = new Intent(Utils.context, FriendsPagerActivity.class);
			intent.putExtra("INVITATION", true);
		} else if (mStatus == ACCEPTED) {
			intent = new Intent(Utils.context, UserInformationActivity.class);
			intent.putExtra("USER", mUserId);
		}
		return intent;
	}

	@Override
	public long getID() {
		return mInvitationId;
	}

	@Override
	public String getText() {
		return "Click here to open the invitation";
	}

	@Override
	public String getTitle() {
		return "Friend request from " + mUserName;
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
		mStatus = status;
	}

}