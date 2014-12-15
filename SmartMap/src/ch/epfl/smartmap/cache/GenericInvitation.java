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
 * A class to represent invitations
 * 
 * @author agpmilli
 */
public class GenericInvitation extends Invitation {

    // We create the bitmaps once
    private static final Bitmap ADD_PERSON_BITMAP = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager()
        .getContext().getResources(), R.drawable.ic_action_add_person);

    private static final Bitmap ACCEPTED_FRIEND_BITMAP = BitmapFactory.decodeResource(ServiceContainer
        .getSettingsManager().getContext().getResources(), R.drawable.ic_accepted_friend_request);

    private static final Bitmap NEW_EVENT_BITMAP = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager()
        .getContext().getResources(), R.drawable.ic_action_event_request);

    private User mUser;
    private Event mEvent;
    private int mType;

    /**
     * Constructor
     * 
     * @param id
     *            id of invitation
     * @param timeStamp
     *            timeStamp of invitation
     * @param status
     *            status of invitation
     * @param user
     *            user of invitation
     * @param event
     *            event of invitation
     * @param type
     *            type of invitation
     */
    protected GenericInvitation(long id, long timeStamp, int status, User user, Event event, int type) {
        super(id, timeStamp, status);

        if ((type != Invitation.ACCEPTED_FRIEND_INVITATION)
            && ((type != Invitation.EVENT_INVITATION) && (type != Invitation.FRIEND_INVITATION))) {
            mType = Invitation.NO_TYPE;
        } else {
            mType = type;
        }

        if ((type == Invitation.FRIEND_INVITATION) || (type == Invitation.ACCEPTED_FRIEND_INVITATION)) {
            mUser = user;
        } else {
            mUser = Invitation.NO_USER;
        }

        if (type == Invitation.EVENT_INVITATION) {
            mEvent = event;
        } else {
            mEvent = Invitation.NO_EVENT;
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.InvitationInterface#getIntent()
     * It depends on the type of invitation and on the status of it
     */
    @Override
    public Intent getIntent() {
        Intent intent = null;
        Context context = ServiceContainer.getSettingsManager().getContext();
        // Depends on type of invitation
        if (mType == InvitationContainer.FRIEND_INVITATION) {
            // And on status of invitation
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
     * It depends on the status (if accepted or declined) and then on the type
     * of invitation
     */
    @Override
    public String getSubtitle() {
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (this.getStatus() == DECLINED) {
            return context.getResources().getString(R.string.invitation_declined);
        } else if (this.getStatus() == ACCEPTED) {
            return context.getResources().getString(R.string.invitation_accepted);
        } else if (mType == FRIEND_INVITATION) {
            return context.getResources().getString(R.string.invitation_click_here_to_open_your_list_of_invitations);
        } else if (mType == EVENT_INVITATION) {
            return context.getResources().getString(R.string.invitation_click_here_to_see_the_event);
        } else if (mType == ACCEPTED_FRIEND_INVITATION) {
            return context.getResources().getString(R.string.invitation_click_here_for_informations);
        } else {
            return "";
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.InvitationInterface#getTitle()
     * It depends on the type of invitation
     */
    @Override
    public String getTitle() {
        Context context = ServiceContainer.getSettingsManager().getContext();
        if (mType == InvitationContainer.FRIEND_INVITATION) {
            return mUser.getName() + " " + context.getResources().getString(R.string.invitation_want_to_be_your_friend);
        } else if (mType == InvitationContainer.EVENT_INVITATION) {
            return context.getResources().getString(R.string.invitation_invites_your_to) + " " + mEvent.getName();
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
        boolean hasChanged = super.update(invitation);
        if ((invitation.getUser() != null) && (invitation.getUser() != mUser)) {
            mUser = invitation.getUser();
            hasChanged = true;
        }
        if ((invitation.getEvent() != null) && (invitation.getEvent() != mEvent)) {
            mEvent = invitation.getEvent();
            hasChanged = true;
        }

        return hasChanged;
    }
}