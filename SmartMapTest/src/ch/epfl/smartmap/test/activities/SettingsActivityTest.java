package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import android.content.Context;
import android.preference.Preference;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.activities.SettingsActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.matcher.BoundedMatcher;
import com.google.android.apps.common.testing.ui.espresso.matcher.PreferenceMatchers;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * <p>
 * Tests the correctness of SettingsActivity.
 * </p>
 * <p>
 * A boolean preference that has a dependency to another boolean preference will
 * have its value set as follows: booleanValue = dependencyIsEnabled ?
 * valueSetByUser : false
 * </p>
 * <p>
 * For example if the checkbox vibrate is checked but the notifications checkbox
 * is unchecked, the value of the vibrate checkbox will be false.
 * </p>
 * 
 * @author SpicyCH
 */
public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {
    public static Matcher<Object> withPreferenceKey(final Matcher<Preference> preferenceMatcher) {
        checkNotNull(preferenceMatcher);
        return new BoundedMatcher<Object, Preference>(Preference.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with preference key:");
                preferenceMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(Preference pref) {
                return preferenceMatcher.matches(pref);
            }
        };
    }

    public static Matcher<Object> withPreferenceKey(String expectedKeyText) {
        checkNotNull(expectedKeyText);
        checkArgument(!expectedKeyText.isEmpty());
        return withPreferenceKey(PreferenceMatchers.withKey(expectedKeyText));
    }

    private Context mContext;

    public SettingsActivityTest() {
        super(SettingsActivity.class);
    }

    private void disableNotifications() {
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled)))
            .perform(ViewActions.click());
        boolean initValue = ServiceContainer.getSettingsManager().notificationsEnabled();
        if (initValue) {
            onView(
                ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled)))
                .perform(ViewActions.click());
        }
    }

    private void enableNotifications() {
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled)))
            .perform(ViewActions.click());
        boolean initValue = ServiceContainer.getSettingsManager().notificationsEnabled();
        if (!initValue) {
            onView(
                ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled)))
                .perform(ViewActions.click());
        }
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        mContext = this.getActivity().getApplicationContext();
        ServiceContainer.setSettingsManager(new SettingsManager(this.getActivity()));
    }

    public void testActivateNotifications() {
        this.disableNotifications();
        boolean initValue = ServiceContainer.getSettingsManager().notificationsEnabled();
        assertEquals("Failed to disable notifications", false, initValue);

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled)))
            .perform(ViewActions.click());
        assertEquals("Failed to enable notifications", true, ServiceContainer.getSettingsManager()
            .notificationsEnabled());
    }

    public void testCanActivateEventNotifications() {
        this.enableNotifications();
        assertEquals("Cannot enable notifications", true, ServiceContainer.getSettingsManager().notificationsEnabled());

        boolean initValue = ServiceContainer.getSettingsManager().notificationsForEventInvitations();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_invitations)))
            .perform(ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for event invitations",
            initValue != ServiceContainer.getSettingsManager().notificationsForEventInvitations());

        boolean initValue2 = ServiceContainer.getSettingsManager().notificationsForEventProximity();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_proximity)))
            .perform(ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for event proximity",
            initValue2 != ServiceContainer.getSettingsManager().notificationsForEventProximity());

    }

    public void testCanActivateFriendRequests() {
        this.enableNotifications();
        assertEquals("Cannot enable notifications", true, ServiceContainer.getSettingsManager().notificationsEnabled());

        boolean initValue = ServiceContainer.getSettingsManager().notificationsForFriendRequests();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_friend_requests)))
            .perform(ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for friend requests", initValue != ServiceContainer
            .getSettingsManager().notificationsForFriendRequests());

    }

    public void testCanActivateFriendshipConfirmations() {
        this.enableNotifications();
        assertEquals("Cannot enable notifications", true, ServiceContainer.getSettingsManager().notificationsEnabled());

        boolean initValue = ServiceContainer.getSettingsManager().notificationsForFriendshipConfirmations();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_friendship_confirmations)))
            .perform(ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for friendship confirmations",
            initValue != ServiceContainer.getSettingsManager().notificationsForFriendshipConfirmations());

    }

    public void testCanChangeRefreshDataFrequency() {

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_refresh_frequency)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("5 seconds")).perform(ViewActions.click());

        assertEquals("Couldn't set the frequency to 5 seconds", 5000, ServiceContainer.getSettingsManager()
            .getRefreshFrequency());

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_refresh_frequency)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("10 seconds (default)")).perform(ViewActions.click());

        assertEquals("Couldn't set the frequency to 10 seconds", 10000, ServiceContainer.getSettingsManager()
            .getRefreshFrequency());
    }

    public void testCanChangeTimeToWaitBeforeHiding() {

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_last_seen_max))).perform(
            ViewActions.click());
        onView(ViewMatchers.withText("10 minutes")).perform(ViewActions.click());

        assertEquals("Couldn't set to 10 minutes", 600000, ServiceContainer.getSettingsManager()
            .getTimeToWaitBeforeHidingFriends());

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_last_seen_max))).perform(
            ViewActions.click());
        onView(ViewMatchers.withText("1 hour (default)")).perform(ViewActions.click());

        assertEquals("Couldn't set to 1 hour", 3600000, ServiceContainer.getSettingsManager()
            .getTimeToWaitBeforeHidingFriends());
    }

    public void testCannotActivateEventsNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_invitations)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForEventInvitations());

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_proximity)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForEventProximity());
    }

    public void testCannotActivateFriendRequestsNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_friend_requests)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForFriendRequests());
    }

    public void testCannotActivateFriendshipNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_friendship_confirmations)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForFriendshipConfirmations());
    }

}