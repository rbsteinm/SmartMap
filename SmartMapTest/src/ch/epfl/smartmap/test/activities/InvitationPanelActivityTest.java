package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import java.util.SortedSet;
import java.util.TreeSet;

import org.mockito.Mockito;

import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.test.database.MockInstances;

public class InvitationPanelActivityTest extends

ActivityInstrumentationTestCase2<MainActivity> {

    public InvitationPanelActivityTest() {
        super(MainActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        Cache newCache = Mockito.mock(Cache.class);
        SortedSet<Invitation> newSortedSet = new TreeSet<Invitation>();
        newSortedSet.add(MockInstances.ROBIN_FRIEND_INVITATION);
        newSortedSet.add(MockInstances.ROBIN_FRIEND_ACCEPTED_INVITATION);
        Mockito.when(newCache.getAllInvitations()).thenReturn(newSortedSet);
        ServiceContainer.setCache(newCache);
    }

    @Override
    protected void tearDown() throws Exception {
        this.getActivity().finish();
    }

    /**
     * Pressback shows main map
     */
    public void testAPressBack() {
        onView(withId(R.id.action_notifications)).perform(click());
        onView(withId(R.id.notification_activity)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    /**
     * Click on accepted invitation open user information activity
     * 
     * @throws InterruptedException
     */
    public void testClickOnAcceptedInvitation() throws InterruptedException {
        onView(withId(R.id.action_notifications)).perform(click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.user_info_header)).check(matches(isDisplayed()));
    }

    /**
     * Click on friend invitation open invitation tabs
     */
    public void testClickOnFriendInvitation() {
        onView(withId(R.id.action_notifications)).perform(click());
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());
        onView(withId(R.id.layout_invitations_tab)).check(matches(isDisplayed()));
    }
}