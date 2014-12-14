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
import ch.epfl.smartmap.cache.MockGenerator;

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
        Invitation newEventInvitation = MockGenerator.getInvitation(Invitation.EVENT_INVITATION);
        Invitation newAcceptedFriendInvitation = MockGenerator.getInvitation(Invitation.ACCEPTED_FRIEND_INVITATION);
        Invitation newFriendInvitation = MockGenerator.getInvitation(Invitation.FRIEND_INVITATION);
        newSortedSet.add(newEventInvitation);
        newSortedSet.add(newFriendInvitation);
        newSortedSet.add(newAcceptedFriendInvitation);
        Mockito.when(newCache.getAllInvitations()).thenReturn(newSortedSet);
        ServiceContainer.setCache(newCache);
        onView(withId(R.id.action_notifications)).perform(click());
    }

    @Override
    protected void tearDown() throws Exception {
        this.getActivity().finish();
    }

    /**
     * Pressback shows main map
     */
    public void testAPressBack() {
        onView(withId(R.id.notification_activity)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    /**
     * Click on accepted invitation open show event information activity
     */
    public void testClickOnAcceptedInvitation() {
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());
        onView(withId(R.id.user_info_header)).check(matches(isDisplayed()));
    }

    /**
     * Click on event invitation open show event information activity
     */
    public void testClickOnEventInvitation() {
        // Create a real Event ?
        // onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(2).perform(click());
        // onView(withId(R.id.show_event_info_header)).check(matches(isDisplayed()));
    }

    /**
     * Click on friend invitation open invitation tabs
     */
    public void testClickOnFriendInvitation() {
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());
        onView(withId(R.id.layout_invitations_tab)).check(matches(isDisplayed()));
    }
}