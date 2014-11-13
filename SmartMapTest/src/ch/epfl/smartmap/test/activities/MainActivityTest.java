package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isAssignableFrom;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withTagValue;
<<<<<<< HEAD
import static org.hamcrest.Matchers.is;
=======
>>>>>>> dev
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matcher;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;

import com.google.android.apps.common.testing.ui.espresso.UiController;
import com.google.android.apps.common.testing.ui.espresso.ViewAction;

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
    
    private static ViewAction actionOpenDrawer() {
        return new ViewAction() {
          @Override
          public Matcher<View> getConstraints() {
            return isAssignableFrom(DrawerLayout.class);
          }

          @Override
          public String getDescription() {
            return "open drawer";
          }

          @Override
          public void perform(UiController uiController, View view) {
            ((DrawerLayout) view).openDrawer(GravityCompat.START);
          }
        };
      }
      private static ViewAction actionCloseDrawer() {
        return new ViewAction() {
          @Override
          public Matcher<View> getConstraints() {
            return isAssignableFrom(DrawerLayout.class);
          }

          @Override
          public String getDescription() {
            return "close drawer";
          }

          @Override
          public void perform(UiController uiController, View view) {
            ((DrawerLayout) view).closeDrawer(GravityCompat.START);
          }
        };
      }

    public void testOpenSearchPanel() throws Exception {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_panel)).check(matches(isDisplayed()));
    }

    public void testOpenAndCloseSearchPanel() throws Exception {
        onView(withId(R.id.action_search)).perform(click());
        pressBack();
        onView(withId(R.id.search_panel)).check(matches(not(isDisplayed())));
    }
    
    public void testOpenSideMenu() throws Exception{
        onView(withId(R.id.left_drawer)).perform(actionOpenDrawer());
        onView(withId(R.id.left_drawer)).check(matches(isDisplayed()));
    }
    
    public void testOpenAndCloseSideMenu() throws Exception{
        onView(withId(R.id.left_drawer)).perform(actionOpenDrawer());
        onView(withId(R.id.left_drawer)).perform(actionCloseDrawer());
        onView(withId(R.id.left_drawer)).check(matches(not(isDisplayed())));
    }
    
    public void testOpenFriendsActivity() throws Exception{
        onView(withTagValue(is((Object) "left_menu_tag_2"))).perform(click());
        //TODO check that FriendsActivity is the current Activity
    }
    
    public void testSideMenuViewExist() throws Exception{
        //TODO check that all the views in the side menu exist
    }
}