package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddEventActivity;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * Tests for AddEventActivity
 * For some reason, espresso sometimes fail to click on a view. Just relaunch
 * the test if this happens.
 * 
 * @author SpicyCH
 */
public class AddEventActivityTest extends ActivityInstrumentationTestCase2<AddEventActivity> {

    private AddEventActivity mAddEventActivity;

    public AddEventActivityTest() {
        super(AddEventActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        mAddEventActivity = this.getActivity();
    }

    @Override
    protected void tearDown() {
        this.getActivity().finish();
    }

    public void testCannotCreateEventWith1Field() {
        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText("TEST_NAME"));

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testCannotCreateEventWith2Field() {
        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText("TEST_NAME"));

        onView(withId(R.id.addEventEndDate)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Done")).perform(ViewActions.click());

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testCannotCreateEventWith3Field() {
        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText("TEST_NAME"));

        onView(withId(R.id.addEventEndDate)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Done")).perform(ViewActions.click());

        onView(withId(R.id.addEventEndTime)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Done")).perform(ViewActions.click());

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testCannotCreateEventWithoutFields() {
        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());
        // If the description is displayed, we are still in AddEventActivity,
        // hence the event couldn't be created.
        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testCannotCreateWithEmptyNameAndGoodOtherFields() {
        // Regression test for bug #40

        Handler myHandler = new Handler();

        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                // call the activity method that updates the UI

                TextView latitude = (TextView) mAddEventActivity.findViewById(R.id.addEventLatitude);
                TextView longitude = (TextView) mAddEventActivity.findViewById(R.id.addEventLongitude);

                latitude.setText("1");
                longitude.setText("2");
            }
        };

        myHandler.post(updateRunnable);

        onView(withId(R.id.addEventEndDate)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Done")).perform(ViewActions.click());

        onView(withId(R.id.addEventEndTime)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Done")).perform(ViewActions.click());

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }
}