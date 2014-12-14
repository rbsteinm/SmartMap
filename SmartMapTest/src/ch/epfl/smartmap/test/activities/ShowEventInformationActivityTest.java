package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.content.Context;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddEventActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * Test ShowEventInformationActivity. Since this activity needs a click on
 * ShowEventsActivity to be launched, we extend
 * ActivityInstrumentationTestCase2<ShowEventsActivity>. If the tests crash,
 * relaunch.
 * 
 * @author SpicyCH
 * @author agpmilli
 */
public class ShowEventInformationActivityTest extends ActivityInstrumentationTestCase2<AddEventActivity> {

    private static final String CREATOR_NAME = "SmartMap SwEng";
    private static final String EVENT_NAME = "Some other test event";
    private Context mContext;

    public ShowEventInformationActivityTest() {
        super(AddEventActivity.class);
    }

    // The standard JUnit 3 setUp method run for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.getActivity();

        mContext = this.getInstrumentation().getTargetContext();

        ServiceContainer.initSmartMapServices(this.getActivity());

        Location loc = new Location("");
        loc.setLatitude(46.519056);
        loc.setLongitude(6.566758);

        String token =
            "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";

        long facebookId = 1482245642055847L;

        String name = "SmartMap SwEng";

        ServiceContainer.getNetworkClient().authServer(name, facebookId, token);

        ServiceContainer.getCache().putUser(
            new UserContainer(1, name, "123", "abc@abc.com", loc, "Mock lockation", User.NO_IMAGE,
                User.BlockStatus.UNBLOCKED, User.SELF));

        Thread.sleep(1000);

        ServiceContainer.getSettingsManager().setLocation(loc);

        onView(withId(R.id.addEventEventName)).perform(ViewActions.typeText(EVENT_NAME));

        onView(withId(R.id.addEventButtonCreateEvent)).perform(ViewActions.click());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreatorDisplayed() {
        onView(withId(R.id.show_event_info_creator)).check(ViewAssertions.matches(ViewMatchers.withText(CREATOR_NAME)));
    }

    public void testDescriptionDisplayed() {
        onView(withId(R.id.show_event_info_description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testEventEndDisplayed() {

        onView(withId(R.id.show_event_info_end_date)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.show_event_info_end_hour)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testEventNameDisplayedCorrectly() {

        onView(withId(R.id.show_event_info_event_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(EVENT_NAME)));
    }

    public void testStartDisplayedCorrectly() {

        onView(withId(R.id.show_event_info_start_date)).check(
            ViewAssertions.matches(ViewMatchers.withText(mContext.getString(R.string.events_list_item_adapter_today))));
    }

    public void testTownAndCitiyDisplayed() {

        onView(withId(R.id.show_event_info_town_and_country)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testZCanInviteFriends() {
        onView(withId(R.id.show_event_info_invite_friends_button)).perform(ViewActions.click());
        onView(ViewMatchers.withText(mContext.getString(R.string.invite_friend_send_button))).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}