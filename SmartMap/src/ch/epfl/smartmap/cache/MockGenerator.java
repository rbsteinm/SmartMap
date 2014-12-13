package ch.epfl.smartmap.cache;

import android.location.Location;
import ch.epfl.smartmap.cache.User.BlockStatus;

/**
 * Create mock objects for test
 * 
 * @author agpmilli
 */
public class MockGenerator {
    public static Event getEvent() {
        return new PublicEvent(0, null, null, null, null, null, null, null, null);
    }

    public static User getFriend() {
        return new Friend(1, "Mock Friend", null, new Location("Provider"), "Not here", BlockStatus.UNBLOCKED);
    }

    public static Invitation getInvitation(int type) {
        if (type == Invitation.EVENT_INVITATION) {
            return new GenericInvitation(0, 1, Invitation.UNREAD, null, getEvent(), type);
        } else if (type == Invitation.FRIEND_INVITATION) {
            return new GenericInvitation(1, 2, Invitation.UNREAD, getStranger(), null, type);
        } else if (type == Invitation.ACCEPTED_FRIEND_INVITATION) {
            return new GenericInvitation(2, 3, Invitation.UNREAD, getFriend(), null, type);
        } else {
            return null;
        }
    }

    public static User getStranger() {
        return new Stranger(2, "Mock Stranger", null);
    }
}
