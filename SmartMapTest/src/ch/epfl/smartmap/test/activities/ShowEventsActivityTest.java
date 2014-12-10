package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import android.app.ListActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.SeekBar;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowEventsActivity;

import com.google.android.apps.common.testing.ui.espresso.UiController;
import com.google.android.apps.common.testing.ui.espresso.ViewAction;
import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * Test different features of ShowEventActivity. I might need to rethink the
 * tests if the server interferes with them
 * later.
 * 
 * @author SpicyCH
 */
public class ShowEventsActivityTest extends ActivityInstrumentationTestCase2<ShowEventsActivity> {

    private ListActivity mActivity;

    public ShowEventsActivityTest() {
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

    public void testCanClickOnAnEvent() throws InterruptedException {

        // Clicks the first element of the list
        onView(withId(0)).perform(ViewActions.click());

        // The dialog may need a network access that might take some time to be
        // displayed
        Thread.sleep(2000);

        // If the AlertDialog has been opened, then the button2 must be
        // displayed. Espresso sometimes cannot find this view, but relaunch the
        // test if this happens, or make espresso functional.
        onView(ViewMatchers.withId(android.R.id.button3)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testCanOpenAddEventActivity() {
        openActionBarOverflowOrOptionsMenu(this.getInstrumentation().getTargetContext());

        onView(withText("Create a new event")).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testCheckingMyEventsChangesListSize() {
        int initialSize = mActivity.getListView().getCount();
        onView(withId(R.id.ShowEventsCheckBoxMyEv)).perform(ViewActions.click());
        mActivity = this.getActivity();
        int finalSize = mActivity.getListView().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than initialSize: " + initialSize,
            finalSize < initialSize);
    }

    public void testCheckingNearMeChangesListSize() {
        // FIXME this test fails sometimes for NO REASONS, wtf???
        int initialSize = mActivity.getListView().getCount();
        onView(withId(R.id.ShowEventsCheckBoxNearMe)).perform(ViewActions.click());
        mActivity = this.getActivity();
        int finalSize = mActivity.getListView().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than initialSize: " + initialSize,
            finalSize < initialSize);
    }

    public void testCheckingOnGoingChangesListSize() {
        int initialSize = mActivity.getListView().getCount();
        onView(withId(R.id.ShowEventscheckBoxStatus)).perform(ViewActions.click());
        mActivity = this.getActivity();
        int finalSize = mActivity.getListView().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than initialSize: " + initialSize,
            finalSize < initialSize);
    }

    public void testSeekBarCannotGoToZero() {
        onView(withId(R.id.showEventSeekBar)).perform(setProgress(0));
        onView(withId(R.id.showEventKilometers)).check(matches(not(ViewMatchers.withText("0 km"))));
    }

    public void testSeekBarChangesKilometersShown() {
        onView(withId(R.id.showEventSeekBar)).perform(setProgress(10));
        onView(withId(R.id.showEventKilometers)).check(matches(ViewMatchers.withText("10 km")));
        onView(withId(R.id.showEventSeekBar)).perform(setProgress(15));
        onView(withId(R.id.showEventKilometers)).check(matches(ViewMatchers.withText("15 km")));
    }

    public void testSeekBarChangesListSize() {
        int initialSize = mActivity.getListView().getCount();
        onView(withId(R.id.ShowEventsCheckBoxNearMe)).perform(ViewActions.click());
        onView(withId(R.id.showEventSeekBar)).perform(setProgress(0));
        mActivity = this.getActivity();
        int finalSize = mActivity.getListView().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than initialSize: " + initialSize,
            finalSize < initialSize);

        onView(withId(R.id.showEventSeekBar)).perform(setProgress(100));
        mActivity = this.getActivity();
        int newFinalSize = mActivity.getListView().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than newFinalSize: " + initialSize,
            finalSize < newFinalSize);
    }

    public void testSeekBarDisabledByDefault() {
        onView(withId(R.id.showEventSeekBar)).check(matches(not(isEnabled())));
    }

    private void addMockEventsInDB() {
        /*
         * GregorianCalendar timeE0 = new GregorianCalendar();
         * timeE0.add(GregorianCalendar.MINUTE, -5);
         * GregorianCalendar timeEndE0 = new GregorianCalendar();
         * timeEndE0.add(GregorianCalendar.HOUR_OF_DAY, 5);
         * Location lutry = new Location("Lutry");
         * lutry.setLatitude(46.506038);
         * lutry.setLongitude(6.685314);
         * PublicEvent e0 = new PublicEvent("Now Event", 2, "Robich", timeE0,
         * timeEndE0, lutry);
         * e0.setID(0);
         * e0.setPositionName("Lutry");
         * GregorianCalendar timeE1 = new GregorianCalendar();
         * timeE1.add(GregorianCalendar.DAY_OF_YEAR, 5);
         * GregorianCalendar timeEndE1 = new GregorianCalendar();
         * timeEndE1.add(GregorianCalendar.DAY_OF_YEAR, 10);
         * Location lausanne = new Location("Lausanne");
         * lausanne.setLatitude(46.519962);
         * lausanne.setLongitude(6.633597);
         * PublicEvent e1 = new PublicEvent("Swag party", 2, "Robich", timeE1,
         * timeEndE1, lausanne);
         * e1.setID(1);
         * e1.setPositionName("Lausanne");
         * GregorianCalendar timeE2 = new GregorianCalendar();
         * timeE2.add(GregorianCalendar.HOUR_OF_DAY, 3);
         * GregorianCalendar timeEndE2 = new GregorianCalendar();
         * timeEndE2.add(GregorianCalendar.DAY_OF_YEAR, 2);
         * Location epfl = new Location("EPFL");
         * epfl.setLatitude(46.526120);
         * epfl.setLongitude(6.563778);
         * PublicEvent e2 = new PublicEvent("LOL Tournament", 1, "Alain",
         * timeE2, timeEndE2, epfl);
         * e2.setID(2);
         * e2.setPositionName("EPFL");
         * GregorianCalendar timeE3 = new GregorianCalendar();
         * timeE3.add(GregorianCalendar.HOUR_OF_DAY, 1);
         * GregorianCalendar timeEndE3 = new GregorianCalendar();
         * timeEndE3.add(GregorianCalendar.HOUR, 5);
         * Location verbier = new Location("Verbier");
         * verbier.setLatitude(46.096076);
         * verbier.setLongitude(7.228875);
         * PublicEvent e3 = new PublicEvent("Freeride World Tour", 1, "Julien",
         * timeE3, timeEndE3, verbier);
         * e3.setID(3);
         * e3.setPositionName("Verbier");
         * String descrE3 =
         * "It�s a vertical free-verse poem on the mountain. It�s the ultimate expression of all that"
         * + "is fun and liberating about sliding on snow in wintertime.";
         * e3.setDescription(descrE3);
         * DatabaseHelper dbHelper = DatabaseHelper.getInstance();
         * dbHelper.clearAll();
         * dbHelper.addEvent(e0);
         * dbHelper.addEvent(e1);
         * dbHelper.addEvent(e2);
         * dbHelper.addEvent(e3);
         */

    }

    // from stackoverflow
    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public org.hamcrest.Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Set a progress";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SeekBar) view).setProgress(progress);
            }
        };
    }

}