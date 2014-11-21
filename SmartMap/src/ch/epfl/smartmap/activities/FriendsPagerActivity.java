package ch.epfl.smartmap.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
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

    private ViewPager mPager;
    private ActionBar mActionBar;
    private final String[] mTabs = { "Friends", "Invitations" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_friends_pager);

        mPager = (ViewPager) this.findViewById(R.id.myViewPager);
        mActionBar = this.getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        PagerAdapter pageAdapter = new PagerAdapter(this, this.getSupportFragmentManager());

        mPager.setAdapter(pageAdapter);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tabName : mTabs) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
