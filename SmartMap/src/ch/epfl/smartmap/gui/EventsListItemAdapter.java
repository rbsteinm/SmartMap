package ch.epfl.smartmap.gui;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;
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

    private final static String TAG = EventsListItemAdapter.class.getSimpleName();

    private final static int MINUTES_IN_A_HOUR = 60;
    private final static int FORTY_EIGHT_HOURS = 48;
    private final static int HUNDRED_PERCENT = 100;

    private final Context mContext;
    private final List<Event> mItemsArrayList;

    private final Location mMyLocation;

    /**
     * An adapter for event's list
     *
     * @param context
     * @param itemsArrayList
     */
    public EventsListItemAdapter(Context context, List<Event> itemsArrayList, Location myLocation) {

        super(context, R.layout.gui_event_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;

        mMyLocation = myLocation;
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

        GregorianCalendar start = mItemsArrayList.get(position).getStartDate();
        GregorianCalendar end = mItemsArrayList.get(position).getEndDate();
        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar midnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), 23, 59);

        GregorianCalendar tomorrowMidnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), 23, 59);
        tomorrowMidnight.add(GregorianCalendar.DAY_OF_YEAR, 1);

        /*Log.i(TAG,
                "Now: " + now.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (now.get(GregorianCalendar.MONTH) + 1) + "/"
                        + now.get(GregorianCalendar.YEAR) + " at "
                        + formatForClock(now.get(GregorianCalendar.HOUR_OF_DAY)) + ":"
                        + formatForClock(now.get(GregorianCalendar.MINUTE)));

        Log.i(TAG,
                "Midnight: " + midnight.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                        + (midnight.get(GregorianCalendar.MONTH) + 1) + "/" + midnight.get(GregorianCalendar.YEAR)
                        + " at " + formatForClock(midnight.get(GregorianCalendar.HOUR_OF_DAY)) + ":"
                        + formatForClock(midnight.get(GregorianCalendar.MINUTE)));*/

        long hoursTillEvent = getDateDiff(now.getTime(), start.getTime(), TimeUnit.HOURS);
        long hoursTillEnd = getDateDiff(now.getTime(), end.getTime(), TimeUnit.HOURS);

        String startDateTextContent = "";
        String endDateTextContent = "";

        String startHourOfDayString = formatForClock(start.get(GregorianCalendar.HOUR_OF_DAY));
        String startMinuteString = formatForClock(start.get(GregorianCalendar.MINUTE));
        String endHourOfDayString = formatForClock(end.get(GregorianCalendar.HOUR_OF_DAY));
        String endMinuteString = formatForClock(end.get(GregorianCalendar.MINUTE));

        if (start.before(now)) {
            // Ongoing event
            Log.d(TAG, "Event : " + mItemsArrayList.get(position).getPositionName());
            Log.d(TAG, "start: " + start);
            Log.d(TAG, "now: " + now);
            startDateTextContent = "Event is live!";
        } else {
            // Upcoming event
            if (start.before(midnight)) {
                startDateTextContent = "Today at " + startHourOfDayString + ":" + startMinuteString;
            } else if (start.before(tomorrowMidnight)) {
                startDateTextContent = "Tomorrow at " + startHourOfDayString + ":" + startMinuteString;
            } else {
                // Add 1 to the month because jan = 0
                startDateTextContent = "Starts: " + start.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                        + (start.get(GregorianCalendar.MONTH) + 1) + "/" + start.get(GregorianCalendar.YEAR) + " at "
                        + startHourOfDayString + ":" + startMinuteString;
            }
        }

        if (end.before(midnight)) {
            endDateTextContent = "Ends today at " + endHourOfDayString + ":" + endMinuteString;
        } else if (end.before(tomorrowMidnight)) {
            endDateTextContent = "Ends Tomorrow at " + endHourOfDayString + ":" + endMinuteString;
        } else {
            // Add 1 to the month because jan = 0
            endDateTextContent = "Ends: " + end.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                    + (end.get(GregorianCalendar.MONTH) + 1) + "/" + end.get(GregorianCalendar.YEAR) + " at "
                    + endHourOfDayString + ":" + endMinuteString;
        }

        startDateText.setText(startDateTextContent);
        endDateText.setText(endDateTextContent);

        double distanceMeEvent = ShowEventsActivity.distance(mMyLocation.getLatitude(), mMyLocation.getLongitude(),
                mItemsArrayList.get(position).getLocation().getLatitude(), mItemsArrayList.get(position).getLocation()
                        .getLongitude(), 'K');
        distanceMeEvent = Math.floor(distanceMeEvent * HUNDRED_PERCENT) / HUNDRED_PERCENT;

        name.setText(mItemsArrayList.get(position).getName() + " @ " + mItemsArrayList.get(position).getPositionName());

        String creator = "Created by " + mItemsArrayList.get(position).getCreatorName();
        creatorTextView.setText(creator);

        // Set the behavior when a list element is clicked

        // TODO

        return convertView;
    }

    /**
     * @param time
     *            a second, minute or hour of the format 0, 24
     * @return a String prefixed with 0 and the time if time < 10
     * @author SpicyCH
     */
    private String formatForClock(int time) {
        String hourOfDayString = "";
        if (time < 10) {
            hourOfDayString += "0" + time;
        } else {
            hourOfDayString += time;
        }

        return hourOfDayString;
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return Math.abs(timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS));
    }
}
