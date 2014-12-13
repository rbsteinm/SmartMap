package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.contrib.DrawerActions.openDrawer;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;

public class SetLocationActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public SetLocationActivityTest() {
        super(MainActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.side_menu_events)).perform(click());
        openActionBarOverflowOrOptionsMenu(this.getInstrumentation().getTargetContext());
        onView(withId(R.id.showEventsMenuNewEvent)).perform(click());
        onView(withId(R.id.add_event_map)).perform(click());
    }

    public void testBackButtonGoesToAddEvent() {
        onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.add_event_activity)).check(matches(isDisplayed()));
    }

    public void testDoneButtonGoesToAddEvent() {
        onView(withId(R.id.set_location_done)).perform(click());
        onView(withId(R.id.add_event_activity)).check(matches(isDisplayed()));
    }

    public void testPressBackGoesToAddEvent() {
        pressBack();
        onView(withId(R.id.add_event_activity)).check(matches(isDisplayed()));
    }
}