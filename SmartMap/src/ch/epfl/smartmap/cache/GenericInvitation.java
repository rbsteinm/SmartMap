package ch.epfl.smartmap.cache;

import android.content.Intent;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.EventInformationActivity;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.gui.Utils;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class GenericInvitation implements Invitation {

    @SuppressWarnings("unused")
    private static final String TAG = GenericInvitation.class.getSimpleName();

    private User mUser;
    private Event mEvent;
    private int mStatus;
    private final long mId;
    private long mTimeStamp;
    private int mType;

    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";

    public GenericInvitation(ImmutableInvitation invitation) {
        if (invitation.getUser() == null) {
            throw new IllegalArgumentException();
        } else {
            mUser = invitation.getUser();
        }
        if (invitation.getId() < 0) {
            throw new IllegalArgumentException();
        } else {
            mId = invitation.getId();
        }
        if (invitation.getEvent() == null) {
            throw new IllegalArgumentException();
        } else {
            mEvent = invitation.getEvent();
        }
        if ((invitation.getStatus() != Invitation.UNREAD)
            || ((invitation.getStatus() != Invitation.READ) || (invitation.getStatus() != Invitation.DECLINED) || (invitation
                .getStatus() != Invitation.ACCEPTED))) {
            throw new IllegalArgumentException();
        } else {
            mStatus = invitation.getStatus();
        }
        if ((invitation.getType() != 0) || ((invitation.getType() != 1) || (invitation.getType() != 2))) {
            throw new IllegalArgumentException();
        } else {
            mType = invitation.getType();
        }

        if (invitation.getTimeStamp() < 0) {
            throw new IllegalArgumentException();
        } else {
            mTimeStamp = invitation.getTimeStamp();
        }

    }

    @Override
    public boolean equals(Object that) {
        return ((that != null) && (that instanceof GenericInvitation) && (this.getId() == ((GenericInvitation) that)
            .getId()));
    }

    public Event getEvent() {
        return mEvent;
    }

    @Override
    public long getId() {
        return mId;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getImmutableCopy()
     */
    @Override
    public ImmutableInvitation getImmutableCopy() {
        return new ImmutableInvitation(mId, mUser.getId(), mEvent.getId(), mStatus, mTimeStamp,
            ImmutableInvitation.EVENT_INVITATION);
    }

    @Override
    public Intent getIntent() {
        Intent intent = null;
        if (mType == ImmutableInvitation.FRIEND_INVITATION) {
            if ((mStatus == READ) || (mStatus == UNREAD)) {
                intent = new Intent(Utils.sContext, FriendsPagerActivity.class);
                intent.putExtra("INVITATION", true);
            } else if (mStatus == ACCEPTED) {
                intent = new Intent(Utils.sContext, UserInformationActivity.class);
                intent.putExtra("USER", mUser.getId());
            }
        } else if (mType == ImmutableInvitation.EVENT_INVITATION) {
            if (mStatus == READ) {
                intent = new Intent(Utils.sContext, ShowEventsActivity.class);
                intent.putExtra("invitation", true);
            } else if (mStatus == ACCEPTED) {
                intent = new Intent(Utils.sContext, EventInformationActivity.class);
                intent.putExtra("EVENT", mEvent.getId());
            }
        } else {
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
        if (mType == ImmutableInvitation.FRIEND_INVITATION) {
            return Utils.sContext.getResources().getString(R.string.notification_open_friend_list);
        } else if (mType == ImmutableInvitation.EVENT_INVITATION) {
            return Utils.sContext.getResources().getString(R.string.notification_open_event_invitation_list);
        } else {
            return Utils.sContext.getResources().getString(R.string.notification_open_friend_info1) + " "
                + mUser.getName() + " "
                + Utils.sContext.getResources().getString(R.string.notification_open_friend_info2);
        }
    }

    @Override
    public String getTitle() {
        if (mType == ImmutableInvitation.FRIEND_INVITATION) {
            return Utils.sContext.getResources().getString(R.string.notification_open_friend_list) + " "
                + mUser.getName();
        } else if (mType == ImmutableInvitation.EVENT_INVITATION) {
            return Utils.sContext.getResources().getString(R.string.notification_event_request_title) + " "
                + mUser.getName();
        } else {
            return mUser.getName() + " "
                + Utils.sContext.getResources().getString(R.string.notification_accepted_friend_title);
        }
    }

    @Override
    public int getType() {
        return mType;
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
     * @see ch.epfl.smartmap.cache.Invitation#update(ch.epfl.smartmap.cache.
     * ImmutableInvitation)
     */
    @Override
    public void update(ImmutableInvitation invitation) {
        if (invitation.getUser() != null) {
            mUser = invitation.getUser();
        }
        if (invitation.getEvent() != null) {
            mEvent = invitation.getEvent();
        }
        if ((invitation.getStatus() == Invitation.ACCEPTED) || (invitation.getStatus() == Invitation.DECLINED)
            || (invitation.getStatus() == Invitation.READ) || (invitation.getStatus() == Invitation.UNREAD)) {
            mStatus = invitation.getStatus();
        }
        if (invitation.getTimeStamp() >= 0) {
            mTimeStamp = invitation.getTimeStamp();
        }
        if ((invitation.getType() == ImmutableInvitation.ACCEPTED_FRIEND_INVITATION)
            || (invitation.getType() == ImmutableInvitation.EVENT_INVITATION)
            || (invitation.getType() == ImmutableInvitation.FRIEND_INVITATION)) {
            mType = invitation.getType();
        }
    }
}