package ch.epfl.smartmap.gui;

import java.util.GregorianCalendar;
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
 * Displays each user in a row. The friend items are clickable to signify they are invited. Inspired by the adapter done
 * by Raphaël.
 * 
 * 
 * @author SpicyCH
 */
public class FriendPickerListAdapter extends ArrayAdapter<User> {

    private static final String TAG = FriendPickerListAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<User> mItemsArrayList;

    /**
     * @param context
     *            Context of the Activity where we want to display the user list
     * @param userList
     *            list of users to display
     */
    public FriendPickerListAdapter(Context context, List<User> userList) {

        super(context, R.layout.gui_friend_list_item, userList);

        mContext = context;
        mItemsArrayList = userList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup) callback function
     * automatically called one time for each user in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO use ViewHolder pattern

        // Create inflater,get item to construct
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gui_friend_list_item, parent, false);

        // Get FriendItem fields
        TextView name = (TextView) convertView.findViewById(R.id.activity_friends_name);
        TextView lastSeen = (TextView) convertView.findViewById(R.id.activity_friends_lastSeen);
        ImageView picture = (ImageView) convertView.findViewById(R.id.activity_friends_picture);

        // Set the User's ID to the tag of its View
        convertView.setTag(mItemsArrayList.get(position).getID());

        // Set fields with friend attributes
        User user = mItemsArrayList.get(position);
        name.setText(user.getName());
        picture.setImageBitmap(mItemsArrayList.get(position).getPicture(mContext));

        // build String "Last seen d/m/y at hour/min"
        String lastSeenString = "Last seen " + user.getLastSeen().get(GregorianCalendar.DAY_OF_MONTH) + "/"
                + (user.getLastSeen().get(GregorianCalendar.MONTH) + 1) + "/"
                + user.getLastSeen().get(GregorianCalendar.YEAR) + " at "
                + user.getLastSeen().get(GregorianCalendar.HOUR) + ":"
                + user.getLastSeen().get(GregorianCalendar.MINUTE);

        lastSeen.setText(lastSeenString);
        lastSeen.setTextColor(Color.GRAY);

        /*
         * if ((mPositionsSelected.get(position) != null) && mPositionsSelected.get(position)) { // This item is
         * selected convertView.setBackgroundColor(color.background_blue); } else {
         * convertView.setBackgroundColor(color.main_grey); }
         */

        return convertView;
    }

    public void onItemClick() {

    }
}
