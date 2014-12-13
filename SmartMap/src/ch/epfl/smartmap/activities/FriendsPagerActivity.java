package ch.epfl.smartmap.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.gui.PagerAdapter;

/**
 * This activity displays your friends in one tab, and your friend request (both
 * sent and received) in another tab
 * 
 * @author rbsteinm
 */
public class FriendsPagerActivity extends FragmentActivity implements ActionBar.TabListener {

    @SuppressWarnings("unused")
    private static final String TAG = FriendsPagerActivity.class.getSimpleName();

    private ViewPager mPager;
    private ActionBar mActionBar;
    private final static String[] TABS = {"Friends", "Invitations"};
    private final static int INVITATION_INDEX = 1;

    public ViewPager getViewPager() {
        return mPager;
    }

    @Override
    public void onBackPressed() {
        this.onNotificationOpen();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_friends_pager);

        mActionBar = this.getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Set action bar and tab color to main color
        mActionBar.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
        mActionBar.setStackedBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.main_blue)));

        mPager = (ViewPager) this.findViewById(R.id.myViewPager);
        PagerAdapter pageAdapter = new PagerAdapter(this, this.getSupportFragmentManager());
        mPager.setAdapter(pageAdapter);

        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tabName : TABS) {
            mActionBar.addTab(mActionBar.newTab().setText(tabName).setTabListener(this));
        }

        /**
         * on swiping, the viewpager makes respective tab selected
         */
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                mActionBar.setSelectedNavigationItem(position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.pager, menu);
        return true;
    }

    /**
     * When this tab is open by a notification
     */
    private void onNotificationOpen() {
        if (this.getIntent().getBooleanExtra("NOTIFICATION", false)) {
            this.startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case R.id.activity_friends_add_button:
                this.startAddFriendActivity(null);
                break;
            case android.R.id.home:
                this.onNotificationOpen();
                this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.getIntent().getBooleanExtra("INVITATION", false) == true) {
            mPager.setCurrentItem(INVITATION_INDEX);
        }
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        // nothing
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction arg1) {
        // on tab selected
        // show respected fragment view
        mPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
        // nothing

    }

    public void startAddFriendActivity(MenuItem menu) {
        Intent displayActivityIntent = new Intent(this, AddFriendActivity.class);
        this.startActivity(displayActivityIntent);
    }

}
