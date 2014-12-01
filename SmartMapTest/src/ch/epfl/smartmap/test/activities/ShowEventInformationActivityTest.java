package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

import java.util.GregorianCalendar;

import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowEventsActivity;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * Test ShowEventInformationActivity. Since this activity needs a click on
 * ShowEventsActivity to be launched, we extend
 * ActivityInstrumentationTestCase2<ShowEventsActivity>. If the tests crash,
 * relaunch. Espresso sucks.
 * 
 * @author SpicyCH
 */
public class ShowEventInformationActivityTest extends ActivityInstrumentationTestCase2<ShowEventsActivity> {

    private static final String CREATOR_NAME = "Julien";
    private static final String DESCRIPTION =
        "It's a vertical free-verse poem on the mountain. It�s the ultimate expression of all that"
            + "is fun and liberating about sliding on snow in wintertime.";
    private static final String EVENT_NAME = "Freeride World Tour";

    private ShowEventsActivity mActivity;

    public ShowEventInformationActivityTest() {
        super(ShowEventsActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = this.getActivity();

        this.addMockEventsInDB();
    }

    @Override
    protected void tearDown() {
        this.getActivity().finish();
    }

    public void testCreatorNameDisplayedCorrectly() {
        onView(withId(0)).perform(ViewActions.click());
        onView(ViewMatchers.withId(android.R.id.button3)).perform(ViewActions.click());

        onView(withId(R.id.show_event_info_creator)).check(
            ViewAssertions.matches(ViewMatchers.withText("By " + CREATOR_NAME)));
    }

    public void testEventEndDisplayed() {
        onView(withId(0)).perform(ViewActions.click());

        onView(ViewMatchers.withId(android.R.id.button3)).perform(ViewActions.click());

        onView(withId(R.id.show_event_info_end)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testEventNameDisplayedCorrectly() {
        onView(withId(0)).perform(ViewActions.click());
        onView(ViewMatchers.withId(android.R.id.button3)).perform(ViewActions.click());

        onView(withId(R.id.show_event_info_event_name)).check(
            ViewAssertions.matches(ViewMatchers.withText(EVENT_NAME)));
    }

    public void testStartDisplayedCorrectly() {
        onView(withId(0)).perform(ViewActions.click());
        onView(ViewMatchers.withId(android.R.id.button3)).perform(ViewActions.click());

        onView(withId(R.id.show_event_info_start)).check(
            ViewAssertions.matches(ViewMatchers.withText("Today")));
    }

    public void testTownAndCitiyDisplayedCorrectly() {
        onView(withId(0)).perform(ViewActions.click());
        onView(ViewMatchers.withId(android.R.id.button3)).perform(ViewActions.click());

        onView(withId(R.id.show_event_info_town_and_country)).check(
            ViewAssertions.matches(ViewMatchers.withText("Verbier, Switzerland")));
    }

    private void addMockEventsInDB() {

        GregorianCalendar timeE3 = new GregorianCalendar();
        timeE3.add(GregorianCalendar.MINUTE, 5);
        GregorianCalendar timeEndE3 = new GregorianCalendar();
        timeEndE3.add(GregorianCalendar.MINUTE, 10);
        Location verbier = new Location("Verbier");
        verbier.setLatitude(46.096076);
        verbier.setLongitude(7.228875);

        /*
         * PublicEvent e3 = new PublicEvent(EVENT_NAME, 1, CREATOR_NAME, timeE3,
         * timeEndE3, verbier);
         * e3.setID(3);
         * e3.setPositionName("Verbier");
         * String descrE3 = DESCRIPTION;
         * e3.setDescription(descrE3);
         * DatabaseHelper dbHelper = DatabaseHelper.getInstance();
         * dbHelper.clearAll();
         * dbHelper.addEvent(e3);
         */

    }

}