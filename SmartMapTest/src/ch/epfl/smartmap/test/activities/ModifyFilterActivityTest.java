package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ModifyFilterActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.FilterContainer;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.test.database.MockContainers;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
public class ModifyFilterActivityTest extends
ActivityInstrumentationTestCase2<ModifyFilterActivity> {

	FilterContainer filter;
	UserContainer user1;
	UserContainer user2;
	UserContainer user3;
	Set<UserContainer> allFriends;

	public ModifyFilterActivityTest() {
		super(ModifyFilterActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ServiceContainer.forceInitSmartMapServices(this.getInstrumentation()
				.getTargetContext());

		this.createMockItems();
		this.createMockCache();

		Intent intent = new Intent(this.getInstrumentation().getTargetContext()
				.getApplicationContext(), ModifyFilterActivity.class);
		intent.putExtra("FILTER", filter.getId());
		this.setActivityIntent(intent);

		this.getActivity();

	}

	public void testRemoveButtonIsDisplayed() {
		onView(withId(R.id.action_remove_filter)).check(matches(isDisplayed()));
	}

	public void testSaveButtonIsDisplayed() {
		onView(withId(R.id.action_save_filter)).check(matches(isDisplayed()));
	}

	public void testRenameButtonIsDisplayed() {
		onView(withId(R.id.action_rename_filter)).check(matches(isDisplayed()));
	}

	public void testClickRemoveOpensDialog(){
		onView(withId(R.id.action_remove_filter)).perform(click());
		onView(withText("Remove filter?")).check(matches(isDisplayed()));
	}



	public void testClickOnRenameOpensDialog(){
		onView(withId(R.id.action_rename_filter)).perform(click());
		onView(withText("Cancel")).check(matches(isDisplayed()));
	}

	public void testRenameFilterChangesActivityTitle(){
		onView(withId(R.id.action_rename_filter)).perform(click());
		onView(withId(R.id.show_filters_alert_dialog_edittext)).perform(ViewActions.typeText("New name"));
		onView(withId(android.R.id.button1)).perform(click());
		onView(withText("New name")).check(matches(isDisplayed()));
	}



	private void createMockCache() {
		Cache newCache = new Cache();
		newCache.putFilter(filter);
		newCache.putUser(user1);
		newCache.putUser(user2);
		newCache.putUser(user3);
		ServiceContainer.setCache(newCache);
	}

	private void createMockItems() {
		user1 = MockContainers.ALAIN_CONTAINER;
		user2 = MockContainers.JULIEN_CONTAINER;
		user3 = MockContainers.ROBIN_CONTAINER;
		allFriends = new HashSet<UserContainer>(Arrays.asList(user1, user2,
				user3));
		filter = new FilterContainer(3, "Family", new HashSet<Long>(
				Arrays.asList(user1.getId())), true);

	}
}
