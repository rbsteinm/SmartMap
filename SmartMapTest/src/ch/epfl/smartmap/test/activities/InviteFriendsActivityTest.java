package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.app.ListActivity;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.InviteFriendsActivity;
import ch.epfl.smartmap.background.ServiceContainer;

import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

/**
 * @author SpicyCH
 */
public class InviteFriendsActivityTest extends ActivityInstrumentationTestCase2<InviteFriendsActivity> {

    private ListActivity mActivity;

    /**
     * Constructor
     * 
     * @param activityClass
     */
    public InviteFriendsActivityTest() {
        super(InviteFriendsActivity.class);
        /*
         * Set<User> users = new HashSet<User>(); users.add(arg0)
         * Cache mockCache = Mockito.mock(Cache.class);
         * Mockito.when(mockCache.getAllFriends()).thenReturn(value);
         */
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.getActivity();
        mActivity = this.getActivity();
    }

    public void testFriendsDisplayed() {
        int friendsSize = mActivity.getListAdapter().getCount();

        assertEquals(friendsSize, ServiceContainer.getCache().getAllFriends().size());
    }

    public void testInviteDisplayed() {
        onView(withId(R.id.invite_friend_send_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
