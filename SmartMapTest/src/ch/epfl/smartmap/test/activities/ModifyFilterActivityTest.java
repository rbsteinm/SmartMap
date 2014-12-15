package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

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

	public void test1() {
		onView(withId(R.id.action_remove_filter)).check(matches(isDisplayed()));
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
		user1 = MockContainers.ALAIN;
		user2 = MockContainers.JULIEN;
		user3 = MockContainers.ROBIN;
		allFriends = new HashSet<UserContainer>(Arrays.asList(user1, user2,
				user3));
		filter = new FilterContainer(3, "Family", new HashSet<Long>(
				Arrays.asList(user1.getId())), true);

	}
}
