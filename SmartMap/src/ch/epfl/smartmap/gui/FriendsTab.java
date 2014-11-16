package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.User;

/**
 * Fragment displaying your friends
 * @author rbsteinm
 *
 */
public class FriendsTab extends ListFragment {
	private List<User> mMockUsersList;
	private Context mContext;

	public FriendsTab(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.list_fragment_friends_tab,
				container, false);
		mMockUsersList = new ArrayList<User>();
		for (User user : MockDB.FRIENDS_LIST) {
			mMockUsersList.add(user);
		}
		mMockUsersList.get(1).setOnline(true);
		mMockUsersList.get(3).setOnline(true);
		sortByOnline(mMockUsersList);

		// Create custom Adapter and pass it to the Activity
		FriendListItemAdapter adapter = new FriendListItemAdapter(mContext,
				mMockUsersList);
		setListAdapter(adapter);

		return view;
	}

	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		long userId = (Long) view.getTag();
		RelativeLayout rl = (RelativeLayout) view;
		TextView tv = (TextView) rl.getChildAt(1);
		assert (tv instanceof TextView)
				&& (tv.getId() == R.id.activity_friends_name);
		String name = tv.getText().toString();
		displayDeleteConfirmationDialog(name, userId);
	}

	private void sortByOnline(List<User> userList) {
		Collections.sort(userList, new Comparator<User>() {

			@Override
			public int compare(User user1, User user2) {
				return Boolean.compare(user2.isOnline(), user1.isOnline());
			}
		});
	}

	private void displayDeleteConfirmationDialog(String name, long userId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("remove " + name + " from your friends?");

		// Add positive button
		builder.setPositiveButton("Remove",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						/*
						 * try { //TODO move this request in an asynch task, =>
						 * interface? ask Marion
						 * NetworkSmartMapClient.getInstance().inviteFriend(id);
						 * } catch (SmartMapClientException e) {
						 * e.printStackTrace(); }
						 */
					}
				});

		// Add negative button
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// display the AlertDialog
		builder.create().show();
	}

}
