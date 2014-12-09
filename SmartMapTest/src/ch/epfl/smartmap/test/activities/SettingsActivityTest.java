package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import android.content.Context;
import android.preference.Preference;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.activities.SettingsActivity;
import ch.epfl.smartmap.background.ServiceContainer;

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
 * <p>
 * Note: for some reason, using withId instead of withText makes espresso crash.
 * </p>
 * 
 * @author SpicyCH
 */
public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {
    private Context mContext;

    public SettingsActivityTest() {
        super(SettingsActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        mContext = this.getActivity().getApplicationContext();
    }

    @Override
    protected void tearDown() {
        this.getActivity().finish();
    }

    public void testActivateNotifications() {
        this.disableNotifications();
        boolean initValue = ServiceContainer.getSettingsManager().notificationsEnabled();
        assertEquals("Failed to disable notifications", false, initValue);

        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
            ViewActions.click());
        assertEquals("Failed to enable notifications", true, ServiceContainer.getSettingsManager()
            .notificationsEnabled());
    }

    public void testCanActivateEventNotifications() {
        this.enableNotifications();
        assertEquals("Cannot enable notifications", true, ServiceContainer.getSettingsManager()
            .notificationsEnabled());

        boolean initValue = ServiceContainer.getSettingsManager().notificationsForEventInvitations();
        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_invitations)))
            .perform(ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for event invitations",
            initValue != ServiceContainer.getSettingsManager().notificationsForEventInvitations());

        boolean initValue2 = ServiceContainer.getSettingsManager().notificationsForEventProximity();
        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_proximity)))
            .perform(ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for event proximity",
            initValue2 != ServiceContainer.getSettingsManager().notificationsForEventProximity());

    }

    public void testCanActivateFriendRequests() {
        this.enableNotifications();
        assertEquals("Cannot enable notifications", true, ServiceContainer.getSettingsManager()
            .notificationsEnabled());

        boolean initValue = ServiceContainer.getSettingsManager().notificationsForFriendRequests();
        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_friend_requests)))
            .perform(ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for friend requests",
            initValue != ServiceContainer.getSettingsManager().notificationsForFriendRequests());

    }

    public void testCanActivateFriendshipConfirmations() {
        this.enableNotifications();
        assertEquals("Cannot enable notifications", true, ServiceContainer.getSettingsManager()
            .notificationsEnabled());

        boolean initValue = ServiceContainer.getSettingsManager().notificationsForFriendshipConfirmations();
        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_friendship_confirmations))).perform(
            ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for friendship confirmations",
            initValue != ServiceContainer.getSettingsManager().notificationsForFriendshipConfirmations());

    }

    public void testCanChangeRefreshDataFrequency() {

        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_refresh_frequency)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("5 seconds")).perform(ViewActions.click());

        assertEquals("Couldn't set the frequency to 5 seconds", 5000, ServiceContainer.getSettingsManager()
            .getRefreshFrequency());

        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_refresh_frequency)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("10 seconds (default)")).perform(ViewActions.click());

        assertEquals("Couldn't set the frequency to 10 seconds", 10000, ServiceContainer.getSettingsManager()
            .getRefreshFrequency());
    }

    public void testCanChangeTimeToWaitBeforeHiding() {

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_last_seen_max)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("10 minutes")).perform(ViewActions.click());

        assertEquals("Couldn't set to 10 minutes", 600000, ServiceContainer.getSettingsManager()
            .getTimeToWaitBeforeHidingFriends());

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_last_seen_max)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("1 hour (default)")).perform(ViewActions.click());

        assertEquals("Couldn't set to 1 hour", 3600000, ServiceContainer.getSettingsManager()
            .getTimeToWaitBeforeHidingFriends());
    }

    @SuppressWarnings("unchecked")
    public void testCanEnableVibrations() {
        this.enableNotifications();
        assertEquals("Cannot enable notifications", true, ServiceContainer.getSettingsManager()
            .notificationsEnabled());

        // Here vibrate is not on the screen, so we must use onData
        boolean initValue = ServiceContainer.getSettingsManager().notificationsVibrate();
        onData(Matchers.<Object> allOf(PreferenceMatchers.withKey("notifications_vibrate"))).perform(
            ViewActions.click());
        assertTrue("Couldn't change boolean status of notifications for vibrations",
            initValue != ServiceContainer.getSettingsManager().notificationsVibrate());
    }

    public void testCannotActivateEventsNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_invitations)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForEventInvitations());

        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_proximity)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForEventProximity());
    }

    public void testCannotActivateFriendRequestsNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_friend_requests)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForFriendRequests());
    }

    public void testCannotActivateFriendshipNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_friendship_confirmations))).perform(
            ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsForFriendshipConfirmations());
    }

    @SuppressWarnings("unchecked")
    public void testCannotActivateVibrateNotificationsIfNotifDisabled() {
        // todo use ondata to load listview
        this.disableNotifications();

        // Here vibrate is not on the screen, so we must use onData
        onData(Matchers.<Object> allOf(PreferenceMatchers.withKey("notifications_vibrate"))).perform(
            ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, ServiceContainer
            .getSettingsManager().notificationsVibrate());

    }

    @SuppressWarnings("unchecked")
    public void testCanSetPublicAndPrivateEvents() {
        onData(Matchers.<Object> allOf(withPreferenceKey("events_show_public"))).perform(ViewActions.click());
        boolean initValue = ServiceContainer.getSettingsManager().showPublicEvents();
        onData(Matchers.<Object> allOf(withPreferenceKey("events_show_public"))).perform(ViewActions.click());

        assertTrue("Couldn't change boolean status of public events", initValue != ServiceContainer
            .getSettingsManager().showPublicEvents());

        /*
         * Private events are disable due to deadline not letting us the time to
         * implement them.
         * onData(Matchers.<Object>
         * allOf(PreferenceMatchers.withKey("events_show_private"))).perform(
         * ViewActions.click());
         * boolean initValue2 =
         * ServiceContainer.getSettingsManager().showPrivateEvents();
         * onData(Matchers.<Object>
         * allOf(PreferenceMatchers.withKey("events_show_private"))).perform(
         * ViewActions.click());
         * assertTrue("Couldn't change boolean status of private events",
         * initValue2 != SettingsManager
         * .getInstance().showPrivateEvents());
         */
    }

    public void testClickingOfflineChangesSettings() {
        boolean initialValue = ServiceContainer.getSettingsManager().isOffline();
        if (initialValue) {
            onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_offline)))
                .perform(ViewActions.click());
        }
        initialValue = ServiceContainer.getSettingsManager().isOffline();
        assertEquals("Couldn't set the button to false", false, initialValue);
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_offline)))
            .perform(ViewActions.click());
        boolean finalValue = ServiceContainer.getSettingsManager().isOffline();
        assertTrue("intitialValue was " + initialValue + " and finalValue was " + finalValue,
            initialValue != finalValue);
    }

    public void testInit() {
        assertTrue(true);
    }

    private void disableNotifications() {
        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
            ViewActions.click());
        boolean initValue = ServiceContainer.getSettingsManager().notificationsEnabled();
        if (initValue) {
            onView(
                ViewMatchers.withText(mContext
                    .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
                ViewActions.click());
        }
    }

    private void enableNotifications() {
        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
            ViewActions.click());
        boolean initValue = ServiceContainer.getSettingsManager().notificationsEnabled();
        if (!initValue) {
            onView(
                ViewMatchers.withText(mContext
                    .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
                ViewActions.click());
        }
    }

    public static Matcher<Object> withPreferenceKey(final Matcher<Preference> preferenceMatcher) {
        checkNotNull(preferenceMatcher);
        return new BoundedMatcher<Object, Preference>(Preference.class) {
            @Override
            protected boolean matchesSafely(Preference pref) {
                return preferenceMatcher.matches(pref);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with preference key:");
                preferenceMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<Object> withPreferenceKey(String expectedKeyText) {
        checkNotNull(expectedKeyText);
        checkArgument(!expectedKeyText.isEmpty());
        return withPreferenceKey(PreferenceMatchers.withKey(expectedKeyText));
    }

}