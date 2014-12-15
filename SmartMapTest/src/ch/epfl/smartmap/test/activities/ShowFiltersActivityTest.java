package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowFiltersActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.FilterContainer;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.test.database.MockContainers;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

public class ShowFiltersActivityTest extends
ActivityInstrumentationTestCase2<ShowFiltersActivity> {

	FilterContainer filter;
	UserContainer user1;
	UserContainer user2;
	UserContainer user3;
	Set<UserContainer> allFriends;
	Set<FilterContainer> filterSet;

	public ShowFiltersActivityTest() {
		super(ShowFiltersActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServiceContainer.forceInitSmartMapServices(this.getInstrumentation()
				.getTargetContext());
		this.createMockItems();
		this.createMockCache();
		this.getActivity();

	}

	public void testCancelCreateFilter() {
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withId(android.R.id.button2)).perform(click());
		onView(withId(R.id.show_filters_alert_dialog_edittext)).check(
				(doesNotExist()));
	}

	public void testCanCreateFilterWithNonEmptyName() {
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withId(R.id.show_filters_alert_dialog_edittext)).perform(
				ViewActions.typeText("Filter"));
		onView(withId(android.R.id.button1)).perform(click());
		onView(withText("Save")).check(matches(isDisplayed()));
	}

	public void testCannotCreateFilterWithEmptyName() {
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withId(android.R.id.button2)).perform(click());
		onView(withId(R.id.activity_show_filters_follow_switch)).check(
				ViewAssertions.matches(ViewMatchers.isDisplayed()));
	}

	public void testCanOppenCreateFilerDialog() {
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withText("New Filter")).check(matches(isDisplayed()));
		// pressBack();
	}

	public void testFilterNameIsDisplayed() {
		onView(withText(filter.getName())).check(matches(isDisplayed()));
	}

	public void testRightNumberOfPeopleInsideFilerIsDisplayed() {
		onView(withText(filter.getIds().size() + " people inside this filter"))
		.check(matches(isDisplayed()));
	}

	public void testSwitchIsCheckedIfFilterIsActive() {
		onView(withId(R.id.activity_show_filters_follow_switch)).check(
				ViewAssertions.matches(ViewMatchers.isChecked()));

	}

	public void testSwitchIsClickable() {
		onView(withId(R.id.activity_show_filters_follow_switch)).check(
				ViewAssertions.matches(ViewMatchers.isClickable()));
	}

	public void testSwitchIsDisplayed() {
		onView(withId(R.id.activity_show_filters_follow_switch)).check(
				ViewAssertions.matches(ViewMatchers.isDisplayed()));
		onView(withText("Activate")).check(matches(isDisplayed()));
	}

	public void testPerformClickOnFilterOpensModifyFilter() {
		onData(anything()).inAdapterView(withId(android.R.id.list))
		.atPosition(0).perform(click());
		onView(withText("Save")).check(matches(isDisplayed()));
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
		filterSet = new HashSet<FilterContainer>();
		filterSet.add(filter);
	}
}
