package ch.epfl.smartmap.cache;

import android.content.Intent;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.gui.Utils;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class EventInvitation implements Invitation {
    private final ImmutableUser mUser;
    private final ImmutableEvent mEvent;
    private int mStatus;

    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";

    public EventInvitation(ImmutableInvitation invitation, ImmutableEvent event) {
        mUser = invitation.getUser();
        mEvent = event;
        mStatus = invitation.getStatus();
    }

    public ImmutableEvent getEvent() {
        return mEvent;
    }

    @Override
    public long getId() {
        return mEvent.getId();
    }

    @Override
    public Intent getIntent() {
        Intent intent = null;
        if (mStatus == READ) {
            intent = new Intent(Utils.sContext, ShowEventsActivity.class);
            intent.putExtra("invitation", true);
        } else if (mStatus == ACCEPTED) {
            // intent = new Intent(Utils.Context,
            // ShowEventInformationActivity.class);
        }
        return intent;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public String getText() {
        return Utils.sContext.getResources().getString(R.string.notification_open_event_invitation_list);
    }

    @Override
    public String getTitle() {
        return Utils.sContext.getResources().getString(R.string.notification_event_request_title) + " "
            + mUser.getName();
    }

    @Override
    public ImmutableUser getUser() {
        return mUser;
    }

    public void setId(long id) {
        mEvent.setId(id);
    }

    @Override
    public void setStatus(int newStatus) {
        mStatus = newStatus;

    }
}