package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;

/**
 * @author rbsteinm
 * 
 */
public class FriendListItemAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final List<User> mItemsArrayList;

    public FriendListItemAdapter(Context context, List<User> itemsArrayList) {

        super(context, R.layout.gui_friend_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater,get Friend View from the xml via Adapter
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gui_friend_list_item, parent, false);

        // Get FriendItem fields
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView isOnline = (TextView) convertView.findViewById(R.id.isOnline);

        // Set fields with friend attributes
        name.setText(mItemsArrayList.get(position).getName());
        String status = "Status: ";
        if (mItemsArrayList.get(position).isOnline()) {
            status += "online";
        } else {
            status += "offline";
        }
        isOnline.setText(status);

        return convertView;
    }
}
