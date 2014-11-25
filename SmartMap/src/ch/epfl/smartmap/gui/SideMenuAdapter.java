package ch.epfl.smartmap.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.epfl.smartmap.R;

/**
 * Customized adapter that displays a list of Views in the side drawer menu
 * ListView
 * 
 * @author rbsteinm
 */
public class SideMenuAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final String[] mListItems;

    /**
     * @param context
     *            Context of the Activity where we want to display the user list
     * @param listItems
     *            list of String to display in the side menu
     */
    public SideMenuAdapter(Context context, String[] listItems) {
        super(context, R.layout.drawer_list_item, listItems);
        mContext = context;
        mListItems = listItems;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create inflater,get item to construct
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        // Get item field
        TextView sideMenuView = (TextView) convertView.findViewById(R.id.side_menu_text_view);
        // Set item field + id
        sideMenuView.setText(mListItems[position]);
        // set tag to each View
        sideMenuView.setTag("side_menu_tag_" + position);

        return convertView;
    }
}
