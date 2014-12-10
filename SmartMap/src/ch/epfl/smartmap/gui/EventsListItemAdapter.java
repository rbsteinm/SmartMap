package ch.epfl.smartmap.gui;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.util.Utils;

/**
 * <p>
 * Adapter used to display the events in a list view. Used in {@link ch.epfl.smartmap.activities.ShowEventsActivity}.
 * </p>
 * <p>
 * To make the scrolling smooth, we use the view adapter design pattern. <br />
 * See <a href= "http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder"
 * >developer.android on ViewHolder</a>
 * </p>
 * 
 * @author SpicyCH
 */
public class EventsListItemAdapter extends ArrayAdapter<Event> {

    private static final int HUNDRED_PERCENT = 100;

    private final Context mContext;

    private final List<Event> mItemsArrayList;

    private final Location mMyLocation;

    /**
     * Constructor
     * 
     * @param context
     * @param itemsArrayList
     */
    public EventsListItemAdapter(Context context, List<Event> itemsArrayList, Location location) {
        super(context, R.layout.gui_event_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
        mMyLocation = new Location(location);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater, get Friend View from the xml via Adapter
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        EventViewHolder viewHolder;
        Event event = this.getItem(position);

        View newConvertView;

        if (convertView == null) {

            newConvertView = inflater.inflate(R.layout.gui_event_list_item, parent, false);

            viewHolder = new EventViewHolder();

            // Set the ViewHolder with the gui_event_list_item fields
            viewHolder.setNameTextView((TextView) newConvertView.findViewById(R.id.eventName));
            viewHolder.setStarTextView((TextView) newConvertView.findViewById(R.id.eventStartDate));
            viewHolder.setEndTextView((TextView) newConvertView.findViewById(R.id.eventEndDate));

            // Needed by the code that makes each item clickable
            viewHolder.setEventId(event.getId());

            // Store the holder with the view
            newConvertView.setTag(viewHolder);
        } else {
            newConvertView = convertView;
            viewHolder = (EventViewHolder) newConvertView.getTag();
        }

        // Set fields with event's attributes

        Calendar start = event.getStartDate();
        Calendar end = event.getEndDate();

        String startString = Utils.getDateString(start) + " " + Utils.getTimeString(start);
        String endString = Utils.getDateString(end) + " " + Utils.getTimeString(end);

        viewHolder.getStartTextView().setText(startString);
        viewHolder.getEndTextView().setText(endString);

        viewHolder.getNameTextView().setText(event.getName() + " @ " + event.getLocationString());

        newConvertView.setId(position);

        return newConvertView;
    }

}