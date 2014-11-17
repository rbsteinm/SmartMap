package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddEventActivity;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

public class AddEventActivityTest extends
        ActivityInstrumentationTestCase2<AddEventActivity> {
    public AddEventActivityTest() {
        super(AddEventActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testCannotCreateEventWithoutFields() {
        onView(withId(R.id.addEventButtonCreateEvent)).perform(
                ViewActions.click());
        // If the description is displayed, we are still in AddEventActivity, hence the event couldn't be created.
        onView(withId(R.id.addEventDescription)).check(
                ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testCanCreateEventWithGoodFields() {
        // TODO fill all fields then check event is displayed in ShowEventsActivity's ListView
    }

}