package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddEventActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;

import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * Tests AddEventActivity. For some reason, espresso sometimes fail to click
 * on a view. Just relaunch the test if this happens.
 * 
 * @author SpicyCH
 */

public class AddEventActivityTest extends ActivityInstrumentationTestCase2<AddEventActivity> {

    private static final String TEST_NAME = "This is a test event";

    public AddEventActivityTest() {
        super(AddEventActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        this.getActivity();
        ServiceContainer.setSettingsManager(new SettingsManager(this.getActivity()));
    }

    public void testCannnotCreateEventWithoutPlaceName() {
        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText(TEST_NAME));
        onView(withId(R.id.addEventPlaceName)).perform(ViewActions.clearText());

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testCannotCreateEventWithoutFields() {
        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        // If the description is displayed, we are still in AddEventActivity,
        // hence the event couldn't be created.
        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testCannotCreateEventWithTooShortEventName() {
        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText("s"));

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testOnlyDescription() throws InterruptedException {
        onView(withId(R.id.addEventDescription)).perform(ViewActions.typeText("Some description"));

        Espresso.pressBack();

        // Espresso doesn't wait the end of the pressback.
        Thread.sleep(1000);

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());
        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testOpenSetLocationWhenClickOnMap() throws InterruptedException {
        onView(withId(R.id.add_event_map)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.set_location_map)).check(matches(isDisplayed()));
    }

    public void testZCanCreateEventWithoutDescription() {
        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText(TEST_NAME));

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}