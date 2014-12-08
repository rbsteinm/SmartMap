package ch.epfl.smartmap.gui;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
 * To make the scrolling smooth, we use the view adapter design pattern. <br />
 * See <a href= "http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder"
 * >developer.android on ViewHolder</a>
 * </p>
 * 
 * @author SpicyCH
 */
public class EventsListItemAdapter extends ArrayAdapter<Event> {

    private static final int HUNDRED_PERCENT = 100;
    private static final int MIDNIGHT_HOUR = 23;
    private static final int MIDNIGHT_MINUTES = 59;

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

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.gui_event_list_item, parent, false);

            viewHolder = new EventViewHolder();

            // Set the ViewHolder with the gui_event_list_item fields
            viewHolder.setNameTextView((TextView) convertView.findViewById(R.id.eventName));
            viewHolder.setStarTextView((TextView) convertView.findViewById(R.id.eventStartDate));
            viewHolder.setEndTextView((TextView) convertView.findViewById(R.id.eventEndDate));

            // Needed by the code that makes each item clickable
            viewHolder.setEventId(event.getId());

            // Store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (EventViewHolder) convertView.getTag();
        }

        // Set fields with event's attributes

        Calendar start = event.getStartDate();
        Calendar end = event.getEndDate();

        String[] textForDates = getTextFromDate(start, end, mContext);

        viewHolder.getStartTextView().setText(textForDates[0]);
        viewHolder.getEndTextView().setText(textForDates[1]);

        double distanceMeEvent = ShowEventsActivity.distance(mMyLocation.getLatitude(), mMyLocation.getLongitude(),
                mItemsArrayList.get(position).getLocation().getLatitude(), mItemsArrayList.get(position).getLocation()
                        .getLongitude());
        distanceMeEvent = Math.floor(distanceMeEvent * HUNDRED_PERCENT) / HUNDRED_PERCENT;

        viewHolder.getNameTextView().setText(event.getName() + " @ " + event.getLocationString());

        convertView.setId(position);

        return convertView;
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
     * @param context
     * @return An array of two Strings: the first is the text computed for the start date and the second if text
     *         computed for the end date.
     * @author SpicyCH
     */
    public static String[] getTextFromDate(Calendar date1, Calendar date2, Context context) {

        if (date1.after(date2)) {
            throw new IllegalArgumentException("date1 must be before date2");
        }

        // First entry holds text for start date, second holds text for end date
        String[] output = new String[2];

        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar midnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR,
                MIDNIGHT_MINUTES);

        GregorianCalendar tomorrowMidnight = new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH), MIDNIGHT_HOUR,
                MIDNIGHT_MINUTES);
        tomorrowMidnight.add(GregorianCalendar.DAY_OF_YEAR, 1);

        String startHourOfDayString = TimePickerFragment.formatForClock(date1.get(GregorianCalendar.HOUR_OF_DAY));
        String startMinuteString = TimePickerFragment.formatForClock(date1.get(GregorianCalendar.MINUTE));

        String endHourOfDayString = TimePickerFragment.formatForClock(date2.get(GregorianCalendar.HOUR_OF_DAY));
        String endMinuteString = TimePickerFragment.formatForClock(date2.get(GregorianCalendar.MINUTE));

        if (date1.before(midnight) && date2.before(midnight)) {

            // ends and starts today

            if (date1.before(now)) {

                output[0] = context.getString(R.string.events_list_item_adapter_event_live);

            } else {
                output[0] = context.getString(R.string.events_list_item_adapter_today);
            }
            output[1] = context.getString(R.string.events_list_item_adapter_from) + " " + startHourOfDayString + ":"
                    + startMinuteString + " " + context.getString(R.string.events_list_item_adapter_to) + " "
                    + endHourOfDayString + ":" + endMinuteString;

        } else if (date1.before(tomorrowMidnight) && date2.before(tomorrowMidnight)) {

            // ends and starts tomorrow

            output[0] = context.getString(R.string.events_list_item_adapter_tomorrow);
            output[1] = context.getString(R.string.events_list_item_adapter_from) + " " + startHourOfDayString + ":"
                    + startMinuteString + " " + context.getString(R.string.events_list_item_adapter_to) + " "
                    + endHourOfDayString + ":" + endMinuteString;

        } else {

            // Upcoming event

            output = getUpcomingEventText(date1, date2, midnight, tomorrowMidnight, startHourOfDayString,
                    startMinuteString, endHourOfDayString, endMinuteString, context);
        }

        return output;
    }

    /**
     * @return
     * @author SpicyCH
     */
    private static String[] getUpcomingEventText(Calendar date1, Calendar date2, Calendar midnight,
            Calendar tomorrowMidnight, String startHourOfDayString, String startMinuteString,
            String endHourOfDayString, String endMinuteString, Context context) {

        String[] output = new String[2];

        if (date1.before(midnight)) {

            output[0] = context.getString(R.string.events_list_item_adapter_today) + " - " + startHourOfDayString + ":"
                    + startMinuteString;

        } else if (date2.before(midnight)) {

            output[1] = context.getString(R.string.events_list_item_adapter_ends_today_at) + " " + endHourOfDayString
                    + ":" + endMinuteString;

        } else if (date1.before(tomorrowMidnight)) {

            output[0] = context.getString(R.string.events_list_item_adapter_tomorrow) + " - " + startHourOfDayString
                    + ":" + startMinuteString;

        } else if (date2.before(tomorrowMidnight)) {

            output[1] = context.getString(R.string.events_list_item_adapter_ends_tomorrow_at) + " "
                    + startHourOfDayString + ":" + startMinuteString;

        } else {

            output[0] = context.getString(R.string.events_list_item_adapter_starts) + " "
                    + date1.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (date1.get(GregorianCalendar.MONTH) + 1) + "/"
                    + date1.get(GregorianCalendar.YEAR) + " - " + startHourOfDayString + ":" + startMinuteString;

            output[1] = context.getString(R.string.events_list_item_adapter_ends) + " "
                    + date2.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (date2.get(GregorianCalendar.MONTH) + 1) + "/"
                    + date2.get(GregorianCalendar.YEAR) + " - " + endHourOfDayString + ":" + endMinuteString;

        }

        return output;
    }

}