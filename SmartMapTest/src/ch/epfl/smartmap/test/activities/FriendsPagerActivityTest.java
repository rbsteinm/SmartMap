package ch.epfl.smartmap.test.activities;

import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.activities.FriendsPagerActivity;

public class FriendsPagerActivityTest extends ActivityInstrumentationTestCase2<FriendsPagerActivity> {

    private FriendsPagerActivity mActivity;

    public FriendsPagerActivityTest() {
        super(FriendsPagerActivity.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();

    }

    public void testNumberOfViewsEqualsNumberOfFriends() {
        // TODO
    }

    public void testClickViewLeadsToFriendsInfo() {
        // TODO
    }

    public void testPressBackButton() {
        // TODO
    }

    public void testPressTopLeftIcon() {
        // TODO
    }

}
