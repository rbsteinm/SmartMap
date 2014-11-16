package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.smartmap.R;
import ch.epfl.smartmap.R.id;
import ch.epfl.smartmap.R.layout;
import ch.epfl.smartmap.R.menu;

import ch.epfl.smartmap.activities.AddFriendActivity;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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
		// mMockUsersList = new ArrayList<User>();
		// for (User user : MockDB.FRIENDS_LIST) {
		// mMockUsersList.add(user);
		// }
		// mMockUsersList.get(1).setOnline(true);
		// mMockUsersList.get(3).setOnline(true);
		// sortByOnline(mMockUsersList);
		// // Create custom Adapter and pass it to the Activity
		// FriendListItemAdapter adapter = new FriendListItemAdapter(mContext,
		// mMockUsersList);
		// setListAdapter(adapter);
		new DisplayInvitationsList().execute();
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
		displayAcceptFriendDialog(name, userId);
	}

	@Override
	public void onResume() {
		super.onResume();
		new DisplayInvitationsList().execute();
	}

	private void displayAcceptFriendDialog(String name, final long userId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Accept " + name + " to become your friend?");

		// Add positive button
		builder.setPositiveButton("Yes, accept",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new AcceptInvitation().execute(userId);
					}
				});

		// Add negative button
		builder.setNegativeButton("No, decline",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new DeclineInvitation().execute(userId);
					}
				});

		// display the AlertDialog
		builder.create().show();
	}

	private class AcceptInvitation extends AsyncTask<Long, Void, String> {

		@Override
		protected String doInBackground(Long... params) {
			String confirmString = "";
			try {
				NetworkSmartMapClient.getInstance().acceptInvitation(params[0]);
				new DatabaseHelper(getActivity()).addUser(NetworkSmartMapClient
						.getInstance().getUserInfo(params[0]));
				confirmString = "Accepted";
			} catch (SmartMapClientException e) {
				confirmString = "Error";
			}
			return confirmString;
		}

		@Override
		protected void onPostExecute(String confirmString) {
			// TODO use handle because must do this in main thread
			// TODO delete item from the list
			Toast.makeText(getActivity(), confirmString, Toast.LENGTH_LONG)
					.show();
		}

	}

	private class DeclineInvitation extends AsyncTask<Long, Void, String> {

		@Override
		protected String doInBackground(Long... params) {
			String confirmString = "";
			try {
				NetworkSmartMapClient.getInstance()
						.declineInvitation(params[0]);
				confirmString = "Declined";
			} catch (SmartMapClientException e) {
				confirmString = "Error";
			}
			return confirmString;
		}

		@Override
		protected void onPostExecute(String confirmString) {
			// TODO use handle because must do this in main thread
			// TODO delete item from the list
			Toast.makeText(getActivity(), confirmString, Toast.LENGTH_LONG)
					.show();
		}

	}

	private class DisplayInvitationsList extends
			AsyncTask<String, Void, List<List<User>>> {

		@Override
		protected List<List<User>> doInBackground(String... params) {
			try {

				List<List<User>> list = NetworkSmartMapClient.getInstance()
						.getInvitations();
				for (User user : list.get(1)) {
					NetworkSmartMapClient.getInstance().ackAcceptedInvitation(
							user.getID());
				}
				return list;

			} catch (SmartMapClientException e) {
				return Collections.emptyList();
			}
		}

		@Override
		protected void onPostExecute(List<List<User>> list) {
			super.onPostExecute(list);
			setListAdapter(new FriendListItemAdapter(getActivity(), list.get(0)));
			for (User newFriend : list.get(1)) {
				new DatabaseHelper(getActivity()).addUser(newFriend);
			}
		}

	}

}
