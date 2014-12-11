package ch.epfl.smartmap.cache;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.EventInformationActivity;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class GenericInvitation implements Invitation, Comparable {

    @SuppressWarnings("unused")
    private static final String TAG = GenericInvitation.class.getSimpleName();

    private static final Bitmap ADD_PERSON_BITMAP = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_action_add_person);

    private static final Bitmap ACCEPTED_FRIEND = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_accepted_friend_request);

    private static final Bitmap NEW_EVENT = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_action_event_request);

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
        if ((invitation.getUser() == null)
            && ((invitation.getType() == Invitation.FRIEND_INVITATION) || (invitation.getType() == Invitation.ACCEPTED_FRIEND_INVITATION))) {
            throw new IllegalArgumentException();
        } else {
            mUser = invitation.getUser();
        }
        if (invitation.getId() < 0) {
            throw new IllegalArgumentException();
        } else {
            mId = invitation.getId();
        }
        if ((invitation.getType() == Invitation.EVENT_INVITATION) && (invitation.getEvent() == null)) {
            throw new IllegalArgumentException();
        } else {
            mEvent = invitation.getEvent();
        }
        if ((invitation.getStatus() != Invitation.UNREAD)
            && ((invitation.getStatus() != Invitation.READ)
                && (invitation.getStatus() != Invitation.DECLINED) && (invitation.getStatus() != Invitation.ACCEPTED))) {
            throw new IllegalArgumentException("Status is " + invitation.getStatus());
        } else {
            mStatus = invitation.getStatus();
        }
        if ((invitation.getType() != Invitation.ACCEPTED_FRIEND_INVITATION)
            && ((invitation.getType() != Invitation.EVENT_INVITATION) && (invitation.getType() != Invitation.FRIEND_INVITATION))) {
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
    public int compareTo(Object that) {
        return Long.valueOf(this.getId()).compareTo(Long.valueOf(((Invitation) that).getId()));
    }

    @Override
    public boolean equals(Object that) {
        return (that != null) && (that instanceof GenericInvitation)
            && (this.getId() == ((GenericInvitation) that).getId());
    }

    @Override
    public Event getEvent() {
        return mEvent;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public Bitmap getImage() {
        if (this.getType() == Invitation.ACCEPTED_FRIEND_INVITATION) {
            return ACCEPTED_FRIEND;
        } else if (this.getType() == Invitation.FRIEND_INVITATION) {
            return ADD_PERSON_BITMAP;
        } else {
            return NEW_EVENT;
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getImmutableCopy()
     */
    @Override
    public ImmutableInvitation getImmutableCopy() {
        ImmutableUser immUser = (mUser != null) ? mUser.getImmutableCopy() : null;
        ImmutableEvent immEvent = (mEvent != null) ? mEvent.getImmutableCopy() : null;

        return new ImmutableInvitation(this.getId(), immUser, immEvent, this.getStatus(),
            this.getTimeStamp(), this.getType());
    }

    @Override
    public Intent getIntent() {
        Intent intent = null;
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (mType == ImmutableInvitation.FRIEND_INVITATION) {
            if ((mStatus == READ) || (mStatus == UNREAD)) {
                intent = new Intent(context, FriendsPagerActivity.class);
                intent.putExtra("INVITATION", true);
            } else if (mStatus == ACCEPTED) {
                intent = new Intent(context, UserInformationActivity.class);
                intent.putExtra("USER", mUser.getId());
            }
        } else if (mType == ImmutableInvitation.EVENT_INVITATION) {
            if (mStatus == READ) {
                intent = new Intent(context, ShowEventsActivity.class);
                intent.putExtra("invitation", true);
            } else if (mStatus == ACCEPTED) {
                intent = new Intent(context, EventInformationActivity.class);
                intent.putExtra("EVENT", mEvent.getId());
            }
        } else {
            intent = new Intent(context, UserInformationActivity.class);
            intent.putExtra("USER", mUser.getId());
        }
        return intent;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (mStatus == DECLINED) {
            return context.getResources().getString(R.string.invitation_declined);
        } else if (mStatus == ACCEPTED) {
            return context.getResources().getString(R.string.invitation_accepted);
        } else if (mType == FRIEND_INVITATION) {
            return context.getResources().getString(
                R.string.invitation_click_here_to_open_your_list_of_invitations);
        } else if (mType == EVENT_INVITATION) {
            return context.getResources().getString(R.string.invitation_click_here_to_see_the_event);
        } else if (mType == ACCEPTED_FRIEND_INVITATION) {
            return context.getResources().getString(R.string.invitation_click_here_for_informations);
        } else {
            return "";
        }
    }

    @Override
    public long getTimeStamp() {
        return mTimeStamp;
    }

    @Override
    public String getTitle() {
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (mType == ImmutableInvitation.FRIEND_INVITATION) {
            return mUser.getName() + " "
                + context.getResources().getString(R.string.invitation_want_to_be_your_friend);
        } else if (mType == ImmutableInvitation.EVENT_INVITATION) {
            return mEvent.getCreator().getName() + " "
                + context.getResources().getString(R.string.invitation_invites_your_to) + " "
                + mEvent.getName();
        } else if (mType == ImmutableInvitation.ACCEPTED_FRIEND_INVITATION) {
            return mUser.getName() + " "
                + context.getResources().getString(R.string.invitation_accepted_your_friend_request);
        } else {
            return "";
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
    public boolean update(ImmutableInvitation invitation) {
        // TODO : Update hasChanged to work correctly
        boolean hasChanged = false;

        if (invitation.getUser() != null) {
            mUser = invitation.getUser();
        }
        if (invitation.getEvent() != null) {
            mEvent = invitation.getEvent();
        }
        if ((invitation.getStatus() == Invitation.ACCEPTED)
            || (invitation.getStatus() == Invitation.DECLINED) || (invitation.getStatus() == Invitation.READ)
            || (invitation.getStatus() == Invitation.UNREAD)) {
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

        return true;
    }
}