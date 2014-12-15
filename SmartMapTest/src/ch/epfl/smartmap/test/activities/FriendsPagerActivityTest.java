package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeRight;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withContentDescription;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.test.database.MockContainers;

public class FriendsPagerActivityTest extends
ActivityInstrumentationTestCase2<FriendsPagerActivity> {

	UserContainer friendContainer;
	InvitationContainer invitation;

	public FriendsPagerActivityTest() {
		super(FriendsPagerActivity.class);
	}

	// The standard JUnit 3 setUp method run for for every test
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServiceContainer.forceInitSmartMapServices(this.getInstrumentation().getTargetContext());

		this.createMockItems();
		this.createMockCache();
		this.getActivity();

	}

	// FIXME : cannot click on invitation in invitation list whereas can click
	// on friend in friend list by doing the same way
	public void ignoredtestClickOnInvitingUsersOpensDialog()
			throws InterruptedException {
		onView(withId(R.id.myViewPager)).perform(swipeLeft());
		onData(anything())
		.inAdapterView(withContentDescription("Friend invitation list"))
		.atPosition(0).perform(click());
		onView(withText("Accept")).check(matches(isDisplayed()));
		onView(withId(R.id.myViewPager)).perform(swipeRight());
	}

	public void testAOpenAddFriendActivity() {
		onView(withId(R.id.activity_friends_add_button)).perform(click());
		onView(withId(R.id.add_friend_activity_searchBar)).check(
				matches(isDisplayed()));
		pressBack();
	}

	public void testClickOnFriendLeadsToFriendsInfo() {
		swipeRight();
		onData(anything()).inAdapterView(withContentDescription("Friend list"))
		.atPosition(0).perform(click());
		onView(withId(R.id.user_info_remove_button)).check(
				matches(isDisplayed()));
		pressBack();
	}

	public void testFriendsNamesAreDisplayed() {
		onView(withText(friendContainer.getName())).check(matches(isDisplayed()));
	}

	public void testFriendsProfilePicturesAreDisplayed() {
		onView(withId(R.id.activity_friends_picture)).check(
				matches(isDisplayed()));
	}

	//TODO when live instances will be ready
	//	public void testFriendsSubtitlesAreDisplayed() {
	//		onView(withText(friendContainer.getLocationString())).check(matches(isDisplayed()));
	//	}

	public void testInvitingUsersAreDisplayed() {
		onView(withId(R.id.myViewPager)).perform(swipeLeft());
		onView(withText(invitation.getUser().getName())).check(
				matches(isDisplayed()));
		onView(withId(R.id.myViewPager)).perform(swipeRight());
	}

	public void testInvitingUsersPicturesAreDisplayed() {
		onView(withId(R.id.myViewPager)).perform(swipeLeft());
		onView(withId(R.id.activity_friends_inviter_picture)).check(
				matches(isDisplayed()));
		onView(withId(R.id.myViewPager)).perform(swipeRight());
	}

	public void testSwipeLeftAndRight() {
		onView(withId(R.id.myViewPager)).perform(swipeLeft());
		onView(withId(R.id.layout_invitations_tab)).check(
				matches(isDisplayed()));
		onView(withId(R.id.myViewPager)).perform(swipeRight());
		onView(withId(R.id.layout_friends_tab)).check(matches(isDisplayed()));
	}

	private void createMockItems() {
		friendContainer = MockContainers.ALAIN_CONTAINER;
		invitation = MockContainers.ROBIN_FRIEND_INVITATION_CONTANER;

	}

	private void createMockCache() {
		Cache newCache = new Cache();

		newCache.putInvitation(invitation);
		newCache.putUser(friendContainer);
		newCache.putFilter(MockContainers.DEFAULT_CONTAINER);
		ServiceContainer.setCache(newCache);

	}

}