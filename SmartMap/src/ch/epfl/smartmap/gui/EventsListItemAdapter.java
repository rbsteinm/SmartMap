package ch.epfl.smartmap.gui;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class EventsListItemAdapter extends ArrayAdapter<Event> {

    private final static int MINUTES_IN_A_HOUR = 60;
    private final static int FORTY_EIGHT_HOURS = 48;

    private final Context mContext;
    private final List<Event> mItemsArrayList;

    /**
     * An adapter for event's list
     *
     * @param context
     * @param itemsArrayList
     */
    public EventsListItemAdapter(Context context, List<Event> itemsArrayList) {

        super(context, R.layout.gui_event_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater, get Friend View from the xml via Adapter
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gui_event_list_item, parent, false);
        // TODO use Holder pattern for smoother scrolling

        // Get EventItem fields
        TextView name = (TextView) convertView.findViewById(R.id.eventName);
        TextView creatorTextView = (TextView) convertView.findViewById(R.id.eventCreator);
        TextView startDateText = (TextView) convertView.findViewById(R.id.eventStartDate);
        TextView endDateText = (TextView) convertView.findViewById(R.id.eventEndDate);

        // Set fields with event's attributes
        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar start = mItemsArrayList.get(position).getStartDate();
        GregorianCalendar end = mItemsArrayList.get(position).getEndDate();

        long hoursTillEvent = getDateDiff(now.getTime(), start.getTime(), TimeUnit.HOURS);

        long hoursTillEnd = getDateDiff(now.getTime(), end.getTime(), TimeUnit.HOURS);

        String startDateTextContent = "";

        String endDateTextContent = "end: ";

        if (start.before(now)) {
            // Ongoing event
            startDateTextContent = "Event is live!";
        } else {
            if (hoursTillEvent <= FORTY_EIGHT_HOURS) {
                startDateTextContent = "starts in "
                        + Long.toString(hoursTillEvent)
                        + " hours and "
                        + Long.toString((getDateDiff(now.getTime(), start.getTime(), TimeUnit.MINUTES))
                                - (hoursTillEvent * MINUTES_IN_A_HOUR)) + " minutes";
            } else {
                // Add 1 to the month because jan = 0
                startDateTextContent = "starts: " + start.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                        + (start.get(GregorianCalendar.MONTH) + 1) + "/" + start.get(GregorianCalendar.YEAR);
            }
        }

        if (hoursTillEnd <= FORTY_EIGHT_HOURS) {
            endDateTextContent = "ends in "
                    + Long.toString(hoursTillEnd)
                    + " hours and "
                    + Long.toString((getDateDiff(now.getTime(), end.getTime(), TimeUnit.MINUTES))
                            - (hoursTillEnd * MINUTES_IN_A_HOUR)) + " minutes";
        } else {
            // Add 1 to the month because jan = 0
            endDateTextContent = "ends: " + end.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                    + (end.get(GregorianCalendar.MONTH) + 1) + "/" + end.get(GregorianCalendar.YEAR);
        }

        startDateText.setText(startDateTextContent);
        endDateText.setText(endDateTextContent);

        name.setText(mItemsArrayList.get(position).getName() + " @ " + mItemsArrayList.get(position).getPositionName());

        String creator = "Creator: " + mItemsArrayList.get(position).getCreatorName();
        creatorTextView.setText(creator);

        return convertView;
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return Math.abs(timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS));
    }
}
