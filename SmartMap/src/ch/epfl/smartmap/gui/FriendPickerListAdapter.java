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
 * Displays each user in a row. The friend items are clickable to signify they are invited. Use ViewHolder pattern as in
 * {@link ch.epfl.smartmap.gui.EventsListItemAdapter}.
 * 
 * @author SpicyCH
 */
public class FriendPickerListAdapter extends ArrayAdapter<User> {

    @SuppressWarnings("unused")
    private static final String TAG = FriendPickerListAdapter.class.getSimpleName();
    private final Context mContext;

    /**
     * @param context
     *            Context of the Activity where we want to display the user list
     * @param userList
     *            list of users to display
     */
    public FriendPickerListAdapter(Context context, List<User> userList) {

        super(context, R.layout.gui_friend_list_item, userList);

        mContext = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup) callback function
     * automatically called one time for each user in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder;
        User user = this.getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gui_select_friend_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.activity_friends_name);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.activity_friends_picture);
            viewHolder.id = user.getId();

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(user.getName());
        viewHolder.picture.setImageBitmap(user.getImage());

        return convertView;
    }

    public static class ViewHolder {
        TextView name;
        ImageView picture;
        long id;
    }
}