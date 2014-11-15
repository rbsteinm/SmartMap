package ch.epfl.smartmap.gui;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;

/**
 * Customized adapter that displays a list of Invitations from Users in a target activity
 * This adapter dynamically creates a row in the activity for each user invitation
 * It displays in each row: inviting user name, user status, TODO user picture
 * @author 
 */
public class InvitationListItemAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final List<User> mItemsArrayList;
    
    /**
     * @param context Context of the Activity where we want to display the user list
     * @param userList list of inviting users to display
     */
    public InvitationListItemAdapter(Context context, List<User> itemsArrayList) {

        super(context, R.layout.gui_invitation_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
    }
    
    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     * callback function automatically called one time for each user in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater,get item to construct
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gui_invitation_list_item, parent, false);

        // Get FriendItem fields
        TextView name = (TextView) convertView.findViewById(R.id.activity_invitations_name);
        TextView isOnline = (TextView) convertView.findViewById(R.id.activity_invitations_isOnline);
        ImageView picture = (ImageView) convertView.findViewById(R.id.activity_invitations_picture);
        
        String array_spinner[];
        array_spinner=new String[2];
        array_spinner[0]="ACCEPT";
        array_spinner[1]="REFUSE";
        
        Spinner s = (Spinner) convertView.findViewById(R.id.spinner_accept_refuse);
        ArrayAdapter adapter = new ArrayAdapter(mContext,
        android.R.layout.simple_spinner_item, array_spinner);
        s.setAdapter(adapter);

        // Set fields with friend attributes
        name.setText(mItemsArrayList.get(position).getName());
        picture.setImageBitmap(mItemsArrayList.get(position).getPicture(mContext));
        String status;
        if (mItemsArrayList.get(position).isOnline()) {
            status = "online";
            isOnline.setTextColor(Color.GREEN);
        } else {
            status = "offline";
        }
        isOnline.setText(status);

        return convertView;
    }
}
