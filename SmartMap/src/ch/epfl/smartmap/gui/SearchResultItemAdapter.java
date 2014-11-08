/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

/**
 * @author jfperren
 *
 */
public class SearchResultItemAdapter extends ArrayAdapter<Friend> {

    private final Context mContext;
    private final List<Friend> mFriendsList;

    public SearchResultItemAdapter(Context context, List<Friend> friendsList) {

        super(context, R.layout.gui_friend_list_item, friendsList);

        mContext = context;
        mFriendsList = friendsList;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return SearchResultViewFactory.getSearchResultView(mContext, mFriendsList.get(position)); 
    }
}
