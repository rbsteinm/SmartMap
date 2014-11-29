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

// import org.junit.FixMethodOrder;
// import org.junit.runners.MethodSorters;

/**
 * Tests whether we can interact with the real quiz server.
 * 
 * @author marion-S
 */

// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NetworkEndToEndTest extends AndroidTestCase {

	private final static long VALID_FACEBOOK_ID = 1482245642055847L;
	private final static String VALID_NAME = "SmartMap SwEng";
	private final static String VALID_FB_ACCESS_TOKEN =
			"CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";
	private final static Location LOCATION = new Location("SmartMapServers");
	private static final double LATITUDE = 45;
	private static final double LONGITUDE = 46;
	private static final long VALID_ID_1 = 1;
	private static final long VALID_ID_2 = 2;
	private static final long MY_ID = 3;
	private static final User VALID_PEOPLE = new Friend(MY_ID, VALID_NAME);
	private static final Event FOOTBALL_TOURNAMENT = new PublicEvent("Football Tournament",
			VALID_PEOPLE.getID(), VALID_PEOPLE.getName(), new GregorianCalendar(2014, 11, 23),
			new GregorianCalendar(2014, 11, 27), LOCATION);
	private static final long VALID_EVENT_ID = 3;
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
		FOOTBALL_TOURNAMENT.setID(VALID_EVENT_ID);

	}

	@Test
	public void testA_AuthServer() throws SmartMapClientException {
		networkClient.authServer(VALID_NAME, VALID_FACEBOOK_ID, VALID_FB_ACCESS_TOKEN);

	}

	@Test
	public void testB_UpdatePos() throws SmartMapClientException {
		networkClient.updatePos(LOCATION);
	}

	@Test
	public void testC_InviteFriend() throws SmartMapClientException {

		try {
			networkClient.inviteFriend(VALID_ID_1);
		} catch (SmartMapClientException e) {
			// ok, cannot invite yourself
		}

	}

	@Test
	public void testD_FollowFriend() throws SmartMapClientException {

		networkClient.followFriend(MY_ID);
	}

	@Test
	public void testE_UnfollowFriend() throws SmartMapClientException {

		networkClient.unfollowFriend(MY_ID);
	}

	@Test
	public void ignoredtestF_AllowFriend() throws SmartMapClientException {

		networkClient.allowFriend(MY_ID);
	}

	@Test
	public void testG_DisallowFriend() throws SmartMapClientException {

		networkClient.disallowFriend(MY_ID);
	}

	@Test
	public void ignoredtestH_AllowFriendList() throws SmartMapClientException {

		networkClient.allowFriendList(Arrays.asList(VALID_ID_1, VALID_ID_2, MY_ID));
	}

	@Test
	public void testI_DisallowFriendList() throws SmartMapClientException {

		networkClient.disallowFriendList(Arrays.asList(VALID_ID_1, VALID_ID_2, MY_ID));
	}

	@Test
	public void testJ_GetUserInfo() throws SmartMapClientException {

		User friend = networkClient.getUserInfo(VALID_ID_1);
		this.assertValidIdAndName(friend);

	}

	@Test
	public void testK_GetInvitations() throws SmartMapClientException {

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

	// FIXME should simulate an invitation on server side??
	@Test
	public void testQ_AcceptInvitation() throws SmartMapClientException {


		try {
			networkClient.acceptInvitation(VALID_ID_1);
		} catch (SmartMapClientException e) {
			// ok because not invited by anyone
		}

	}

	@Test
	public void testL_ListFriendPos() throws SmartMapClientException {

		List<User> users = networkClient.listFriendsPos();

		assertTrue("Null list", users != null);

		for (User user : users) {
			Location location = user.getLocation();
			assertTrue("Invalid id", user.getID() > 0);
			assertTrue("Unexpected latitude", (-90 <= location.getLatitude())
					&& (location.getLatitude() <= 90));
			assertTrue("Unexpected longitude", (-180 <= location.getLongitude())
					&& (location.getLongitude() <= 180));
		}
	}

	@Test
	public void testM_FindUsers() throws SmartMapClientException {

		List<User> friends = networkClient.findUsers("s");

		assertTrue("Null list", friends != null);
		for (User user : friends) {
			this.assertValidIdAndName(user);
		}
	}

	// FIXME normal that no error whereas no invitation to decline?
	@Test
	public void testN_declineInvitation() throws SmartMapClientException {

		networkClient.declineInvitation(MY_ID);
	}

	@Test
	public void testO_removeFriend() throws SmartMapClientException {

		try {
			networkClient.removeFriend(MY_ID);
		} catch (ServerFeedbackException e) {
			// ok because I cannot remove myself
		}
	}

	// FIXME normal that no error whereas no accepted invitation to ack?
	@Test
	public void testP_AckAcceptedInvitation() throws SmartMapClientException {

		networkClient.ackAcceptedInvitation(MY_ID);
	}

	public void testQ_GetFriendsIds() throws SmartMapClientException {

		List<Long> ids = networkClient.getFriendsIds();
		for (long id : ids) {
			assertTrue("Unexpected id", id >= 0);
		}
	}

	public void testR_AckRemovedFriends() throws SmartMapClientException {

		networkClient.ackRemovedFriend(MY_ID);
	}

	public void testS_GetProfilePicture() throws SmartMapClientException {

		networkClient.getProfilePicture(MY_ID);
	}

	public void testT_createEvent() throws SmartMapClientException {

		long eventId=networkClient.createPublicEvent(FOOTBALL_TOURNAMENT);
		assertTrue("Unexpected id", eventId >= 0);

	}

	public void testU_updateEvent() throws SmartMapClientException {

		FOOTBALL_TOURNAMENT.setName("Exposition");
		networkClient.updateEvent(FOOTBALL_TOURNAMENT);
	}

	// TODO To complete
	public void testV_getPublicEvents() throws SmartMapClientException {

		List<Event> events = networkClient.getPublicEvents(45, 46, 1000);
		for (Event event : events) {
			assertTrue("Unexpected event id", event.getID() >= 0);
			assertTrue("Unexpected creator id", event.getCreator() >= 0);
			assertTrue("Unexpected end and start dates", event.getEndDate().after(event.getStartDate()));
			assertTrue("Unexpected latitude", (-90 <= event.getLocation().getLatitude())
					&& (event.getLocation().getLatitude() <= 90));
			assertTrue("Unexpected longitude", (-180 <= event.getLocation().getLongitude())
					&& (event.getLocation().getLongitude() <= 180));
			assertTrue("Unexpected position name", ((2 < event.getPositionName().length()) && (event
					.getPositionName().length() <= 60)));
			assertTrue("Unexpected event name",
					((2 < event.getName().length()) && (event.getName().length() <= 60)));
			assertTrue("Unexpected creator name", ((2 < event.getCreatorName().length()) && (event
					.getCreatorName().length() <= 60)));
			assertTrue("Unexpected event description", (event.getName().length() <= 255));

		}
	}

	private void assertValidIdAndName(User user) {
		assertTrue("Unexpected id", user.getID() >= 0);
		assertTrue("Unexpected name", (2 < user.getName().length()) && (user.getName().length() <= 60));
	}

}