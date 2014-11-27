package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.listeners.OnFriendListUpdateListener;

/**
 * Fragment displaying your friends in FriendsActivity
 * 
 * @author rbsteinm
 */
public class FriendsTab extends ListFragment implements OnFriendListUpdateListener {
	private List<User> mFriendList;

	private final Context mContext;
	private DatabaseHelper mCacheDB;

	public FriendsTab(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.list_fragment_friends_tab, container, false);
		mCacheDB = DatabaseHelper.getInstance();
		mFriendList = new ArrayList<User>();
		mFriendList = mCacheDB.getAllFriends();

		// Create custom Adapter and pass it to the Activity
		FriendListItemAdapter adapter = new FriendListItemAdapter(mContext, mFriendList);
		this.setListAdapter(adapter);

		// Initialize the listener
		mCacheDB.addOnFriendListUpdateListener(this);

		return view;
	}

	@Override
	public void onFriendListUpdate() {
		mFriendList = mCacheDB.getAllFriends();
		this.setListAdapter(new FriendListItemAdapter(mContext, mFriendList));
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		User user = mFriendList.get(position);
		RelativeLayout rl = (RelativeLayout) view;
		TextView tv = (TextView) rl.getChildAt(1);
		assert (tv instanceof TextView) && (tv.getId() == R.id.activity_friends_name);
		Intent intent = new Intent(mContext, UserInformationActivity.class);
		intent.putExtra("USER", user);
		this.startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		this.setListAdapter(new FriendListItemAdapter(mContext, mCacheDB.getAllFriends()));
	}

}