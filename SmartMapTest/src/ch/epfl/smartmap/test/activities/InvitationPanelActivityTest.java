package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

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
        Invitation newFriendInvitation = MockGenerator.getInvitation(Invitation.FRIEND_INVITATION);
        newSortedSet.add(newEventInvitation);
        newSortedSet.add(newFriendInvitation);
        Mockito.when(newCache.getAllInvitations()).thenReturn(newSortedSet);
        ServiceContainer.setCache(newCache);
        onView(withId(R.id.action_notifications)).perform(click());
    }

    public void testClickOnInvitation() {
        onView(withId(R.id.myViewPager)).perform(click());
        // onView(withId(R.id.myViewPager)).check(matches(isDisplayed()));
    }

    public void testPressBack() {
        onView(withId(R.id.notification_activity)).check(matches(isDisplayed()));
        pressBack();
        // onView(withId(R.id.frame_layout_main)).check(matches(isDisplayed()));
    }
}