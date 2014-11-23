package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.gui.EventViewHolder;
import ch.epfl.smartmap.gui.EventsListItemAdapter;

/**
 * This activity shows the events and offers to filter them.
 * 
 * @author SpicyCH
 */
public class ShowEventsActivity extends ListActivity {

    @SuppressWarnings("unused")
    private final static String TAG = ShowEventsActivity.class.getSimpleName();

    private final static double EARTH_RADIUS_KM = 6378.1;
    private final static int SEEK_BAR_MIN_VALUE = 2;
    private final static int ONE_HUNDRED = 100;

    /**
     * Computes the distance between two GPS locations (takes into consideration the earth radius), inspired
     * by
     * wikipedia. This is costly as there are several library calls to sin, cos, etc...
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

    private DatabaseHelper mDbHelper;
    private boolean mMyEventsChecked;
    private boolean mOngoingChecked;

    private boolean mNearMeChecked;
    private List<Event> mEventsList;
    private List<Event> mCurrentList;
    private static String mMyName;

    private Location mMyLocation;

    private void initializeGUI() {
        // We need to intialize the two following Singletons to let espresso
        // tests pass.
        mContext = this.getApplicationContext();
        SettingsManager.initialize(mContext);
        DatabaseHelper.initialize(mContext);

        mMyName = SettingsManager.getInstance().getUserName();

        mMyLocation = SettingsManager.getInstance().getLocation();

        mMyEventsChecked = false;
        mOngoingChecked = false;
        mNearMeChecked = false;

        mShowKilometers = (TextView) this.findViewById(R.id.showEventKilometers);

        // By default, the seek bar is disabled. This is done programmatically
        // as android:enabled="false" doesn't work
        // out in xml
        mSeekBar = (SeekBar) this.findViewById(R.id.showEventSeekBar);
        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        mDbHelper = DatabaseHelper.getInstance();

        mEventsList = mDbHelper.getAllEvents();
    }

    public void onCheckboxClicked(View v) {
        CheckBox checkBox = (CheckBox) v;

        switch (v.getId()) {
            case R.id.ShowEventsCheckBoxNearMe:
                if (checkBox.isChecked()) {
                    mNearMeChecked = true;
                    // Show the seek bar
                    mSeekBar.setEnabled(true);
                } else {
                    mNearMeChecked = false;
                    // Hide the seek bar
                    mSeekBar.setEnabled(false);
                }
                break;
            case R.id.ShowEventsCheckBoxMyEv:
                if (checkBox.isChecked()) {
                    mMyEventsChecked = true;
                } else {
                    mMyEventsChecked = false;
                }
                break;
            case R.id.ShowEventscheckBoxStatus:
                if (checkBox.isChecked()) {
                    mOngoingChecked = true;
                } else {
                    mOngoingChecked = false;
                }
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

        final EventViewHolder eventViewHolder = (EventViewHolder) this.findViewById(position).getTag();
        final Event event = eventViewHolder.getEvent();

        String message =
            EventsListItemAdapter.getTextFromDate(event.getStartDate(), event.getEndDate(), "start") + " - "
                + EventsListItemAdapter.getTextFromDate(event.getStartDate(), event.getEndDate(), "end")
                + "\nCreated by " + event.getCreatorName() + "\n\n" + event.getDescription();

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(event.getName()
            + " @ "
            + event.getPositionName()
            + "\n"
            + distance(mMyLocation.getLatitude(), mMyLocation.getLongitude(), event.getLocation()
                .getLatitude(), event.getLocation().getLongitude()) + " km away");
        alertDialog.setMessage(message);
        final Activity activity = this;
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
            this.getString(R.string.show_event_on_the_map_button), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {

                    Toast.makeText(activity,
                        ShowEventsActivity.this.getString(R.string.show_event_on_the_map_loading),
                        Toast.LENGTH_SHORT).show();

                    Intent showEventIntent = new Intent(mContext, MainActivity.class);
                    showEventIntent.putExtra("location", event.getLocation());
                    ShowEventsActivity.this.startActivity(showEventIntent);

                }
            });

        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.showEventsMenuNewEvent:
                Intent showEventIntent = new Intent(mContext, AddEventActivity.class);
                this.startActivity(showEventIntent);
            default:
                // No other menu items!
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // This is needed to show an update of the events' list after having
        // created an event
        this.updateCurrentList();
    }

    /**
     * This runs in O(n), can we do better?
     */
    private void updateCurrentList() {

        mMyLocation = SettingsManager.getInstance().getLocation();
        mEventsList = mDbHelper.getAllEvents();
        mCurrentList = new ArrayList<Event>();

        // Copy complete list into current list
        for (Event e : mEventsList) {
            mCurrentList.add(e);
        }

        for (Event e : mEventsList) {
            if (mMyEventsChecked) {
                if (!e.getCreatorName().equals(mMyName)) {
                    mCurrentList.remove(e);
                }
            }

            if (mOngoingChecked) {
                if (!e.getStartDate().before(new GregorianCalendar())) {
                    mCurrentList.remove(e);
                }
            }

            if (mNearMeChecked) {
                if (mMyLocation != null) {
                    double distanceMeEvent =
                        distance(e.getLocation().getLatitude(), e.getLocation().getLongitude(),
                            mMyLocation.getLatitude(), mMyLocation.getLongitude());
                    String[] showKMContent = mShowKilometers.getText().toString().split(" ");
                    double distanceMax = Double.parseDouble(showKMContent[0]);
                    if (!(distanceMeEvent < distanceMax)) {
                        mCurrentList.remove(e);
                    }
                } else {
                    Toast.makeText(this.getApplicationContext(),
                        this.getString(R.string.show_event_cannot_retrieve_current_location),
                        Toast.LENGTH_SHORT).show();
                }
            }
        }

        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mCurrentList, mMyLocation);
        this.setListAdapter(adapter);
    }
}