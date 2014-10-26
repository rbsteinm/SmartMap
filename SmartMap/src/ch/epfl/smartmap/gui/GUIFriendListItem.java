package ch.epfl.smartmap.gui;

import android.content.Context;
import ch.epfl.smartmap.cache.Friend;

/**
 * @author rbsteinm
 * 
 */

/**
 * this class represents an item of the leftMenu FriendList
 * 
 */
public class GUIFriendListItem {
    private Context mContext;
    private Friend mFriend;

    public GUIFriendListItem(Context context, Friend friend) {
        mContext = context;
    }
}
