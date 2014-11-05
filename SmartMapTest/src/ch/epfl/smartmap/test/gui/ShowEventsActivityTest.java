package ch.epfl.smartmap.test.gui;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static org.hamcrest.Matchers.not;

import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.gui.ShowEventsActivity;

public class ShowEventsActivityTest extends ActivityInstrumentationTestCase2<ShowEventsActivity> {
    public ShowEventsActivityTest() {
        super(ShowEventsActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testSeekBarDisabledByDefault() {
        onView(withId(R.id.showEventSeekBar)).check(matches(not(isEnabled())));
    }

}