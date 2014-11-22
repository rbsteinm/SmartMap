package ch.epfl.smartmap.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.Notifications;

/**
 * Customized adapter that displays a list of Views in the side drawer menu
 * ListView
 * 
 * @author rbsteinm
 */
public class SideMenuAdapter extends ArrayAdapter<String> {

	private final Context mContext;
	private final String[] mListItems;
	private static final int INDEX_PROFILE = 0;
	private static final int INDEX_FRIENDS = 1;
	private static final int INDEX_EVENTS = 2;
	private static final int INDEX_FILTERS = 3;
	private static final int INDEX_SETTINGS = 4;

	/**
	 * @param context
	 *            Context of the Activity where we want to display the user list
	 * @param listItems
	 *            list of String to display in the side menu
	 */
	public SideMenuAdapter(Context context, String[] listItems) {
		super(context, R.layout.drawer_list_item, listItems);
		mContext = context;
		mListItems = listItems;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Create inflater,get item to construct
		LayoutInflater inflater = (LayoutInflater) mContext
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater
		    .inflate(R.layout.drawer_list_item, parent, false);

		// Get text view field
		TextView sideMenuTextView = (TextView) convertView
		    .findViewById(R.id.side_menu_text_view);

		// Get image view field
		TextView sideMenuImageView = (TextView) convertView
		    .findViewById(R.id.side_menu_img_view);

		// Set item field + id
		sideMenuTextView.setText(mListItems[position]);
		// Set tag to each View
		sideMenuTextView.setTag("side_menu_tag_" + position);

		// Put the number of current notification on several menus
		if (position == INDEX_PROFILE) {
			sideMenuImageView.setBackgroundResource(0);
		}

		if (position == INDEX_FRIENDS) {
			if (Notifications.getNumberOfFriendNotification() == 0) {
				sideMenuImageView.setBackgroundResource(0);
			} else {
				sideMenuImageView
				    .setBackgroundResource(R.drawable.red_circle_notification);
				sideMenuImageView.setText(""
				    + (Notifications.getNumberOfFriendNotification()));
			}
		}

		if (position == INDEX_EVENTS) {
			if (Notifications.getNumberOfEventNotification() == 0) {
				sideMenuImageView.setBackgroundResource(0);
			} else {
				sideMenuImageView
				    .setBackgroundResource(R.drawable.red_circle_notification);
				sideMenuImageView.setText(""
				    + (Notifications.getNumberOfEventNotification()));
			}
		}
		if (position == INDEX_FILTERS) {
			sideMenuImageView.setBackgroundResource(0);
		}

		if (position == INDEX_SETTINGS) {
			sideMenuImageView.setBackgroundResource(0);
		}

		return convertView;
	}
}
