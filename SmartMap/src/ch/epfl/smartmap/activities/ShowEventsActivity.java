package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
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
import ch.epfl.smartmap.listeners.OnCacheListener;
import ch.epfl.smartmap.util.Utils;

/**
 * This activity shows the events and offers to filter them.
 * 
 * @author SpicyCH
 */
public class ShowEventsActivity extends ListActivity {

    private static final String TAG = ShowEventsActivity.class.getSimpleName();

    private static final int SEEK_BAR_MIN_VALUE = 2;

    private static final int METERS_IN_ONE_KM = 1000;

    private SeekBar mSeekBar;

    private TextView mShowKilometers;

    private boolean mMyEventsChecked;

    private boolean mOngoingChecked;
    private boolean mNearMeChecked;
    private List<Event> mEventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_events);

        // Makes the logo clickable (clicking it returns to previous activity)
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

        this.initializeGUI();

        // Create custom Adapter and pass it to the Activity
        this.setListAdapter(new EventsListItemAdapter(this, mEventsList));

        // Initialize the listener
        ServiceContainer.getCache().addOnCacheListener(new OnCacheListener() {
            @Override
            public void onEventListUpdate() {
                ShowEventsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowEventsActivity.this.setListAdapter(new EventsListItemAdapter(ShowEventsActivity.this,
                                mEventsList));
                    }
                });
            }
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "Event at position " + position + " with id " + id + " (view id " + v.getId() + ")");
        super.onListItemClick(l, v, position, id);
        this.displayInfoDialog(position);
    }

    /**
     * Triggered when a checkbox is clicked. Updates the displayed list of events.
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.show_events, menu);
        return true;
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
                Intent showEventIntent = new Intent(ShowEventsActivity.this, AddEventActivity.class);
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
     * Displays an AlertDialog with a button to see the event on the map, and another to see more details.
     * 
     * @param event
     *            the event to show a dialog for
     * @param creatorName
     *            the creator of the event
     * @author SpicyCH
     */
    private void displayDialog(final Event event, String creatorName) {
        AlertDialog alertDialog = new AlertDialog.Builder(ShowEventsActivity.this).create();

        Calendar start = event.getStartDate();
        Calendar end = event.getEndDate();

        final String message = Utils.getDateString(start) + " " + Utils.getTimeString(start) + " - "
                + Utils.getDateString(end) + " " + Utils.getTimeString(end) + "\n"
                + ShowEventsActivity.this.getString(R.string.show_event_by) + " " + creatorName + "\n\n"
                + event.getDescription();

        alertDialog.setTitle(event.getName() + " " + this.getResources().getString(R.string.near) + " "
                + event.getLocationString());

        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                ShowEventsActivity.this.getString(R.string.show_event_on_the_map_button),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(ShowEventsActivity.this,
                                ShowEventsActivity.this.getString(R.string.show_event_on_the_map_loading),
                                Toast.LENGTH_SHORT).show();
                        Intent showEventIntent = new Intent(ShowEventsActivity.this, MainActivity.class);
                        showEventIntent.putExtra(AddEventActivity.LOCATION_EXTRA, event.getLocation());
                        ShowEventsActivity.this.startActivity(showEventIntent);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
                ShowEventsActivity.this.getString(R.string.show_event_details_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent showEventIntent = new Intent(ShowEventsActivity.this, EventInformationActivity.class);
                        showEventIntent.putExtra("EVENT", event.getId());
                        ShowEventsActivity.this.startActivity(showEventIntent);
                    }
                });
        alertDialog.show();
    }

    /**
     * <p>
     * Displays an AlertDialog with details about the event and two buttons: <br />
     * -Show on map: opens the map at the location of the event<br />
     * -See details: opens a new activity and display all the event's info
     * </p>
     * 
     * @param position
     *            the position of the item that has been clicked
     * @author SpicyCH
     */
    private void displayInfoDialog(int position) {

        Toast.makeText(ShowEventsActivity.this, this.getString(R.string.show_event_loading_info), Toast.LENGTH_SHORT)
                .show();

        final EventViewHolder eventViewHolder = (EventViewHolder) this.findViewById(position).getTag();

        this.getEvent(eventViewHolder.getEventId());

    }

    private void getEvent(long eventId) {

        Log.d(TAG, "Retrieving event...");

        Event event = ServiceContainer.getCache().getEvent(eventId);
        String creatorName = event.getCreator().getName();

        Log.d(TAG, "Processing event...");

        if ((event == null) || (creatorName == null)) {
            Log.e(TAG, "The server returned a null event or creatorName");

            Toast.makeText(ShowEventsActivity.this,
                    ShowEventsActivity.this.getString(R.string.show_event_server_error), Toast.LENGTH_SHORT).show();

        } else {

            // Construct the dialog that display more detailed infos and
            // offers to show event on the map
            // or to
            // show more details.

            this.displayDialog(event, creatorName);

        }

    }

    /**
     * Initilizes the activity.
     * 
     * @author SpicyCH
     */
    private void initializeGUI() {

        if (ServiceContainer.getSettingsManager() == null) {
            ServiceContainer.initSmartMapServices(this);
        }

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
     * Updates the list displayed to the user.
     */
    private void updateCurrentList() {

        Log.d(TAG, "Updating event's list to match user choices");

        mEventsList = new ArrayList<Event>(ServiceContainer.getCache().getAllEvents());

        if (mNearMeChecked) {
            int maximumDistanceKM = mSeekBar.getProgress() * METERS_IN_ONE_KM;
            Location myLocation = ServiceContainer.getSettingsManager().getLocation();

            Log.d(TAG, "My location: " + myLocation.getLatitude() + "/" + myLocation.getLongitude());
            for (int i = 0; i < mEventsList.size(); i++) {
                Log.d(TAG, "Event " + i + " has distance " + mEventsList.get(i).getLocation().distanceTo(myLocation)
                        + " meters.");
                if (mEventsList.get(i).getLocation().distanceTo(myLocation) > maximumDistanceKM) {
                    mEventsList.remove(i);
                }
            }
        }

        if (mMyEventsChecked) {
            mEventsList.retainAll(ServiceContainer.getCache().getMyEvents());
        }

        if (mOngoingChecked) {
            mEventsList.retainAll(ServiceContainer.getCache().getLiveEvents());
        }

        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mEventsList);
        this.setListAdapter(adapter);
    }

    /**
     * Listens for the progress change of the Seekbar and updates the list accordingly.
     * 
     * @author SpicyCH
     */
    private class SeekBarChangeListener implements OnSeekBarChangeListener {

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

}