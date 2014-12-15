package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddFriendActivity;
import ch.epfl.smartmap.background.ServiceContainer;

import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * @author rbsteinm
 */
public class AddFriendActivityTest extends ActivityInstrumentationTestCase2<AddFriendActivity> {

    public AddFriendActivityTest() {
        super(AddFriendActivity.class);
    }

    // The standard JUnit 3 setUp method run for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        String token =
            "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";
        long facebookId = 1482245642055847L;
        String name = "SmartMap SwEng";
        ServiceContainer.getNetworkClient().authServer(name, facebookId, token);
    }

    public void testSearchBarDisplayed() {
        onView(ViewMatchers.withId(R.id.add_friend_activity_searchBar)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    public void testUserIsDisplayed() throws InterruptedException {
        onView(ViewMatchers.withId(R.id.add_friend_activity_searchBar)).perform(
            ViewActions.typeText("raphael steinmann"));
        Thread.sleep(5000);
        onView(ViewMatchers.withId(R.id.activity_friends_layout)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
