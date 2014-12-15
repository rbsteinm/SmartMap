package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.contrib.DrawerActions.closeDrawer;
import static com.google.android.apps.common.testing.ui.espresso.contrib.DrawerActions.openDrawer;
import static com.google.android.apps.common.testing.ui.espresso.contrib.DrawerMatchers.isClosed;
import static com.google.android.apps.common.testing.ui.espresso.contrib.DrawerMatchers.isOpen;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;

/**
 * Test for mainActivity
 * These tests pass one by one but not everytime you launch them all together
 * 
 * @author agpmilli
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        this.getActivity().finish();
    }

    /**
     * Test close search view using pressBack()
     */
    public void testCloseSearchViewWithBackButton() {
        onView(withId(R.id.action_search)).perform(click());
        pressBack();
        pressBack();
        onView(withId(R.id.search_panel)).check(matches(not(isDisplayed())));
    }

    /**
     * Test close search view using home
     */
    public void testCloseSearchViewWithMenuItem() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.action_hide_search)).perform(click());
        onView(withId(R.id.search_panel)).check(matches(not(isDisplayed())));
    }

    /**
     * Test open about activity using side menu
     */
    public void testOpenAboutMenu() {
        onView(withId(android.R.id.home)).perform(click());
        onView(allOf(withId(R.id.side_menu_text_view), withText(R.string.about_text))).perform(click());
        onView(withId(R.id.about_header)).check(matches(isDisplayed()));
    }

    /**
     * Test open and close notification activity
     */
    public void testOpenAndCloseNotificationActivityUsingHome() {
        onView(withId(R.id.action_notifications)).perform(click());
        onView(withId(R.id.notification_activity)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    /**
     * Test open and close side menu using slide
     */
    public void testOpenAndCloseSideMenu() {
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        closeDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    /**
     * Test open events activity using side menu
     */
    public void testOpenEventMenu() throws InterruptedException {
        onView(withId(android.R.id.home)).perform(click());
        onView(allOf(withId(R.id.side_menu_text_view), withText(R.string.events_text))).perform(click());
        onView(withId(R.id.show_event_header)).check(matches(isDisplayed()));
    }

    /**
     * Test open filters activity using side menu
     */
    public void testOpenFilterMenu() {
        onView(withId(android.R.id.home)).perform(click());
        onView(allOf(withId(R.id.side_menu_text_view), withText(R.string.filters_text))).perform(click());
        onView(withId(R.id.show_filter_header)).check(matches(isDisplayed()));
    }

    /**
     * Test open friends activity using side menu
     */
    public void testOpenFriendMenu() {
        onView(withId(android.R.id.home)).perform(click());
        onView(allOf(withId(R.id.side_menu_text_view), withText(R.string.friends_text))).perform(click());
        onView(withId(R.id.friends_pager_activity)).check(matches(isDisplayed()));
    }

    /**
     * Test open notification activity by clicking button
     */
    public void testOpenNotificationActivity() {
        onView(withId(R.id.action_notifications)).perform(click());
        onView(withId(R.id.notification_activity)).check(matches(isDisplayed()));
    }

    /**
     * Test open search view
     */
    public void testOpenSearchView() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_panel)).check(matches(isDisplayed()));
    }

    /**
     * Test open settings activity using side menu
     */
    public void testOpenSettingsMenu() {
        onView(withId(android.R.id.home)).perform(click());
        onView(withText(R.string.settings_text)).perform(click());
        onView(withText(R.string.settings_offline)).check(matches(isDisplayed()));
    }

    /**
     * Test open side menu using button
     */
    public void testOpenSideMenuUsingButton() {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    /**
     * Test open side menu by sliding
     * When we launch all tests it doesn't pass but it pass alone because of
     * openDrawer issue :
     * https://code.google.com/p/android-test-kit/issues/detail?id=64
     */
    public void testZOpenSideMenu() {
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }
}