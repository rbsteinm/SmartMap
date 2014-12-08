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

    private static final String TAG = EventInvitation.class.getSimpleName();
    private static final int ID_MASK = 1;

    private final User mUser;
    private final Event mEvent;
    private int mStatus;

    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";

    public EventInvitation(ImmutableInvitation invitation) {
        assert invitation.getType() == ImmutableInvitation.EVENT_INVITATION;
        mUser = invitation.getUser();
        mEvent = invitation.getEvent();
        mStatus = invitation.getStatus();
    }

    @Override
    public boolean equals(Object that) {
        return ((that != null) && (that instanceof EventInvitation) && (this.getId() == ((EventInvitation) that)
            .getId()));
    }

    public Event getEvent() {
        return mEvent;
    }

    @Override
    public long getId() {
        return (mEvent.getId() << 2) | ID_MASK;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getImmutableCopy()
     */
    @Override
    public ImmutableInvitation getImmutableCopy() {
        // TODO Auto-generated method stub
        return null;
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getType()
     */
    @Override
    public int getType() {
        return Invitation.EVENT_INVITATION;
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