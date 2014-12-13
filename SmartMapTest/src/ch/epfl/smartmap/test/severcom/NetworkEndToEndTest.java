package ch.epfl.smartmap.test.severcom;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import android.content.Context;
import android.location.Location;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.servercom.InvitationBag;
import ch.epfl.smartmap.servercom.ServerFeedbackException;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * Tests whether we can interact with the real SmartMap server.
 * 
 * @author marion-S
 */

public class NetworkEndToEndTest extends AndroidTestCase {

    private final static long SMARTMAP_SWENG_FACEBOOK_ID = 1482245642055847L;
    private final static String SMARTMAP_SWENG_NAME = "SmartMap SwEng";
    private final static String SMARTMAP_SWENG_FB_ACCESS_TOKEN =
        "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";
    private static final long SMARTMAP_SWENG_ID = 3;
    /*
     * private static final ImmutableUser SMARTMAP_SWENG = new ImmutableUser(
     * SMARTMAP_SWENG_ID, SMARTMAP_SWENG_NAME, null, null, null, null,
     * null, false);
     */

    private final static long SMART_MAP_FACEBOOK_ID = 1395136807427991L;
    private final static String SMART_MAP_NAME = "Smart Map";
    private final static String SMART_MAP_FB_ACCESS_TOKEN =

        "CAAEWMqbRPIkBAPayty1578xCWRA4mHMTAVORgG8HNFKDJHoReb05eaVvRR59fGL2JsrBtLlKhgG7ZB0ZAtVut4OpiiwXZCBx1SCEhZAegiu6IqKX8SnJjyZAA1ZCqQP7ctt3q1hhYv78x9UNInmYYAPQ2SdepRxlalCaJbVdrlZAanM0TIZAZBjQIqzVb9uvjEG8uoSNB4RJ2X9psGtBmm9mn";
    private static final long SMART_MAP_ID = 11;
    /*
     * private static final ImmutableUser SMART_MAP = new ImmutableUser(
     * SMART_MAP_ID, SMART_MAP_NAME, null, null, null,null,null,false);
     */

    private final static Location LOCATION = new Location("SmartMapServers");
    private static final double LATITUDE = 45;
    private static final double LONGITUDE = 46;
    private static final long VALID_ID_1 = 1;

    private static long CREATED_EVENT_ID;

    /*
     * private static final ImmutableEvent FOOTBALL_TOURNAMENT = new
     * ImmutableEvent(
     * 0, "Football Tournament", SMARTMAP_SWENG.getId(),
     * "Not a basketball tournament !",
     * new GregorianCalendar(2014, 11, 23), new GregorianCalendar(2014,
     * 11, 27), LOCATION, "Stade de la Pontaise",
     * new HashSet<Long>( Arrays.asList((long) 3)));
     */

    private static final long VALID_EVENT_ID_1 = 95;
    private static final long VALID_EVENT_ID_2 = 96;
    private Context mContext;
    private SmartMapClient networkClient;

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        mContext = new RenamingDelegatingContext(this.getContext(), "test_");
        ServiceContainer.initSmartMapServices(mContext);
        networkClient = ServiceContainer.getNetworkClient();

        // SettingsManager.initialize(mContext);
        LOCATION.setLatitude(LATITUDE);
        LOCATION.setLongitude(LONGITUDE);

        networkClient.authServer(SMARTMAP_SWENG_NAME, SMARTMAP_SWENG_FACEBOOK_ID,
            SMARTMAP_SWENG_FB_ACCESS_TOKEN);

