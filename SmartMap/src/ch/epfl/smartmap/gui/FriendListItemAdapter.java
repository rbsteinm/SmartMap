package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;

/**
 * Customized adapter that displays a list of users in a target activity
 * This adapter dynamically creates a row in the activity for each user
 * It displays in each row: user name, user status, TODO user picture
 * 
 * @author rbsteinm
 */
public class FriendListItemAdapter extends ArrayAdapter<User> {

	private final Context mContext;
	private final List<User> mItemsArrayList;

	/**
	 * @param context
	 *            Context of the Activity where we want to display the user list
	 * @param userList
	 *            list of users to display
	 */
	public FriendListItemAdapter(Context context, List<User> itemsArrayList) {

		super(context, R.layout.gui_friend_list_item, itemsArrayList);

		mContext = context;
		mItemsArrayList = itemsArrayList;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 * callback function automatically called one time for each user in the list
	 */
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Create inflater,get item to construct
		LayoutInflater inflater = (LayoutInflater) mContext
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.gui_friend_list_item, parent,
		    false);

		// Get FriendItem fields
		TextView name = (TextView) convertView
		    .findViewById(R.id.activity_friends_name);
		TextView isOnline = (TextView) convertView
		    .findViewById(R.id.activity_friends_isOnline);
		ImageView picture = (ImageView) convertView
		    .findViewById(R.id.activity_friends_picture);

		// Set the User's ID to the tag of its View
		convertView.setTag(mItemsArrayList.get(position).getID());

		// Set fields with friend attributes
		name.setText(mItemsArrayList.get(position).getName());
		picture.setImageBitmap(mItemsArrayList.get(position).getPicture(
		    mContext));
		String status;
		if (mItemsArrayList.get(position).isOnline()) {
			status = "online";
			isOnline.setTextColor(Color.GREEN);
		} else {
			status = "offline";
		}
		isOnline.setText(status);

		return convertView;
	}
}