package ch.epfl.smartmap.gui;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
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

    private final static int HUNDRED_PERCENT = 100;
    private final static int MIDNIGHT_HOUR = 23;
    private final static int MIDNIGHT_MINUTES = 59;
    private final static int TEN = 10;

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
        TextView startDateText = (TextView) convertView.findViewById(R.id.eventStartDate);
        TextView endDateText = (TextView) convertView.findViewById(R.id.eventEndDate);

        // Set fields with event's attributes

        GregorianCalendar start = mItemsArrayList.get(position).getStartDate();
        GregorianCalendar end = mItemsArrayList.get(position).getEndDate();

        String startDateTextContent = "";
        String endDateTextContent = "";

        startDateTextContent = setTextFromDate(start, "start");
        endDateTextContent = setTextFromDate(end, "end");

        startDateText.setText(startDateTextContent);
        endDateText.setText(endDateTextContent);

        double distanceMeEvent = ShowEventsActivity.distance(mMyLocation.getLatitude(), mMyLocation.getLongitude(),
                mItemsArrayList.get(position).getLocation().getLatitude(), mItemsArrayList.get(position).getLocation()
                        .getLongitude());
        distanceMeEvent = Math.floor(distanceMeEvent * HUNDRED_PERCENT) / HUNDRED_PERCENT;

        name.setText(mItemsArrayList.get(position).getName() + " @ " + mItemsArrayList.get(position).getPositionName());

        // Set the behavior when a list element is clicked
        convertView.setId(position);
        convertView.setTag(mItemsArrayList.get(position));
        // TODO

        return convertView;
    }

    /**
     *
     * @param date the date (start or end of the event)
     * @param s "start" or "end"
     * @return the time of the event, in a cool format
     * @author SpicyCH
     */
    protected static String setTextFromDate(GregorianCalendar date, String s) {
        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar midnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR,
                MIDNIGHT_MINUTES);

        GregorianCalendar tomorrowMidnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR,
                MIDNIGHT_MINUTES);
        tomorrowMidnight.add(GregorianCalendar.DAY_OF_YEAR, 1);

        String startHourOfDayString = formatForClock(date.get(GregorianCalendar.HOUR_OF_DAY));
        String startMinuteString = formatForClock(date.get(GregorianCalendar.MINUTE));

        String dateTextContent = "";

        if (date.before(now)) {
            // Ongoing event
            if (s.equals("start")) {
                dateTextContent = "Event is live!";
            } else {
                throw new IllegalArgumentException(
                        "The given date is before now, but the given string s wasn't 'start'");
            }
        } else {
            // Upcoming event
            if (date.before(midnight)) {
                if (s.equals("start")) {
                    dateTextContent = "Today at " + startHourOfDayString + ":" + startMinuteString;
                } else {
                    dateTextContent ="Ends today at " + startHourOfDayString + ":" + startMinuteString;
                }
            } else if (date.before(tomorrowMidnight)) {
                if (s.equals("start")) {
                    dateTextContent = "Tomorrow at " + startHourOfDayString + ":" + startMinuteString;
                } else {
                    dateTextContent = "Ends Tomorrow at " + startHourOfDayString + ":" + startMinuteString;
                }
            } else {
                // Add 1 to the month because jan = 0
                if (s.equals("start")) {
                    dateTextContent = "Starts: ";
                } else {
                    dateTextContent = "Ends: ";
                }
                dateTextContent += date.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                        + (date.get(GregorianCalendar.MONTH) + 1) + "/" + date.get(GregorianCalendar.YEAR) + " at "
                        + startHourOfDayString + ":" + startMinuteString;
            }
        }

        return dateTextContent;
    }

    /**
     * @param time
     *            a second, minute or hour of the format 0, 24
     * @return a String prefixed with 0 and the time if time < 10
     * @author SpicyCH
     */
    private static String formatForClock(int time) {
        String hourOfDayString = "";
        if (time < TEN) {
            hourOfDayString += "0" + time;
        } else {
            hourOfDayString += time;
        }

        return hourOfDayString;
    }

    /**
     * Might be useful later
     */
    @SuppressWarnings("unused")
    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return Math.abs(timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS));
    }
}
