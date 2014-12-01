package ch.epfl.smartmap.cache;

import android.content.Intent;
import android.graphics.Bitmap;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.gui.Utils;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class EventInvitation implements Invitation {
	private final long mInvitationId;
	private final long mUserId;
	private final long mEventId;
	private final String mUserName;
	private final String mEventName;
	private int mStatus;

	public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
	public static final int IMAGE_QUALITY = 100;
	public static final String PROVIDER_NAME = "SmartMapServers";

	public EventInvitation(long invitationId, long userId, String userName, long eventId, String eventName,
	    int status) {
		mInvitationId = invitationId;
		mUserId = userId;
		mEventId = eventId;
		mUserName = userName;
		mEventName = eventName;
		mStatus = status;
	}

	@Override
	public int getStatus() {
		return mStatus;
	}

	@Override
	public Intent getIntent() {
		Intent intent = null;
		if (mStatus == READ) {
			intent = new Intent(Utils.sContext, ShowEventsActivity.class);
			intent.putExtra("invitation", true);
		} else if (mStatus == ACCEPTED) {
			// intent = new Intent(Utils.Context, ShowEventInformationActivity.class);
		}
		return intent;
	}

	@Override
	public long getId() {
		return mInvitationId;
	}

	@Override
	public String getText() {
		return Utils.sContext.getResources().getString(R.string.notification_open_event_invitation_list);
	}

	@Override
	public String getTitle() {
		return Utils.sContext.getResources().getString(R.string.notification_event_request_title) + " "
		    + mUserName;
	}

	@Override
	public String getUserName() {
		return mUserName;
	}

	@Override
	public long getUserId() {
		return mUserId;
	}

	public long getEventId() {
		return mEventId;
	}

	public String getEventName() {
		return mEventName;
	}

	@Override
	public Bitmap getImage() {
		return null;
	}

	@Override
	public void setStatus(int newStatus) {
		mStatus = newStatus;

	}
}