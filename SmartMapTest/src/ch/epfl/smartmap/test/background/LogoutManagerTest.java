package ch.epfl.smartmap.test.background;

import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;
import ch.epfl.smartmap.background.LogoutManager;
import ch.epfl.smartmap.background.ServiceContainer;

import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * Test the logout feature.
 *
 * @author SpicyCH
 */
public class LogoutManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public LogoutManagerTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() {
        ServiceContainer.initSmartMapServices(this.getActivity());
        LogoutManager.initialize(this.getActivity());

    }

    public void testCanDisplayLogoutDialog() throws InterruptedException {
        Espresso.onView(ViewMatchers.withText(R.string.app_name)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText(R.string.logout_text)).perform(ViewActions.click());
        Thread.sleep(2000);
    }
}
