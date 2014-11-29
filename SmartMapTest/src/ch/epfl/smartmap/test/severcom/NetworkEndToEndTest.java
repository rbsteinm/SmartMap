package ch.epfl.smartmap.test.severcom;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import android.content.Context;
import android.location.Location;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.PublicEvent;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.NotificationBag;
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
	private final static String SMARTMAP_SWENG_FB_ACCESS_TOKEN = "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";
	private static final long SMARTMAP_SWENG_ID = 3;
	private static final User SMARTMAP_SWENG = new Friend(SMARTMAP_SWENG_ID,
			SMARTMAP_SWENG_NAME);

	private final static long SMART_MAP_FACEBOOK_ID = 1395136807427991L;
	private final static String SMART_MAP_NAME = "Smart Map";
	private final static String SMART_MAP_FB_ACCESS_TOKEN = "CAAEWMqbRPIkBAPayty1578xCWRA4mHMTAVORgG8HNFKDJHoReb05eaVvRR59fGL2JsrBtLlKhgG7ZB0ZAtVut4OpiiwXZCBx1SCEhZAegiu6IqKX8SnJjyZAA1ZCqQP7ctt3q1hhYv78x9UNInmYYAPQ2SdepRxlalCaJbVdrlZAanM0TIZAZBjQIqzVb9uvjEG8uoSNB4RJ2X9psGtBmm9mn";
	private static final long SMART_MAP_ID = 11;
	private static final User SMART_MAP = new Friend(SMART_MAP_ID,
			SMART_MAP_NAME);

	private final static Location LOCATION = new Location("SmartMapServers");
	private static final double LATITUDE = 45;
	private static final double LONGITUDE = 46;
	private static final long VALID_ID_1 = 1;

	private static final Event FOOTBALL_TOURNAMENT = new PublicEvent(
			"Football Tournament", SMARTMAP_SWENG.getID(),
			SMARTMAP_SWENG.getName(), new GregorianCalendar(2014, 11, 23),
			new GregorianCalendar(2014, 11, 27), LOCATION,
			Arrays.asList((long) 3));

	private static final Event CONFERENCE = new PublicEvent("Conference",
			SMARTMAP_SWENG.getID(), SMART_MAP.getName(), new GregorianCalendar(
					2014, 12, 23), new GregorianCalendar(2014, 12, 25),
					LOCATION, Arrays.asList((long) 11));

	private static final long VALID_EVENT_ID_1 = 3;
	private static final long VALID_EVENT_ID_2 = 11;
	private Context mContext;
	private SmartMapClient networkClient;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		networkClient = NetworkSmartMapClient.getInstance();
		mContext = new RenamingDelegatingContext(this.getContext(), "test_");
		SettingsManager.initialize(mContext);
		LOCATION.setLatitude(LATITUDE);
		LOCATION.setLongitude(LONGITUDE);
		FOOTBALL_TOURNAMENT.setPositionName("Paris");
		FOOTBALL_TOURNAMENT.setID(VALID_EVENT_ID_1);
		CONFERENCE.setPositionName("Paris");
		CONFERENCE.setID(VALID_EVENT_ID_2);

		networkClient.authServer(SMARTMAP_SWENG_NAME,
				SMARTMAP_SWENG_FACEBOOK_ID, SMARTMAP_SWENG_FB_ACCESS_TOKEN);

	}

	@Test
	public void testAuthServer() throws SmartMapClientException {
		networkClient.authServer(SMARTMAP_SWENG_NAME,
				SMARTMAP_SWENG_FACEBOOK_ID, SMARTMAP_SWENG_FB_ACCESS_TOKEN);

	}

	@Test
	public void testUpdatePos() throws SmartMapClientException {
		networkClient.updatePos(LOCATION);
	}

	@Test
	public void testInviteFriend() throws SmartMapClientException {

		try {
			networkClient.inviteFriend(SMARTMAP_SWENG_ID);
		} catch (SmartMapClientException e) {
			// ok, cannot invite yourself
		}

	}

	@Test
	public void testFollowFriend() throws SmartMapClientException {

		networkClient.followFriend(SMARTMAP_SWENG_ID);
	}

	@Test
	public void testUnfollowFriend() throws SmartMapClientException {

		networkClient.unfollowFriend(SMARTMAP_SWENG_ID);
	}

	@Test
	public void testAllowFriend() throws SmartMapClientException {

		networkClient.allowFriend(SMARTMAP_SWENG_ID);
	}

	@Test
	public void testDisallowFriend() throws SmartMapClientException {

		networkClient.disallowFriend(SMARTMAP_SWENG_ID);
	}

	@Test
	public void testAllowFriendList() throws SmartMapClientException {

		networkClient.allowFriendList(Arrays.asList(VALID_ID_1, SMART_MAP_ID));
	}

	@Test
	public void testDisallowFriendList() throws SmartMapClientException {

		networkClient.disallowFriendList(Arrays
				.asList(VALID_ID_1, SMART_MAP_ID));
	}

	@Test
	public void testGetUserInfo() throws SmartMapClientException {

		User friend = networkClient.getUserInfo(VALID_ID_1);
		this.assertValidIdAndName(friend);

	}

	@Test
	public void testGetInvitations() throws SmartMapClientException {

		NotificationBag notificationBag = networkClient.getInvitations();
		List<User> inviters = notificationBag.getInvitingUsers();
		assertTrue("Null inviter list", inviters != null);
		List<User> newFriends = notificationBag.getNewFriends();
		assertTrue("Null new friends list", newFriends != null);
		List<Long> removedFriends = notificationBag.getRemovedFriendsIds();
		assertTrue("Null removed friends list", removedFriends != null);

		for (User user : inviters) {
			this.assertValidIdAndName(user);
		}
		for (User user : newFriends) {
			this.assertValidIdAndName(user);
		}
		for (long id : removedFriends) {
			assertTrue("Unexpected id", id >= 0);
		}

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
	public void testListFriendPos() throws SmartMapClientException {

		List<User> users = networkClient.listFriendsPos();

		assertTrue("Null list", users != null);

		for (User user : users) {
			Location location = user.getLocation();
			assertTrue("Invalid id", user.getID() > 0);
			assertTrue("Unexpected latitude", (-90 <= location.getLatitude())
					&& (location.getLatitude() <= 90));
			assertTrue(
					"Unexpected longitude",
					(-180 <= location.getLongitude())
					&& (location.getLongitude() <= 180));
		}
	}

	@Test
	public void testFindUsers() throws SmartMapClientException {

		List<User> friends = networkClient.findUsers("s");

		assertTrue("Null list", friends != null);
		for (User user : friends) {
			this.assertValidIdAndName(user);
		}
	}

	@Test
	public void testDeclineInvitation() throws SmartMapClientException {

		networkClient.declineInvitation(SMARTMAP_SWENG_ID);
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
	public void testAckAcceptedInvitation() throws SmartMapClientException {

		networkClient.ackAcceptedInvitation(SMARTMAP_SWENG_ID);
	}

	public void testGetFriendsIds() throws SmartMapClientException {

		List<Long> ids = networkClient.getFriendsIds();
		for (long id : ids) {
			assertTrue("Unexpected id", id >= 0);
		}
	}

	public void testAckRemovedFriends() throws SmartMapClientException {

		networkClient.ackRemovedFriend(SMARTMAP_SWENG_ID);
	}

	public void testGetProfilePicture() throws SmartMapClientException {

		networkClient.getProfilePicture(SMARTMAP_SWENG_ID);
	}

	public void testCreateEvent() throws SmartMapClientException {

		long eventId = networkClient.createPublicEvent(FOOTBALL_TOURNAMENT);
		assertTrue("Unexpected id", eventId >= 0);

		networkClient.authServer(SMART_MAP_NAME, SMART_MAP_FACEBOOK_ID,
				SMART_MAP_FB_ACCESS_TOKEN);
		long eventId2 = networkClient.createPublicEvent(CONFERENCE);
		assertTrue("Unexpected id", eventId2 >= 0);

	}

	public void testUpdateEvent() throws SmartMapClientException {

		FOOTBALL_TOURNAMENT.setName("Exposition");
		networkClient.updateEvent(FOOTBALL_TOURNAMENT);
	}

	public void testGetPublicEvents() throws SmartMapClientException {

		List<Event> events = networkClient.getPublicEvents(45, 46, 1000);
		for (Event event : events) {
			this.assertValidEvent(event);
		}
	}

	public void testJoinEvent() throws SmartMapClientException {
		networkClient.joinEvent(VALID_EVENT_ID_2);
	}

	public void testLeaveEvent() throws SmartMapClientException {
		networkClient.leaveEvent(VALID_EVENT_ID_2);
	}

	public void testInviteUsersToEvent() throws SmartMapClientException {
		networkClient.inviteUsersToEvent(VALID_EVENT_ID_1,
				Arrays.asList(VALID_ID_1, SMART_MAP_ID));
	}

	public void testGetEventInvitations() throws SmartMapClientException {
		List<Event> events = networkClient.getEventInvitations();
		for (Event event : events) {
			this.assertValidEvent(event);
		}
	}

	public void testAckEventInvitation() throws SmartMapClientException {
		networkClient.ackEventInvitation(VALID_EVENT_ID_2);
	}

	public void testGetEventInfo() throws SmartMapClientException {
		Event event = networkClient.getEventInfo(VALID_EVENT_ID_2);
		this.assertValidEvent(event);
	}

	private void assertValidIdAndName(User user) {
		assertTrue("Unexpected id", user.getID() >= 0);
		assertTrue("Unexpected name", (2 < user.getName().length())
				&& (user.getName().length() <= 60));
	}

	private void assertValidEvent(Event event) {
		assertTrue("Unexpected event id", event.getID() >= 0);
		assertTrue("Unexpected creator id", event.getCreator() >= 0);
		assertTrue("Unexpected end and start dates",
				event.getEndDate().after(event.getStartDate()));
		assertTrue("Unexpected latitude", (-90 <= event.getLocation()
				.getLatitude()) && (event.getLocation().getLatitude() <= 90));
		assertTrue("Unexpected longitude", (-180 <= event.getLocation()
				.getLongitude()) && (event.getLocation().getLongitude() <= 180));
		assertTrue("Unexpected position name", ((2 < event.getPositionName()
				.length()) && (event.getPositionName().length() <= 60)));
		assertTrue(
				"Unexpected event name",
				((2 < event.getName().length()) && (event.getName().length() <= 60)));
		assertTrue("Unexpected creator name", ((2 < event.getCreatorName()
				.length()) && (event.getCreatorName().length() <= 60)));
		assertTrue("Unexpected event description",
				(event.getName().length() <= 255));
		assertTrue("Unexpected participants list",
				event.getParticipants() != null);
		for (long id : event.getParticipants()) {
			assertTrue("Unexpected participants id", id >= 0);
		}

	}

}