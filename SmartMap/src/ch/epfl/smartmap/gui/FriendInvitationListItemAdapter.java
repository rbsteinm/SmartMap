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
import ch.epfl.smartmap.cache.Invitation;

/**
 * @author marion
 */
public class FriendInvitationListItemAdapter extends ArrayAdapter<Invitation> {

    private final Context mContext;
    private final List<Invitation> mItemsArrayList;

    /**
     * @param context
     *            Context of the Activity where we want to display the user list
     * @param userList
     *            list of users to display
     */
    public FriendInvitationListItemAdapter(Context context, List<Invitation> itemsArrayList) {

        super(context, R.layout.gui_friend_invitation_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater,get item to construct
        FriendInvitationViewHolder viewHolder;
        Invitation invitation = mItemsArrayList.get(position);

        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gui_friend_invitation_list_item, parent, false);
            viewHolder = new FriendInvitationViewHolder();

            viewHolder.setUserName((TextView) convertView.findViewById(R.id.activity_friends_inviter_name));
            viewHolder
                .setPicture((ImageView) convertView.findViewById(R.id.activity_friends_inviter_picture));
            viewHolder.setUserId(invitation.getUser().getId());
            viewHolder.setInvitationId(invitation.getUser().getId());

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FriendInvitationViewHolder) convertView.getTag();
        }

        if (invitation != null) {
            viewHolder.getUserName().setText(invitation.getUser().getName());
            // viewHolder.getPicture().setImageBitmap(user.getPicture(mContext));
        }

        return convertView;
    }

    static class FriendInvitationViewHolder {
        private TextView mUserName;
        private ImageView mPicture;
        private long mUserId;
        private long mInvitationId;

        public long getInvitationId() {
            return mInvitationId;
        }

        public ImageView getPicture() {
            return mPicture;
        }

        public long getUserId() {
            return mUserId;
        }

        public TextView getUserName() {
            return mUserName;
        }

        public void setInvitationId(long invitationId) {
            mInvitationId = invitationId;
        }

        public void setPicture(ImageView picture) {
            mPicture = picture;
        }

        public void setUserId(long userId) {
            mUserId = userId;
        }

        public void setUserName(TextView name) {
            mUserName = name;
        }

    }
}
