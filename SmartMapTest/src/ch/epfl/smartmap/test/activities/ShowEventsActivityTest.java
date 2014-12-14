package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.mockito.Mockito;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.SeekBar;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.PublicEvent;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import ch.epfl.smartmap.util.Utils;

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

    public void testCanOpenAddEventActivity() {
        Context context = this.getInstrumentation().getTargetContext();
        openActionBarOverflowOrOptionsMenu(context);

        onView(withText(context.getString(R.string.showEventsMenuNewEvent))).perform(ViewActions.click());

        onView(withId(R.id.addEventDescription)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    public void testCheckingMyEventsChangesListSize() throws InterruptedException {

        mActivity = this.getActivity();

        int initialSize = mActivity.getListAdapter().getCount();

        onView(withId(R.id.ShowEventsCheckBoxMyEv)).perform(ViewActions.click());
        mActivity = this.getActivity();
        int finalSize = mActivity.getListAdapter().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than initialSize: " + initialSize,
            finalSize < initialSize);
    }

    public void testCheckingNearMeChangesListSize() {
        int initialSize = mActivity.getListAdapter().getCount();
        onView(withId(R.id.ShowEventsCheckBoxNearMe)).perform(ViewActions.click());
        mActivity = this.getActivity();
        int finalSize = mActivity.getListAdapter().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than initialSize: " + initialSize,
            finalSize < initialSize);
    }

    public void testCheckingOnGoingChangesListSize() {
        int initialSize = mActivity.getListAdapter().getCount();
        onView(withId(R.id.ShowEventscheckBoxStatus)).perform(ViewActions.click());
        mActivity = this.getActivity();
        int finalSize = mActivity.getListAdapter().getCount();
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
        int initialSize = mActivity.getListAdapter().getCount();
        onView(withId(R.id.ShowEventsCheckBoxNearMe)).perform(ViewActions.click());
        onView(withId(R.id.showEventSeekBar)).perform(setProgress(0));
        mActivity = this.getActivity();
        int finalSize = mActivity.getListAdapter().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than initialSize: " + initialSize,
            finalSize < initialSize);

        onView(withId(R.id.showEventSeekBar)).perform(setProgress(100));
        mActivity = this.getActivity();
        int newFinalSize = mActivity.getListAdapter().getCount();
        assertTrue("finalSize: " + finalSize + " wasn't smaller than newFinalSize: " + initialSize,
            finalSize < newFinalSize);
    }

    public void testSeekBarDisabledByDefault() {
        onView(withId(R.id.showEventSeekBar)).check(matches(not(isEnabled())));
    }

    public void testZCanClickOnRefresh() {
        Context context = this.getInstrumentation().getTargetContext();
        openActionBarOverflowOrOptionsMenu(context);

        onView(withText(context.getString(R.string.show_events_menu_refresh))).perform(ViewActions.click());
    }

    private void addMockEventsInDB() throws SmartMapClientException, InterruptedException {

        ServiceContainer.initSmartMapServices(this.getActivity());

        Location loc = new Location("");
        loc.setLatitude(46.519056);
        loc.setLongitude(6.566758);

        String name = "SmartMap SwEng";

        UserContainer userContainer =
            new UserContainer(1, name, "123", "abc@abc.com", loc, "Mock lockation", User.NO_IMAGE,
                User.BlockStatus.UNBLOCKED, User.SELF);

        ServiceContainer.getCache().putUser(userContainer);

        Thread.sleep(1000);

        ServiceContainer.getSettingsManager().setLocation(loc);

        Calendar inFiveMins = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        inFiveMins.add(Calendar.MINUTE, 5);

        User me = ServiceContainer.getCache().getSelf();
        Set<Long> participants = new HashSet<Long>();

        GregorianCalendar timeE0 = new GregorianCalendar();
        timeE0.add(GregorianCalendar.MINUTE, -5);
        GregorianCalendar timeEndE0 = new GregorianCalendar();
        timeEndE0.add(GregorianCalendar.HOUR_OF_DAY, 5);
        Location lutry = new Location("Lutry");
        lutry.setLatitude(46.506038);
        lutry.setLongitude(6.685314);
        PublicEvent e0 =
            new PublicEvent(1, "Lutry fun", me, timeE0, timeEndE0, lutry, "Lutry", "Le fun à lutry",
                participants);

        GregorianCalendar timeE1 = new GregorianCalendar();
        timeE1.add(GregorianCalendar.DAY_OF_YEAR, 5);
        GregorianCalendar timeEndE1 = new GregorianCalendar();
        timeEndE1.add(GregorianCalendar.DAY_OF_YEAR, 10);
        Location lausanne = new Location("Lausanne");
        lausanne.setLatitude(46.519962);
        lausanne.setLongitude(6.633597);
        PublicEvent e1 =
            new PublicEvent(2, "Electrosanne", me, timeE1, timeEndE1, lausanne, "Lausanne", "boom boom",
                participants);

        GregorianCalendar timeE2 = new GregorianCalendar();
        timeE2.add(GregorianCalendar.HOUR_OF_DAY, 3);
        GregorianCalendar timeEndE2 = new GregorianCalendar();
        timeEndE2.add(GregorianCalendar.DAY_OF_YEAR, 2);
        Location epfl = new Location("EPFL");
        epfl.setLatitude(46.526120);
        epfl.setLongitude(6.563778);
        PublicEvent e2 =
            new PublicEvent(3, "Espresso tesing", me, timeE2, timeEndE2, epfl, "EPFL",
                "have a cup of Espresso", participants);

        GregorianCalendar timeE3 = new GregorianCalendar();
        timeE3.add(GregorianCalendar.HOUR_OF_DAY, 1);
        GregorianCalendar timeEndE3 = new GregorianCalendar();
        timeEndE3.add(GregorianCalendar.HOUR, 5);
        Location verbier = new Location("Verbier");
        verbier.setLatitude(1);
        verbier.setLongitude(2);
        PublicEvent e3 =
            new PublicEvent(4, "Far away", me, timeE3, timeEndE3, verbier, "Somewhere very far", "hello",
                participants);

        Set<Event> allEvents = new HashSet<Event>();
        allEvents.add(e0);
        allEvents.add(e1);
        allEvents.add(e2);
        allEvents.add(e3);

        Cache cache = Mockito.mock(Cache.class);
        Mockito.when(cache.getAllEvents()).thenReturn(allEvents);

        ServiceContainer.setCache(cache);
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