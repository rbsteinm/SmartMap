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
 * Customized adapter that displays a list of invitation in a target activity
 * This adapter dynamically creates a row in the activity for each invitation
 * It displays in each row: a title, a picture and a text
 * 
 * @author agpmilli
 */
public class InvitationListItemAdapter extends ArrayAdapter<Invitation> {
    private final Context mContext;
    private final List<Invitation> mItemsArrayList;

    /**
     * @param context
     *            Context of the Activity where we want to display the user list
     * @param userList
     *            list of users to display
     */
    public InvitationListItemAdapter(Context context, List<Invitation> itemsArrayList) {

        super(context, R.layout.gui_notification_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater,get item to construct
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gui_notification_list_item, parent, false);

        // Get FriendItem fields
        TextView title = (TextView) convertView.findViewById(R.id.activity_notification_title);
        TextView text = (TextView) convertView.findViewById(R.id.activity_notification_text);
        ImageView image = (ImageView) convertView.findViewById(R.id.activity_notification_picture);

        // Set the User's ID to the tag of its View
        convertView.setTag(mItemsArrayList.get(position).getId());

        title.setText(mItemsArrayList.get(position).getTitle());
        text.setText(mItemsArrayList.get(position).getTitle());
        image.setImageBitmap(mItemsArrayList.get(position).getUser().getImage());

        return convertView;
    }
}
