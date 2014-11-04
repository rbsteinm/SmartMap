package ch.epfl.smartmap.test.login;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.gui.StartActivity;

public class StartActivityTest extends
    ActivityInstrumentationTestCase2<StartActivity> {
    public StartActivityTest() {
        super(StartActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity(); // prevent error
                       // "No activities found. Did you forget to launch the activity by calling getActivity()"
    }

    public void testLogoClick() throws Exception {
        onView(withId(R.id.logo)).perform(click());
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
    }
}