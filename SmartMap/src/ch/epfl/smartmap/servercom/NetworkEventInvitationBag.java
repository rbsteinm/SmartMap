package ch.epfl.smartmap.servercom;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import android.util.Log;
import ch.epfl.smartmap.cache.ImmutableEvent;
import ch.epfl.smartmap.cache.ImmutableInvitation;
import ch.epfl.smartmap.cache.Invitation;

/**
 * @author jfperren
 */
public class NetworkEventInvitationBag implements InvitationBag {

    private static final String TAG = NetworkEventInvitationBag.class.getSimpleName();
    private final Set<ImmutableInvitation> invitations;

    /**
     * The constructor takes List arguments for compliance with server
     * communication code.
     */
    public NetworkEventInvitationBag(HashSet<ImmutableEvent> hashSet) {

        if (hashSet == null) {
            throw new IllegalArgumentException("invitingEvents is null");
        }

        invitations = new HashSet<ImmutableInvitation>();
        long timeStamp = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+01:00")).getTimeInMillis();

        for (ImmutableEvent event : hashSet) {
            invitations.add(new ImmutableInvitation(Invitation.NO_ID, null, event, Invitation.UNREAD,
                timeStamp, Invitation.EVENT_INVITATION));
            Log.d(
                TAG,
                "Network send invitation bag with event #" + event.getId() + "created by "
                    + event.getImmCreator());
        }
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
     */
    @Override
    public Set<ImmutableInvitation> getInvitations() {
        return new HashSet<ImmutableInvitation>(invitations);
    }
}
