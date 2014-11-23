package ch.epfl.smartmap.test.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.gui.FriendsTab;

public class FriendsPagerActivityTest extends
		ActivityInstrumentationTestCase2<FriendsPagerActivity> {

	private FriendsPagerActivity mActivity;
	private FriendsTab mFriendsTab;
	private ListView mFriendsListView;
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
		// mFriendsTab = mActivity.get
		mFriendsListView = mFriendsTab.getListView();
		mCacheDB = DatabaseHelper.getInstance();
	}

	// public void testNumberOfViewsEqualsNumberOfFriends() {
	// // TODO
	// assert (mFriendsListView.getCount() == mCacheDB.getAllUsers().size());
	// }
	//
	// public void testClickViewLeadsToFriendsInfo() {
	// // TODO
	// }
	//
	// public void testPressBackButton() {
	// // TODO
	// }
	//
	// public void testPressTopLeftIcon() {
	// // TODO
	// }

}
