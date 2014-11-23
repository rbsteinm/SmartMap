package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;

/**
 * Customized adapter that displays a list of notification in a target activity
 * This adapter dynamically creates a row in the activity for each notification
 * It displays in each row: user name, user status
 * 
 * @author agpmilli
 */
public class InvitationListItemAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final List<User> mItemsArrayList;

    /**
     * @param context
     *            Context of the Activity where we want to display the user list
     * @param userList
     *            list of users to display
     */
    public InvitationListItemAdapter(Context context, List<User> itemsArrayList) {

        super(context, R.layout.gui_notification_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     * callback function automatically called one time for each user in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater,get item to construct
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gui_notification_list_item, parent, false);

        // Get FriendItem fields
        TextView title = (TextView) convertView.findViewById(R.id.activity_notification_title);
        TextView text = (TextView) convertView.findViewById(R.id.activity_notification_text);
        ImageView picture = (ImageView) convertView.findViewById(R.id.activity_notification_picture);

        // Set the User's ID to the tag of its View
        convertView.setTag(mItemsArrayList.get(position).getID());

        // Set fields with friend attributes
        title.setText(mContext.getString(R.string.notification_invitefriend_title));
        picture.setImageBitmap(mItemsArrayList.get(position).getPicture(mContext));
        text.setText(mItemsArrayList.get(position).getName() + " "
            + mContext.getString(R.string.notification_friend_invitation));

        return convertView;
    }
}
