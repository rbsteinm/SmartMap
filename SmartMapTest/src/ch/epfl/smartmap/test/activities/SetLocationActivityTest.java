package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddEventActivity;

public class SetLocationActivityTest extends ActivityInstrumentationTestCase2<AddEventActivity> {

    public SetLocationActivityTest() {
        super(AddEventActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        onView(withId(R.id.add_event_map)).perform(click());
    }

    public void testBackButtonGoesToAddEvent() {
        onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.add_event_activity)).check(matches(isDisplayed()));
    }

    public void testDoneButtonGoesToAddEvent() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.set_location_done)).perform(click());
        onView(withId(R.id.add_event_activity)).check(matches(isDisplayed()));
    }

    public void testPressBackGoesToAddEvent() {
        pressBack();
        onView(withId(R.id.add_event_activity)).check(matches(isDisplayed()));
    }
}