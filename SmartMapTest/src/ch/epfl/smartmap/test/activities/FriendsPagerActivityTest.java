package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.gui.FriendsTab;
import ch.epfl.smartmap.gui.PagerAdapter;

public class FriendsPagerActivityTest extends ActivityInstrumentationTestCase2<FriendsPagerActivity> {

	private FriendsPagerActivity mActivity;
	private PagerAdapter mPagerAdapter;
	private FriendsTab mFriendsTab;
	private ListView mFriendsListView;
	private ViewPager mViewPager;
	private DatabaseHelper mCacheDB;

	public FriendsPagerActivityTest() {
		super(FriendsPagerActivity.class);
	}

	// The standard JUnit 3 setUp method run for for every test
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		// TODO find a way to retrieve the FriendsTab fragment
		mViewPager = (ViewPager) mActivity.findViewById(R.id.myViewPager);
		mPagerAdapter = (PagerAdapter) mViewPager.getAdapter();
		mFriendsTab = (FriendsTab) mPagerAdapter.getItem(0);
		mFriendsListView = mFriendsTab.getListView();
	}


	//	public void testClickViewLeadsToFriendsInfo() {
	//		// TODO
	//	}
	public void testOpenAddFriendActivity() {
		onView(withId(R.id.activity_friends_add_button)).perform(click());
		onView(withId(R.id.add_friend_activity_searchBar)).check(matches(isDisplayed()));
		pressBack();
	}

	//	public void testPressBackButton() {
	//		// TODO
	//	}

	//	public void testPressTopLeftIcon() {
	//		// TODO
	//	}

}
