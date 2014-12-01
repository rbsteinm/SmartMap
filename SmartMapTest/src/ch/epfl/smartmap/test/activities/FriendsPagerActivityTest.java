package ch.epfl.smartmap.test.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.gui.FriendsTab;
import ch.epfl.smartmap.gui.PagerAdapter;

public class FriendsPagerActivityTest extends ActivityInstrumentationTestCase2<FriendsPagerActivity> {

    private FriendsPagerActivity mActivity;
    private PagerAdapter mPagerAdapter;
    private FriendsTab mFriendsTab;
    private ListView mFriendsListView;

    public FriendsPagerActivityTest() {
        super(FriendsPagerActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        // TODO find a way to retrieve the FriendsTab fragment
        // mPagerAdapter = (PagerAdapter) mActivity.getViewPager().getAdapter();
        // mFriendsTab = (FriendsTab) mPagerAdapter.getItem(0);
        // mFriendsListView = mFriendsTab.getListView();
    }

    /*
     * public void testClickViewLeadsToFriendsInfo() {
     * onView(withId(R.id.activity_friends_layout)).perform(click());
     * onView(withId(R.id.user_info_remove_button)).check(matches(isDisplayed()));
     * pressBack();
     * }
     * public void testOpenAddFriendActivity() {
     * onView(withId(R.id.activity_friends_add_button)).perform(click());
     * onView(withId(R.id.add_friend_activity_searchBar)).check(matches(isDisplayed()));
     * pressBack();
     * }
     */

}
