package ch.epfl.smartmap.servercom;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import android.util.Log;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.util.Utils;

/**
 * @author jfperren
 */
public class NetworkEventInvitationBag implements InvitationBag {

    private static final String TAG = NetworkEventInvitationBag.class.getSimpleName();
    private final Set<InvitationContainer> invitations;

    /**
     * The constructor takes List arguments for compliance with server communication code.
     */
    public NetworkEventInvitationBag(HashSet<EventContainer> hashSet) {

        if (hashSet == null) {
            throw new IllegalArgumentException("invitingEvents is null");
        }

        invitations = new HashSet<InvitationContainer>();
        long timeStamp = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND)).getTimeInMillis();

        for (EventContainer event : hashSet) {
            invitations.add(new InvitationContainer(Invitation.NO_ID, null, event, Invitation.UNREAD, timeStamp,
                    Invitation.EVENT_INVITATION));
            Log.d(TAG,
                    "Network send invitation bag with event #" + event.getId() + "created by " + event.getImmCreator());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.smartmap.servercom.NotificationBag#getInvitingUsers()
     */
    @Override
    public Set<InvitationContainer> getInvitations() {
        return new HashSet<InvitationContainer>(invitations);
    }
}
