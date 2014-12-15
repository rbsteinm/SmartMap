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
 * Displays each user in a row. The friend items are clickable to signify they
 * are invited. Use ViewHolder
 * pattern as in {@link ch.epfl.smartmap.gui.EventsListItemAdapter}.
 * 
 * @author SpicyCH
 * @author agpmilli
 */
public class FriendPickerListAdapter extends ArrayAdapter<User> {

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder;
        User user = this.getItem(position);

        View newConvertView;

        if (convertView == null) {
            newConvertView = inflater.inflate(R.layout.gui_select_friend_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mName = (TextView) newConvertView.findViewById(R.id.activity_friends_name);
            viewHolder.mPicture = (ImageView) newConvertView.findViewById(R.id.activity_friends_picture);
        } else {
            newConvertView = convertView;
            viewHolder = (ViewHolder) newConvertView.getTag();
        }

        viewHolder.mName.setText(user.getName());
        viewHolder.mPicture.setImageBitmap(user.getActionImage());
        viewHolder.setId(user.getId());
        newConvertView.setTag(viewHolder);

        return newConvertView;
    }

    /**
     * A <code>ViewHolder</code> to store views and avoid useless
     * findViewById().
     * 
     * @see <a
     *      href="http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder">
     *      developer.android</a>
     */
    public static class ViewHolder {
        private TextView mName;
        private ImageView mPicture;
        private long mId;

        public long getId() {
            return mId;
        }

        public void setId(long newId) {
            mId = newId;
        }
    }
}