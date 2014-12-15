package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ch.epfl.smartmap.activities.FriendsPagerActivity;

/**
 * A FragmentPagerAdapter to display two tabs in {@link FriendsPagerActivity} : a tab to display the friends,
 * and a tab to display the received invitations
 * 
 * @author marion-S
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments;
    private final Context mContext;

    /**
     * Constructor
     * 
     * @param context
     * @param fm
     */
    public PagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<Fragment>();
        mFragments.add(new FriendsTab(mContext));
        mFragments.add(new InvitationsTab(mContext));

    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return mFragments.size();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

}
