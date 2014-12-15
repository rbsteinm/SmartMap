package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddFriendActivity;

import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * @author rbsteinm
 */
public class AddFriendActivityTest extends ActivityInstrumentationTestCase2<AddFriendActivity> {

    private static final String TAG = AboutActivityTest.class.getSimpleName();
    private Context mContext;

    public AddFriendActivityTest() {
        super(AddFriendActivity.class);
    }

    // The standard JUnit 3 setUp method run for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        mContext = this.getActivity().getApplicationContext();
    }

    public void testSearchBarDisplayed() {
        onView(ViewMatchers.withId(R.id.add_friend_activity_searchBar)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

}
