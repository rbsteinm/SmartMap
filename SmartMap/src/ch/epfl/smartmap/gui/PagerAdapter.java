package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.smartmap.cache.User;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

public class PagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;
	private final Context mContext;

	public PagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
		fragments = new ArrayList<Fragment>();
		fragments.add(new FriendsTab(this.mContext));
		fragments.add(new InvitationsTab(this.mContext));
		// fragments.add(new FriendsTab());
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
