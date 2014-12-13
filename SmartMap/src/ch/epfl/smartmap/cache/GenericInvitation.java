package ch.epfl.smartmap.cache;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.EventInformationActivity;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * A class to represent the user's invitations
 * 
 * @author agpmilli
 */
public class GenericInvitation extends Invitation {

    @SuppressWarnings("unused")
    private static final String TAG = GenericInvitation.class.getSimpleName();

    private static final Bitmap ADD_PERSON_BITMAP = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_action_add_person);

    private static final Bitmap ACCEPTED_FRIEND_BITMAP = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_accepted_friend_request);

    private static final Bitmap NEW_EVENT_BITMAP = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_action_event_request);

    private User mUser;
    private Event mEvent;
    private int mType;

    public static final int DEFAULT_PICTURE = R.drawable.ic_default_user; // placeholder
    public static final int IMAGE_QUALITY = 100;
    public static final String PROVIDER_NAME = "SmartMapServers";

    public GenericInvitation(long id, long timeStamp, int status, User user, Event event, int type) {
        super(id, timeStamp, status);

        if ((user == null)
            && ((type == Invitation.FRIEND_INVITATION) || (type == Invitation.ACCEPTED_FRIEND_INVITATION))) {
            throw new IllegalArgumentException();
        } else {
            mUser = user;
        }

        if ((type == Invitation.EVENT_INVITATION) && (event == null)) {
            throw new IllegalArgumentException();
        } else {
            mEvent = event;
        }

        if ((type != Invitation.ACCEPTED_FRIEND_INVITATION)
            && ((type != Invitation.EVENT_INVITATION) && (type != Invitation.FRIEND_INVITATION))) {
            throw new IllegalArgumentException();
        } else {
            mType = type;
        }

    }

    @Override
    public InvitationContainer getContainerCopy() {
        return super.getContainerCopy().setUser(mUser).setEvent(mEvent).setType(mType);
    }

    @Override
    public Event getEvent() {
        return mEvent;
    }

    @Override
    public Bitmap getImage() {
        if (this.getType() == Invitation.ACCEPTED_FRIEND_INVITATION) {
            return ACCEPTED_FRIEND_BITMAP;
        } else if (this.getType() == Invitation.FRIEND_INVITATION) {
            return ADD_PERSON_BITMAP;
        } else {
            return NEW_EVENT_BITMAP;
        }
    }

    @Override
    public Intent getIntent() {
        Intent intent = null;
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (mType == InvitationContainer.FRIEND_INVITATION) {
            if ((this.getStatus() == READ) || (this.getStatus() == UNREAD)) {
                intent = new Intent(context, FriendsPagerActivity.class);
            } else if ((this.getStatus() == ACCEPTED) || (this.getStatus() == DECLINED)) {
                intent = new Intent(context, UserInformationActivity.class);
                intent.putExtra("USER", mUser.getId());
            }
        } else if (mType == InvitationContainer.EVENT_INVITATION) {
            intent = new Intent(context, EventInformationActivity.class);
            intent.putExtra("EVENT", mEvent.getId());
        } else {
            intent = new Intent(context, UserInformationActivity.class);
            intent.putExtra("USER", mUser.getId());
        }
        intent.putExtra("INVITATION", true);
        return intent;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#getSubtitle()
     */
    @Override
    public String getSubtitle() {
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (this.getStatus() == DECLINED) {
            return context.getResources().getString(R.string.invitation_declined);
        } else if (this.getStatus() == ACCEPTED) {
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
    public String getTitle() {
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (mType == InvitationContainer.FRIEND_INVITATION) {
            return mUser.getName() + " "
                + context.getResources().getString(R.string.invitation_want_to_be_your_friend);
        } else if (mType == InvitationContainer.EVENT_INVITATION) {
            return mEvent.getCreator().getName() + " "
                + context.getResources().getString(R.string.invitation_invites_your_to) + " "
                + mEvent.getName();
        } else if (mType == InvitationContainer.ACCEPTED_FRIEND_INVITATION) {
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.Invitation#update(ch.epfl.smartmap.cache.
     * ImmutableInvitation)
     */
    @Override
    public boolean update(InvitationContainer invitation) {
        boolean hasChanged = false;

        if (invitation.getUser() != null) {
            mUser = invitation.getUser();
            hasChanged = true;
        }
        if (invitation.getEvent() != null) {
            mEvent = invitation.getEvent();
            hasChanged = true;
        }

        if ((invitation.getType() == InvitationContainer.ACCEPTED_FRIEND_INVITATION)
            || (invitation.getType() == InvitationContainer.EVENT_INVITATION)
            || (invitation.getType() == InvitationContainer.FRIEND_INVITATION)) {
            mType = invitation.getType();
            hasChanged = true;
        }

        return super.update(invitation) || hasChanged;
    }
}