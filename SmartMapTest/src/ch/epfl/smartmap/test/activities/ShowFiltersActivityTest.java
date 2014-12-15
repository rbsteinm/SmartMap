package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mockito.Mockito;

import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowFiltersActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.FilterContainer;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

public class ShowFiltersActivityTest extends ActivityInstrumentationTestCase2<ShowFiltersActivity> {


	Filter filter;
	Set<Filter> filterSet;

	public ShowFiltersActivityTest() {
		super(ShowFiltersActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServiceContainer.forceInitSmartMapServices(this.getActivity());
		this.getActivity();
		filter=Filter.createFromContainer(new FilterContainer(3, "Family", new HashSet<Long>(Arrays.asList((long) 2)), true));
		this.createMockCacheWithMockFilter(filter);
	}

	public void testCancelCreateFilter(){
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withId(android.R.id.button2)).perform(click());
		onView(withId(R.id.show_filters_alert_dialog_edittext)).check((doesNotExist()));
	}

	public void testCanCreateFilterWithNonEmptyName() {
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withId(R.id.show_filters_alert_dialog_edittext)).perform(ViewActions.typeText("Family"));
		onView(withId(android.R.id.button2)).perform(click());
		onView(withId(R.id.activity_show_filters_follow_switch)).check(doesNotExist());
	}

	public void testCannotCreateFilterWithEmptyName() {
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withId(android.R.id.button1)).perform(click());
		onView(withId(R.id.activity_show_filters_follow_switch)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
	}

	public void testCanOppenCreateFilerDialog(){
		onView(withId(R.id.activity_show_filters_add_button)).perform(click());
		onView(withText("New filter")).check(matches(isDisplayed()));
		pressBack();
	}

	public void testFilterNameIsDisplayed(){
		onView(withText(filter.getName())).check(matches(isDisplayed()));
	}

	public void testRightNumberOfPeopleInsideFilerIsDisplayed(){
		onView(withText(filter.getIds().size() + " people inside this filter")).check(matches(isDisplayed()));
	}

	public void testSwitchIsCheckedIfFilterIsActive(){
		onView(withId(R.id.activity_show_filters_follow_switch)).check(ViewAssertions.matches(ViewMatchers.isChecked()));

	}

	public void testSwitchIsClickable(){
		onView(withId(R.id.activity_show_filters_follow_switch)).check(ViewAssertions.matches(ViewMatchers.isClickable()));
	}

	public void testSwitchIsDisplayed(){
		onView(withId(R.id.activity_show_filters_follow_switch)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		onView(withText("Activate")).check(matches(isDisplayed()));
	}

	//	public void testSwitchIsNotCheckedIfFilterIsNotActive(){
	//		Filter nonActiveFilter = Filter.createFromContainer(new FilterContainer(4, "Other", new HashSet<Long>(Arrays.asList((long) 2)), false));
	//		filterSet.remove(filter);
	//		this.createMockCacheWithMockFilter(nonActiveFilter);
	//		this.setActivity(this.getActivity());
	//		onView(withId(R.id.activity_show_filters_follow_switch)).check(ViewAssertions.matches((ViewMatchers.isNotChecked())));
	//	}

	private void createMockCacheWithMockFilter(Filter filter){
		Cache newCache = Mockito.mock(Cache.class);
		filterSet = new HashSet<Filter>();
		filterSet.add(filter);
		Mockito.when(newCache.getAllCustomFilters()).thenReturn(filterSet);
		Mockito.when(newCache.getFilter(3)).thenReturn(filter);
		ServiceContainer.setCache(newCache);
	}
}
