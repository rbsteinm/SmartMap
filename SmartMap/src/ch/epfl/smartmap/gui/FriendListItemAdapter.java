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
 * Customized adapter that displays a list of users in a target activity This
 * adapter dynamically creates a row in the activity for each user It displays
 * in each row: user name, user status, user picture
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
     * android.view.ViewGroup) callback function automatically called one time
     * for each user in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater,get item to construct
        FriendViewHolder viewHolder;
        User user = mItemsArrayList.get(position);

        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gui_friend_list_item, parent, false);
            viewHolder = new FriendViewHolder();

            viewHolder.setName((TextView) convertView.findViewById(R.id.activity_friends_name));
            viewHolder.setLastSeen((TextView) convertView.findViewById(R.id.activity_friends_lastSeen));
            viewHolder.setPicture((ImageView) convertView.findViewById(R.id.activity_friends_picture));
            viewHolder.setUserId(user.getId());

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FriendViewHolder) convertView.getTag();
        }

        // Set fields with user attributes
        if (user != null) {
            viewHolder.getName().setText(user.getName());
            viewHolder.getPicture().setImageBitmap(user.getImage());
            viewHolder.getLastSeen().setText(user.getSubtitle());
        }

        return convertView;
    }

    /**
     * @author rbsteinm
     *         ViewHolder pattern implementation for smoother scrolling
     *         in lists populated by {@link ch.epfl.smartmap.gui.FriendListItemAdapter}
     */
    public static class FriendViewHolder {
        private TextView mName;
        private TextView mLastSeen;
        private ImageView mPicture;
        private long mUserId;

        public TextView getName() {
            return mName;
        }

        public TextView getLastSeen() {
            return mLastSeen;
        }

        public ImageView getPicture() {
            return mPicture;
        }

        public long getUserId() {
            return mUserId;
        }

        public void setName(TextView name) {
            mName = name;
        }

        public void setLastSeen(TextView lastSeen) {
            mLastSeen = lastSeen;
        }

        public void setPicture(ImageView picture) {
            mPicture = picture;
        }

        public void setUserId(long userId) {
            mUserId = userId;
        }
    }
}