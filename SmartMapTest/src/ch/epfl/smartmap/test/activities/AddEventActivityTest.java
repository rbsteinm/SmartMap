package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddEventActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

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

        ServiceContainer.initSmartMapServices(this.getActivity());

        Location loc = new Location("");
        loc.setLatitude(46);
        loc.setLongitude(6);

        String token =
            "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";

        long facebookId = 1482245642055847L;

        String name = "SmartMap SwEng";

        ServiceContainer.getCache().putUser(
            new UserContainer(2, name, "123", "abc@abc.com", loc, "Mock lockation", User.NO_IMAGE,
                User.BlockStatus.UNBLOCKED, User.SELF));

        ServiceContainer.getNetworkClient().authServer(name, facebookId, token);

        Thread.sleep(1000);

        ServiceContainer.getSettingsManager().setLocation(loc);

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
        onView(withId(R.id.set_location_activity)).check(matches(isDisplayed()));
    }

    public void testZZZCanCreateEventWithoutDescription() throws InterruptedException {
        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText(TEST_NAME));

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

        this.getActivity();

        onView(withId(R.id.addEventDescription)).check(
            ViewAssertions.matches(not(ViewMatchers.isDisplayed())));
    }
}