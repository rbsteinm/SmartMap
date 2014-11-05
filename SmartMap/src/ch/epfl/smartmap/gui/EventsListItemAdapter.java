package ch.epfl.smartmap.gui;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Event;

/**
 * Adapter DP used to display the events in a list view
 *
 * @author SpicyCH
 *
 */
@SuppressLint("ViewHolder")
public class EventsListItemAdapter extends ArrayAdapter<Event> {

    private final Context mContext;
    private final List<Event> mItemsArrayList;

    public EventsListItemAdapter(Context context, List<Event> itemsArrayList) {

        super(context, R.layout.gui_event_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater, get Friend View from the xml via Adapter
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gui_event_list_item, parent, false);
        // TODO use Holder pattern for smoother scrolling

        // Get EventItem fields
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView isOnline = (TextView) convertView.findViewById(R.id.isOnline);

        // Set fields with event's attributes
        name.setText(mItemsArrayList.get(position).getName());
        String status = "Status: ";
        isOnline.setText(status);

        return convertView;
    }
}
