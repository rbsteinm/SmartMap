package ch.epfl.smartmap.test.gui;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.hasFocus;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.gui.MainActivity;

public class MainActivityTest extends
    ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity(); // prevent error
                       // "No activities found. Did you forget to launch the activity by calling getActivity()"
    }

    public void testOpenSearchPanel() throws Exception {
        onView(withId(R.id.searchButton)).perform(click());
        onView(withId(R.id.searchLayout)).check(matches(isDisplayed()));
    }

    public void testOpenAndCloseSearchPanel() throws Exception {
        onView(withId(R.id.searchButton)).perform(click());
        pressBack();
        onView(withId(R.id.searchLayout)).check(matches(not(isDisplayed())));
    }

    public void testSearchBarFocus() throws Exception {
        onView(withId(R.id.searchButton)).perform(click());
        // Should not have focus at first
        onView(withId(R.id.searchBar)).check(matches(not(hasFocus())));
        onView(withId(R.id.searchBar)).perform(click());
        // Get focus after being clicked
        onView(withId(R.id.searchBar)).check(matches(hasFocus()));
        onView(withId(R.id.searchLayout)).perform(click());
        // Lose focus when clicked outside
        onView(withId(R.id.searchBar)).check(matches(not(hasFocus())));
    }
}