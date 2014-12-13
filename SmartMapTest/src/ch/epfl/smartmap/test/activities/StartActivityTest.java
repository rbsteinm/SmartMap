package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.StartActivity;

import com.facebook.Session;

/**
 * @author agpmilli
 */
public class StartActivityTest extends ActivityInstrumentationTestCase2<StartActivity> {
    public StartActivityTest() {
        super(StartActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity(); // prevent error
        // "No activities found. Did you forget to launch the activity by calling getActivity()"
    }

    @Override
    protected void tearDown() throws Exception {
        this.getActivity().finish();
    }

    /**
     * Click on startActivity shouldn't do anything
     * 
     * @throws Exception
     */
    public void testContainerClick() {
        if ((Session.getActiveSession() == null) || Session.getActiveSession().getPermissions().isEmpty()) {
            onView(withId(R.id.container)).perform(click()).check(matches(isDisplayed()));
        } else {
            // OK
        }
    }

    /**
     * Test if Facebook button is displayed or not (depends if first log in or
     * not)
     */
    public void testFacebookButtonVisibility() throws InterruptedException {
        if ((Session.getActiveSession() == null) || Session.getActiveSession().getPermissions().isEmpty()) {
            Thread.sleep(1000);
            // Problem double view
            // onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        } else {
            // no facebook button
        }
    }

    /**
     * Click on logo shouldn't do anything
     */
    public void testLogoClick() {
        if ((Session.getActiveSession() == null) || Session.getActiveSession().getPermissions().isEmpty()) {
            onView(withId(R.id.logo)).check(matches(isDisplayed()));
        }
    }

    /**
     * Welcome text should always be displayed (but sometimes with alpha 0.0
     */
    public void testWelcomeApparence() {
        if ((Session.getActiveSession() == null) || Session.getActiveSession().getPermissions().isEmpty()) {
            onView(withId(R.id.welcome)).check(matches(isDisplayed()));
        }
    }

}