        // Utils.sContext = this.getContext();
    }

    @Test
    public void testAcceptInvitation() throws SmartMapClientException {

        try {
            networkClient.acceptInvitation(VALID_ID_1);
        } catch (SmartMapClientException e) {
            // ok because not invited by anyone
        }

    }

    @Test
    public void testAckAcceptedInvitation() throws SmartMapClientException {

        networkClient.ackAcceptedInvitation(SMARTMAP_SWENG_ID);
    }

    public void testAckEventInvitation() throws SmartMapClientException {
        networkClient.ackEventInvitation(VALID_EVENT_ID_2);
    }

    public void testAckRemovedFriends() throws SmartMapClientException {

        networkClient.ackRemovedFriend(SMARTMAP_SWENG_ID);
    }

    @Test
    public void testAuthServer() throws SmartMapClientException {
        networkClient.authServer(SMARTMAP_SWENG_NAME, SMARTMAP_SWENG_FACEBOOK_ID,
            SMARTMAP_SWENG_FB_ACCESS_TOKEN);

    }

    public void testCreateEvent() throws SmartMapClientException {

        /*
         * long eventId = networkClient.createPublicEvent(FOOTBALL_TOURNAMENT);
         * assertTrue("Unexpected event id.", eventId >= 0);
         * CREATED_EVENT_ID = eventId;
         */

    }

    @Test
    public void testDeclineInvitation() throws SmartMapClientException {

        networkClient.declineInvitation(SMARTMAP_SWENG_ID);
    }

    @Test
    public void testFindUsers() throws SmartMapClientException {

        List<UserContainer> friends = networkClient.findUsers("s");

        assertTrue("Null list", friends != null);
        for (UserContainer user : friends) {
            this.assertValidIdAndName(user);
        }
    }

    // Follow friend is no longer supported by the server.

    public void testGetEventInfo() throws SmartMapClientException {
        EventContainer event = networkClient.getEventInfo(CREATED_EVENT_ID);
        this.assertValidEvent(event);
    }

    // public void testGetEventInvitations() throws SmartMapClientException {
    // List<Long> events = networkClient.getEventInvitations();
    // for (Long eventId : events) {
    // assertTrue("Ivalid event Id.", eventId > 0);
    // }
    // }

    public void testGetFriendsIds() throws SmartMapClientException {

        List<Long> ids = networkClient.getFriendsIds();
        for (long id : ids) {
            assertTrue("Unexpected id", id >= 0);
        }
    }

    @Test
    public void testGetInvitations() throws SmartMapClientException {

        InvitationBag invitationBag = networkClient.getInvitations();
        Set<InvitationContainer> invitations = invitationBag.getInvitations();
        assertTrue("Null invitations set", invitations != null);
        for (InvitationContainer invitation : invitations) {
            this.assertValidInvitation(invitation);
        }

    }

    public void testGetProfilePicture() throws SmartMapClientException {

        networkClient.getProfilePicture(SMARTMAP_SWENG_ID);
    }

    public void testGetPublicEvents() throws SmartMapClientException {

        List<Long> events = networkClient.getPublicEvents(45, 46, 1000);
        for (Long eventId : events) {
            assertTrue("Ivalid event Id.", eventId > 0);
        }
    }

    @Test
    public void testGetUserInfo() throws SmartMapClientException {

        UserContainer friend = networkClient.getUserInfo(VALID_ID_1);
        this.assertValidIdAndName(friend);

    }

    @Test
    public void testInviteFriend() throws SmartMapClientException {

        try {
            networkClient.inviteFriend(SMARTMAP_SWENG_ID);
        } catch (SmartMapClientException e) {
            // ok, cannot invite yourself
        }

    }

    public void testInviteUsersToEvent() throws SmartMapClientException {
        networkClient.inviteUsersToEvent(CREATED_EVENT_ID, Arrays.asList(VALID_ID_1, SMART_MAP_ID));
    }

    public void testJoinEvent() throws SmartMapClientException {
        networkClient.joinEvent(CREATED_EVENT_ID);
    }

    public void testLeaveEvent() throws SmartMapClientException {
        networkClient.leaveEvent(VALID_EVENT_ID_2);
    }

    @Test
    public void testListFriendPos() throws SmartMapClientException {

        List<UserContainer> users = networkClient.listFriendsPos();

        assertTrue("Null list", users != null);

        for (UserContainer user : users) {

            assertTrue("Invalid id", user.getId() > 0);
            this.assertValidLocation(user.getLocation());
        }
    }

    @Test
    public void testRemoveFriend() throws SmartMapClientException {

        try {
            networkClient.removeFriend(SMARTMAP_SWENG_ID);
        } catch (ServerFeedbackException e) {
            // ok because I cannot remove myself
        }
    }

    @Test
    public void testUpdateEventName() throws SmartMapClientException {

        /*
         * ImmutableEvent update = new ImmutableEvent(CREATED_EVENT_ID, "Toto",
         * SMARTMAP_SWENG.getId(), "Not a basketball tournament !",
         * new GregorianCalendar(2014, 11, 23), new GregorianCalendar(
         * 2014, 11, 27), LOCATION, "Stade de la Pontaise",
         * new HashSet<Long>(Arrays.asList((long) 3)));
         * networkClient.updateEvent(update);
         */

        EventContainer modifiedEvent = networkClient.getEventInfo(CREATED_EVENT_ID);
        assertEquals("Updated event name does not match", modifiedEvent.getName(), "Toto");

    }

    @Test
    public void testUpdatePos() throws SmartMapClientException {
        networkClient.updatePos(LOCATION);
    }

    private void assertValidEvent(EventContainer event) {
        assertTrue("Unexpected event id", event.getId() >= 0);
        assertTrue("Unexpected creator id", event.getCreatorId() >= 0);
        assertTrue("Unexpected end and start dates", event.getEndDate().after(event.getStartDate()));
        this.assertValidLocation(event.getLocation());
        assertTrue("Unexpected position name", ((2 < event.getLocationString().length()) && (event
            .getLocationString().length() <= 60)));
        assertTrue("Unexpected event name",
            ((2 < event.getName().length()) && (event.getName().length() <= 60)));
        assertTrue("Unexpected creator id.", event.getCreatorId() > 0);
        assertTrue("Unexpected event description", (event.getName().length() <= 255));
        assertTrue("Unexpected participants list", event.getParticipantIds() != null);
        for (long id : event.getParticipantIds()) {
            assertTrue("Unexpected participants id", id >= 0);
        }
    }

    private void assertValidIdAndName(UserContainer user) {
        assertTrue("Unexpected id", user.getId() >= 0);
        assertTrue("Unexpected name", (2 < user.getName().length()) && (user.getName().length() <= 60));
    }

    private void assertValidInvitation(InvitationContainer invitation) {
        assertNotNull("Null TimeStamp", invitation.getTimeStamp());
        assertTrue("Unexpected invitation type",
            (invitation.getType() == Invitation.ACCEPTED_FRIEND_INVITATION)
                || (invitation.getType() == Invitation.FRIEND_INVITATION));
        this.assertValidIdAndName(invitation.getUserInfos());
        if (invitation.getType() == Invitation.ACCEPTED_FRIEND_INVITATION) {
            this.assertValidLocation(invitation.getUserInfos().getLocation());
        }

    }

    private void assertValidLocation(Location location) {
        assertNotNull(location);
        assertTrue("Unexpected latitude", (-90 <= location.getLatitude()) && (location.getLatitude() <= 90));
        assertTrue("Unexpected longitude", (-180 <= location.getLongitude())
            && (location.getLongitude() <= 180));
    }
}