package ch.epfl.smartmap.test.gui;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.SeekBar;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.gui.ShowEventsActivity;

import com.google.android.apps.common.testing.ui.espresso.UiController;
import com.google.android.apps.common.testing.ui.espresso.ViewAction;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

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

    public void testSeekBarChangesKilometersShown() {
        onView(withId(R.id.showEventSeekBar)).perform(setProgress(10));
        onView(withId(R.id.showEventKilometers)).check(matches(ViewMatchers.withText("10 km")));

        onView(withId(R.id.showEventSeekBar)).perform(setProgress(15));
        onView(withId(R.id.showEventKilometers)).check(matches(ViewMatchers.withText("15 km")));
    }

    public void testSeekBarCannotGoToZero() {
        onView(withId(R.id.showEventSeekBar)).perform(setProgress(0));
        onView(withId(R.id.showEventKilometers)).check(matches(not(ViewMatchers.withText("0 km"))));
    }

    // from stackoverflow
    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((SeekBar) view).setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress";
            }

            @Override
            public org.hamcrest.Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }


}