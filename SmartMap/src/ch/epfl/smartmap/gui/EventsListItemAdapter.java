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
        //DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        //mItemsArrayList = dbHelper.getAllEvents();

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

        startDateTextContent = setTextFromDate(start, end, "start");
        endDateTextContent = setTextFromDate(start, end, "end");

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
     * @param date1
     *            the date (start or end of the event)
     * @param s
     *            "start" or "end"
     * @return the time of the event, in a cool format
     * @author SpicyCH
     */
    protected static String setTextFromDate(GregorianCalendar date1, GregorianCalendar date2, String s) {
        if (date1.after(date2)) {
            // TODO assert
            throw new IllegalArgumentException("date1 must be before date2");
        }

        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar midnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR,
                MIDNIGHT_MINUTES);

        GregorianCalendar tomorrowMidnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR,
                MIDNIGHT_MINUTES);
        tomorrowMidnight.add(GregorianCalendar.DAY_OF_YEAR, 1);

        String startHourOfDayString = formatForClock(date1.get(GregorianCalendar.HOUR_OF_DAY));
        String startMinuteString = formatForClock(date1.get(GregorianCalendar.MINUTE));

        String endHourOfDayString = formatForClock(date2.get(GregorianCalendar.HOUR_OF_DAY));
        String endMinuteString = formatForClock(date2.get(GregorianCalendar.MINUTE));

        String dateTextContent = "";

        if (date1.before(midnight) && date2.before(midnight)) {
            // ends and starts the same day
            if (s.equals("start")) {
                dateTextContent = "Today";
            } else {
                dateTextContent = "from " + startHourOfDayString + ":" + startMinuteString + " to "
                        + endHourOfDayString + ":" + endMinuteString;
            }
        } else if (date1.before(tomorrowMidnight) && date2.before(tomorrowMidnight)) {
            // ends and starts the same day
            if (s.equals("start")) {
                dateTextContent = "Tomorrow";
            } else {
                dateTextContent = "from " + startHourOfDayString + ":" + startMinuteString + " to "
                        + endHourOfDayString + ":" + endMinuteString;
            }
        } else {
            // Upcoming event
            if (date1.before(midnight) && s.equals("start")) {
                dateTextContent = "Today at " + startHourOfDayString + ":" + startMinuteString;
            } else if (date2.before(midnight) && s.equals("end")) {
                dateTextContent = "Ends today at " + endHourOfDayString + ":" + endMinuteString;
            } else if (date1.before(tomorrowMidnight) && s.equals("start")) {
                dateTextContent = "Tomorrow at " + startHourOfDayString + ":" + startMinuteString;
            } else if (date2.before(tomorrowMidnight) && s.equals("end")) {
                dateTextContent = "Ends tomorrow at " + startHourOfDayString + ":" + startMinuteString;
            } else {
                if (s.equals("start")) {
                    dateTextContent = "Starts: " + date1.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                            + (date1.get(GregorianCalendar.MONTH) + 1) + "/" + date1.get(GregorianCalendar.YEAR)
                            + " at " + startHourOfDayString + ":" + startMinuteString;
                } else {
                    dateTextContent = "Ends: " + date2.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                            + (date2.get(GregorianCalendar.MONTH) + 1) + "/" + date2.get(GregorianCalendar.YEAR)
                            + " at " + endHourOfDayString + ":" + endMinuteString;
                }

            }
        }

        if (date1.before(now)) {
            // Ongoing event
            if (s.equals("start")) {
                dateTextContent = "Event is live!";
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
