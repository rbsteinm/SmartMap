package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.gui.EventViewHolder;
import ch.epfl.smartmap.gui.EventsListItemAdapter;

/**
 * This activity shows the events and offers to filter them.
 * 
 * @author SpicyCH
 */
public class ShowEventsActivity extends ListActivity {

    /**
     * Loads an event and displays its infos.
     * 
     * @author SpicyCH
     */
    class LoadEventTask extends AsyncTask<Long, Void, Map<String, Object>> {
        private static final String EVENT_KEY = "EVENT";
        private static final String CREATOR_NAME_KEY = "CREATOR_NAME";

        @Override
        protected Map<String, Object> doInBackground(Long... params) {

            Log.d(TAG, "Retrieving event...");

            long eventId = params[0];

            Map<String, Object> output = new HashMap<String, Object>();

            Event event = ServiceContainer.getCache().getPublicEvent(eventId);
            output.put(EVENT_KEY, event);

            output.put(CREATOR_NAME_KEY, event.getCreator().getName());

            return output;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {

            Log.d(TAG, "Processing event...");

            final Event event = (Event) result.get(EVENT_KEY);
            final String creatorName = (String) result.get(CREATOR_NAME_KEY);

            if ((event == null) || (creatorName == null)) {
                Log.e(TAG, "The server returned a null event or creatorName");

                Toast.makeText(mContext, mContext.getString(R.string.show_event_server_error), Toast.LENGTH_SHORT)
                    .show();

            } else {

                // Construct the dialog that display more detailed infos and
                // offers to show event on the map
                // or to
                // show more details.

                AlertDialog alertDialog = new AlertDialog.Builder(ShowEventsActivity.this).create();

                String[] textForDates =
                    EventsListItemAdapter.getTextFromDate(event.getStartDate(), event.getEndDate(), mContext);

                final String message =
                    textForDates[0] + " " + textForDates[1] + "\n" + mContext.getString(R.string.show_event_by) + " "
                        + creatorName + "\n\n" + event.getDescription();

                alertDialog.setTitle(event.getName()
                    + " @ "
                    + event.getLocationString()
                    + "\n"
                    + distance(mMyLocation.getLatitude(), mMyLocation.getLongitude(),
                        event.getLocation().getLatitude(), event.getLocation().getLongitude()) + " km away");
                alertDialog.setMessage(message);

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                    mContext.getString(R.string.show_event_on_the_map_button), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(mContext,
                                ShowEventsActivity.this.getString(R.string.show_event_on_the_map_loading),
                                Toast.LENGTH_SHORT).show();
                            Intent showEventIntent = new Intent(mContext, MainActivity.class);
                            showEventIntent.putExtra("location", event.getLocation());
                            ShowEventsActivity.this.startActivity(showEventIntent);
                        }
                    });

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
                    mContext.getString(R.string.show_event_details_button), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent showEventIntent = new Intent(mContext, EventInformationActivity.class);
                            showEventIntent.putExtra("EVENT", event.getId());
                            ShowEventsActivity.this.startActivity(showEventIntent);
                        }
                    });

                alertDialog.show();
            }

        }
    }

    /**
     * Listens for the progress change of the Seekbar and updates the list
     * accordingly.
     * 
     * @author SpicyCH
     */
    class SeekBarChangeListener implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getProgress() < SEEK_BAR_MIN_VALUE) {
                seekBar.setProgress(SEEK_BAR_MIN_VALUE);
            }
            mShowKilometers.setText(mSeekBar.getProgress() + " km");
            ShowEventsActivity.this.updateCurrentList();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Nothing
        }
    }

    private static final String TAG = ShowEventsActivity.class.getSimpleName();
    private static final double EARTH_RADIUS_KM = 6378.1;

    private static final int SEEK_BAR_MIN_VALUE = 2;

    private static final int ONE_HUNDRED = 100;

    /**
     * Computes the distance between two GPS locations (takes into consideration
     * the earth radius), inspired
     * by
     * wikipedia. This is costly as there are several library calls to sin, cos,
     * etc...
     * 
     * @param lat1
     *            latitude of point 1
     * @param lon1
     *            longitude of point 1
     * @param lat2
     *            latitude of point 2
     * @param lon2
     *            longitude of point 2
     * @return the distance between the two locations in km, rounded to 2 digits
     * @author SpicyCH
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {

        // TODO use provided method by Google?

        double radLat1 = Math.toRadians(lat1);
        double radLong1 = Math.toRadians(lon1);
        double radLat2 = Math.toRadians(lat2);
        double radLong2 = Math.toRadians(lon2);

        double sec1 = Math.sin(radLat1) * Math.sin(radLat2);
        double dl = Math.abs(radLong1 - radLong2);
        double sec2 = Math.cos(radLat1) * Math.cos(radLat2);
        double centralAngle = Math.acos(sec1 + (sec2 * Math.cos(dl)));
        double distance = centralAngle * EARTH_RADIUS_KM;

        return Math.floor(distance * ONE_HUNDRED) / ONE_HUNDRED;
    }

    private SeekBar mSeekBar;
    private TextView mShowKilometers;
    private Context mContext;

    private boolean mMyEventsChecked;

    private boolean mOngoingChecked;

    private boolean mNearMeChecked;

    /**
     * Contains all events
     */
    private List<Event> mEventsList;

    /**
     * Contains the displayed events
     */
    private List<Event> mCurrentList;

    private Location mMyLocation;

    /**
     * <p>
     * Displays an AlertDialog with details about the event and two buttons: <br />
     * -Show on map: opens the map at the location of the event<br />
     * -See details: opens a new activity and display all the event's info
     * </p>
     * 
     * @param position
     *            the position of item that has been clicked
     * @author SpicyCH
     */
    private void displayInfoDialog(int position) {

        Toast.makeText(mContext, this.getString(R.string.show_event_loading_info), Toast.LENGTH_SHORT).show();

        final EventViewHolder eventViewHolder = (EventViewHolder) this.findViewById(position).getTag();

        // Need an AsyncTask because getEventById searches on our server if
        // event not stored in cache.
        LoadEventTask loadEvent = new LoadEventTask();
        Log.d(TAG, "Executing loadEvent task with event id " + eventViewHolder.getEventId());
        loadEvent.execute(eventViewHolder.getEventId());

    }

    private void initializeGUI() {

        // We need to intialize the two following Singletons to let espresso
        // tests pass.
        mContext = this.getApplicationContext();

        mMyLocation = ServiceContainer.getSettingsManager().getLocation();

        mMyEventsChecked = false;
        mOngoingChecked = false;
        mNearMeChecked = false;

        mShowKilometers = (TextView) this.findViewById(R.id.showEventKilometers);

        mSeekBar = (SeekBar) this.findViewById(R.id.showEventSeekBar);
        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        mEventsList = new ArrayList<Event>(ServiceContainer.getCache().getAllVisibleEvents());
    }

    /**
     * Triggered when a checkbox is clicked. Updates the displayed list of
     * events.
     * 
     * @param v
     *            the checkbox whose status changed
     * @author SpicyCH
     */
    public void onCheckboxClicked(View v) {
        if (!(v instanceof CheckBox)) {
            throw new IllegalArgumentException("This method requires v to be a CheckBox");
        }

        CheckBox checkBox = (CheckBox) v;

        switch (v.getId()) {
            case R.id.ShowEventsCheckBoxNearMe:

                mNearMeChecked = checkBox.isChecked();
                mSeekBar.setEnabled(mNearMeChecked);
                break;
            case R.id.ShowEventsCheckBoxMyEv:

                mMyEventsChecked = checkBox.isChecked();
                break;
            case R.id.ShowEventscheckBoxStatus:

                mOngoingChecked = checkBox.isChecked();
                break;
            default:
                break;
        }

        this.updateCurrentList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_events);

        // Makes the logo clickable (clicking it returns to previous activity)
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

        this.initializeGUI();

        // Create custom Adapter and pass it to the Activity
        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mEventsList, mMyLocation);
        this.setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.show_events, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.displayInfoDialog(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
                break;
            case R.id.showEventsMenuNewEvent:
                Intent showEventIntent = new Intent(mContext, AddEventActivity.class);
                this.startActivity(showEventIntent);
                break;
            case R.id.show_events_menu_refresh:
                this.updateCurrentList();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        // This is needed to show an update of the events' list after having
        // created one.
        this.updateCurrentList();
    }

    /**
     * This runs in O(n), can we do better?
     */
    private void updateCurrentList() {

        mMyLocation = ServiceContainer.getSettingsManager().getLocation();
        mEventsList = new ArrayList<Event>(ServiceContainer.getCache().getAllVisibleEvents());
        mCurrentList = new ArrayList<Event>();

        // // Copy complete list into current list
        // for (Event e : mEventsList) {
        // mCurrentList.add(e);
        // }
        //
        // for (Event e : mEventsList) {
        // if (mMyEventsChecked) {
        // if
        // (!ServiceContainer.getCache().getFriend(e.getCreatorId()).getName().equals(mMyName))
        // {
        // mCurrentList.remove(e);
        // }
        // }
        //
        // if (mOngoingChecked && !e.getStartDate().before(new
        // GregorianCalendar())) {
        // mCurrentList.remove(e);
        // }
        //
        // if (mNearMeChecked) {
        // if (mMyLocation != null) {
        // double distanceMeEvent =
        // distance(e.getLocation().getLatitude(),
        // e.getLocation().getLongitude(),
        // mMyLocation.getLatitude(), mMyLocation.getLongitude());
        // String[] showKMContent =
        // mShowKilometers.getText().toString().split(" ");
        // double distanceMax = Double.parseDouble(showKMContent[0]);
        // if (!(distanceMeEvent < distanceMax)) {
        // mCurrentList.remove(e);
        // }
        // } else {
        // Toast.makeText(this.getApplicationContext(),
        // this.getString(R.string.show_event_cannot_retrieve_current_location),
        // Toast.LENGTH_SHORT).show();
        // }
        // }
        // }

        // TODO

        ServiceContainer.getCache().getNearEvents();
        ServiceContainer.getCache().getOnGoingEvents();
        ServiceContainer.getCache().getMyEvents();

        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mCurrentList, mMyLocation);
        this.setListAdapter(adapter);
    }

}