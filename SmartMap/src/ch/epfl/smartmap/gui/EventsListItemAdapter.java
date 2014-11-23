package ch.epfl.smartmap.gui;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.cache.Event;

/**
 * <p>
 * Adapter used to display the events in a list view.
 * </p>
 * <p>
 * To make the scrolling smooth, we use the view adapter design pattern. See <a href=
 * "http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder"
 * >developer.android on ViewHolder</a>
 * </p>
 * 
 * @author SpicyCH
 */
public class EventsListItemAdapter extends ArrayAdapter<Event> {

    @SuppressWarnings("unused")
    private final static String TAG = EventsListItemAdapter.class.getSimpleName();

    private final static int HUNDRED_PERCENT = 100;
    private final static int MIDNIGHT_HOUR = 23;
    private final static int MIDNIGHT_MINUTES = 59;

    /**
     * Might be useful later
     */
    @SuppressWarnings("unused")
    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return Math.abs(timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS));
    }

    /**
     * <p>
     * Gets a String to describe an event's date and time in a cool and human readable format.
     * </p>
     * <p>
     * date1 must be before date2
     * </p>
     * 
     * @param date1
     * @param date2
     * @param s
     * @return a String of the form "Today at 04:01"
     * @author SpicyCH
     */
    public static String getTextFromDate(GregorianCalendar date1, GregorianCalendar date2, String s) {

        if (date1.after(date2)) {
            throw new IllegalArgumentException("date1 must be before date2");
        }

        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar midnight =
            new GregorianCalendar(now.get(GregorianCalendar.YEAR), now.get(GregorianCalendar.MONTH),
                now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR, MIDNIGHT_MINUTES);

        GregorianCalendar tomorrowMidnight =
            new GregorianCalendar(now.get(GregorianCalendar.YEAR), now.get(GregorianCalendar.MONTH),
                now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR, MIDNIGHT_MINUTES);
        tomorrowMidnight.add(GregorianCalendar.DAY_OF_YEAR, 1);

        String startHourOfDayString =
            TimePickerFragment.formatForClock(date1.get(GregorianCalendar.HOUR_OF_DAY));
        String startMinuteString = TimePickerFragment.formatForClock(date1.get(GregorianCalendar.MINUTE));

        String endHourOfDayString =
            TimePickerFragment.formatForClock(date2.get(GregorianCalendar.HOUR_OF_DAY));
        String endMinuteString = TimePickerFragment.formatForClock(date2.get(GregorianCalendar.MINUTE));
        String dateTextContent = "";

        if (date1.before(midnight) && date2.before(midnight)) {
            // ends and starts the same day
            if (s.equals("start")) {
                dateTextContent = "Today";
            } else {
                dateTextContent =
                    "from " + startHourOfDayString + ":" + startMinuteString + " to " + endHourOfDayString
                        + ":" + endMinuteString;
            }
        } else if (date1.before(tomorrowMidnight) && date2.before(tomorrowMidnight)) {
            // ends and starts the same day
            if (s.equals("start")) {
                dateTextContent = "Tomorrow";
            } else {
                dateTextContent =
                    "from " + startHourOfDayString + ":" + startMinuteString + " to " + endHourOfDayString
                        + ":" + endMinuteString;
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
                    dateTextContent =
                        "Starts: " + date1.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                            + (date1.get(GregorianCalendar.MONTH) + 1) + "/"
                            + date1.get(GregorianCalendar.YEAR) + " at " + startHourOfDayString + ":"
                            + startMinuteString;
                } else {
                    dateTextContent =
                        "Ends: " + date2.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                            + (date2.get(GregorianCalendar.MONTH) + 1) + "/"
                            + date2.get(GregorianCalendar.YEAR) + " at " + endHourOfDayString + ":"
                            + endMinuteString;
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

    private final Context mContext;

    private final List<Event> mItemsArrayList;

    private final Location mMyLocation;

    /**
     * <<<<<<< HEAD
     * Constructor
     * 
     * @param context
     * @param itemsArrayList
     * @param myLocation
     *            the user's location
     *            =======
     *            An adapter for event's list
     * @param context
     * @param itemsArrayList
     *            >>>>>>> service-2
     */
    public EventsListItemAdapter(Context context, List<Event> itemsArrayList, Location myLocation) {
        super(context, R.layout.gui_event_list_item, itemsArrayList);

        mContext = context;
        mItemsArrayList = itemsArrayList;

        mMyLocation = myLocation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater, get Friend View from the xml via Adapter
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        EventViewHolder viewHolder;
        Event event = this.getItem(position);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.gui_event_list_item, parent, false);

            viewHolder = new EventViewHolder();

            // Set the ViewHolder with the gui_event_list_item fields
            viewHolder.setNameTextView((TextView) convertView.findViewById(R.id.eventName));
            viewHolder.setStarTextView((TextView) convertView.findViewById(R.id.eventStartDate));
            viewHolder.setEndTextView((TextView) convertView.findViewById(R.id.eventEndDate));

            // Needed by the code that makes each item clickable
            viewHolder.setEvent(event);

            // Store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (EventViewHolder) convertView.getTag();
        }

        // Set fields with event's attributes

        GregorianCalendar start = event.getStartDate();
        GregorianCalendar end = event.getEndDate();

        viewHolder.getStartTextView().setText(getTextFromDate(start, end, "start"));
        viewHolder.getEndTextView().setText(getTextFromDate(start, end, "end"));

        double distanceMeEvent =
            ShowEventsActivity.distance(mMyLocation.getLatitude(), mMyLocation.getLongitude(),
                mItemsArrayList.get(position).getLocation().getLatitude(), mItemsArrayList.get(position)
                    .getLocation().getLongitude());
        distanceMeEvent = Math.floor(distanceMeEvent * HUNDRED_PERCENT) / HUNDRED_PERCENT;

        viewHolder.getNameTextView().setText(event.getName() + " @ " + event.getPositionName());

        convertView.setId(position);

        return convertView;
    }
}
