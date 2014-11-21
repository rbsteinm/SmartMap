package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.activities.SettingsActivity;
import ch.epfl.smartmap.cache.SettingsManager;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * <p>
 * Tests the correctness of SettingsActivity.
 * </p>
 * <p>
 * A boolean preference that has a dependency to another boolean preference will
 * have its value set as follows: booleanValue = dependencyIsEnabled ?
 * valueSetByUser : false For example if the checkbox vibrate is checked but the
 * notifications checkbox is unchecked, the value of the vibrate checkbox will
 * be false.
 * </p>
 * <p>
 * For some reason, using withId instead of withText makes espresso crash.
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

    public void testClickingAlwaysSharePositionChangesTheSettings() {
        boolean initialValue = SettingsManager.getInstance().alwaysShare();
        if (initialValue) {
            onView(
                ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_always_share)))
                .perform(ViewActions.click());
        }
        initialValue = SettingsManager.getInstance().alwaysShare();
        assertEquals("Couldn't set the button to false", false, initialValue);
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_always_share)))
            .perform(ViewActions.click());
        boolean finalValue = SettingsManager.getInstance().alwaysShare();
        assertTrue("intitialValue was " + initialValue + " and finalValue was " + finalValue,
            initialValue != finalValue);
    }

    public void testCanChangeRefreshDataFrequency() {

        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_refresh_frequency)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("5 seconds")).perform(ViewActions.click());

        assertEquals("Couldn't set the frequency to 5 seconds", 5, SettingsManager.getInstance()
            .getRefreshFrequency());

        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_refresh_frequency)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("10 seconds (default)")).perform(ViewActions.click());

        assertEquals("Couldn't set the frequency to 10 seconds", 10, SettingsManager.getInstance()
            .getRefreshFrequency());
    }

    public void testCanChangeTimeToWaitBeforeHiding() {

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_last_seen_max)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("10 minutes")).perform(ViewActions.click());

        assertEquals("Couldn't set to 10", 10, SettingsManager.getInstance()
            .getTimeToWaitBeforeHidingFriends());

        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_last_seen_max)))
            .perform(ViewActions.click());
        onView(ViewMatchers.withText("30 minutes (default)")).perform(ViewActions.click());

        assertEquals("Couldn't set to 30", 30, SettingsManager.getInstance()
            .getTimeToWaitBeforeHidingFriends());
    }

    public void testActivateNotifications() {
        this.disableNotifications();
        boolean initValue = SettingsManager.getInstance().notificationsEnabled();
        assertEquals("Failed to disable notifications", false, initValue);

        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
            ViewActions.click());
        assertEquals("Failed to enable notifications", true, SettingsManager.getInstance()
            .notificationsEnabled());
    }

    public void testCannotActivateFriendRequestsNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_friend_requests)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, SettingsManager
            .getInstance().notificationsForFriendRequests());
    }

    public void testCannotActivateFriendshipNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_friendship_confirmations))).perform(
            ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, SettingsManager
            .getInstance().notificationsForFriendshipConfirmations());
    }

    public void testCannotActivateEventsNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_invitations)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, SettingsManager
            .getInstance().notificationsForEventInvitations());

        onView(
            ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_event_proximity)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, SettingsManager
            .getInstance().notificationsForEventProximity());
    }

    public void testCannotActivateVibrateNotificationsIfNotifDisabled() {
        this.disableNotifications();
        onView(ViewMatchers.withText(mContext.getString(ch.epfl.smartmap.R.string.pref_title_vibrate)))
            .perform(ViewActions.click());
        assertEquals("Can enable a checkbox when its dependency is disabled", false, SettingsManager
            .getInstance().notificationsVibrate());
    }

    private void disableNotifications() {
        onView(
            ViewMatchers.withText(mContext
                .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
            ViewActions.click());
        boolean initValue = SettingsManager.getInstance().notificationsEnabled();
        if (initValue) {
            onView(
                ViewMatchers.withText(mContext
                    .getString(ch.epfl.smartmap.R.string.pref_title_notifications_enabled))).perform(
                ViewActions.click());
        }
    }

}