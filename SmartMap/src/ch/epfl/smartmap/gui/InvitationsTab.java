package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.smartmap.R;
import ch.epfl.smartmap.R.id;
import ch.epfl.smartmap.R.layout;
import ch.epfl.smartmap.R.menu;

import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.ListFragment;

public class InvitationsTab extends ListFragment {

	private List<User> mMockUsersList;
	Context mContext;

	public InvitationsTab(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.list_fragment_invitations_tab,
				container, false);
		mMockUsersList = new ArrayList<User>();
		for (User user : MockDB.FRIENDS_LIST) {
			mMockUsersList.add(user);
		}
		mMockUsersList.get(1).setOnline(true);
		mMockUsersList.get(3).setOnline(true);
		sortByOnline(mMockUsersList);
		// Create custom Adapter and pass it to the Activity
		InvitationListItemAdapter adapter = new InvitationListItemAdapter(
				mContext, mMockUsersList);
		setListAdapter(adapter);
		return view;
	}

	private void sortByOnline(List<User> userList) {
		Collections.sort(userList, new Comparator<User>() {

			@Override
			public int compare(User user1, User user2) {
				return Boolean.compare(user2.isOnline(), user1.isOnline());
			}
		});
	}

}
