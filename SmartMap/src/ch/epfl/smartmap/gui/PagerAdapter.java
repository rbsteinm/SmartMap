package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author marion-S
 * 
 */
public class PagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> mFragments;
	private final Context mContext;

	public PagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new FriendsTab(this.mContext));
		mFragments.add(new InvitationsTab(this.mContext));

	}

	@Override
	public Fragment getItem(int position) {
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

}
