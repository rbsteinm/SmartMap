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
import static org.hamcrest.Matchers.not;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity(); // prevent error
        // "No activities found. Did you forget to launch the activity by calling getActivity()"
    }

    public void testCloseSearchViewWithBackButton() {
        onView(withId(R.id.action_search)).perform(click());
        pressBack();
        pressBack();
        onView(withId(R.id.search_panel)).check(matches(not(isDisplayed())));
    }

    public void testCloseSearchViewWithMenuItem() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.action_hide_search)).perform(click());
        onView(withId(R.id.search_panel)).check(matches(not(isDisplayed())));
    }

    public void testNormalSearchQuery() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.action_search)).perform(ViewActions.typeText("Julien Perrenoud"));
        // TODO : Check there is only one result
    }

    public void testOpenAndCloseNotificationActivityUsingHome() {
        onView(withId(R.id.action_notifications)).perform(click());
        onView(withId(R.id.notification_activity)).check(matches(isDisplayed()));
        pressBack();
        // onView(withId(R.id.notification_activity)).check(
        // matches(not(hasFocus())));
    }

    public void testOpenAndCloseSideMenu() throws Exception {
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        closeDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    public void testOpenFriendsActivity() throws Exception {
        ListView lv = (ListView) this.getActivity().findViewById(R.id.left_drawer_listView);
        View friendView = lv.getChildAt(1);
        openDrawer(R.id.drawer_layout);
        onView(withId(friendView.getId())).perform(click());
        onView(withId(R.id.friends_pager_activity)).check(matches(isDisplayed()));
        // TODO check that FriendsActivity is the current Activity
    }

    public void testOpenNotificationActivity() {
        onView(withId(R.id.action_notifications)).perform(click());
        onView(withId(R.id.notification_activity)).check(matches(isDisplayed()));
    }

    public void testOpenSearchView() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_panel)).check(matches(isDisplayed()));
    }

    public void testOpenSideMenu() throws Exception {
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }

    public void testOpenSideMenuUsingButton() throws Exception {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    public void testSideMenuViewExist() throws Exception {
        // TODO check that all the views in the side menu exist
    }

    public void testWrongSearchQuery() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.action_search)).perform(ViewActions.typeText("flksdh�fjkslkfshdfljkshfd"));
        // TODO : Check no result is displayed
    }
}