package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.hasFocus;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.NotificationsActivity;

public class NotificationsActivityTest extends
		ActivityInstrumentationTestCase2<NotificationsActivity> {
	public NotificationsActivityTest() {
		super(NotificationsActivity.class);
	}

	// The standard JUnit 3 setUp method run for for every test
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.getActivity(); // prevent error
		// "No activities found. Did you forget to launch the activity by calling getActivity()"
		// TODO UNCOMMENT WHEN CACHE IS READY
		// DatabaseHelper.initialize(this.getActivity());
		// DatabaseHelper.getInstance().addFriendInvitation(
		// new FriendInvitation(1, 3, "Smart Map", Invitation.UNREAD));
	}

	public void testClickOnNotification() {
		// TODO
	}

	public void testPressBackGoesToAddEvent() {
		onView(withId(R.id.notification_activity)).check(matches(hasFocus()));
		pressBack();
		onView(withId(R.id.notification_activity)).check(
				matches(not(hasFocus())));
	}
}