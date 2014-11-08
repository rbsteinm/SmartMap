package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.ListActivity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.UserEvent;

/**
 * This activity shows the events and offers to filter them.
 *
 * @author SpicyCH
 *
 */
public class ShowEventsActivity extends ListActivity {

    private final static int SEEK_BAR_MIN_VALUE = 5;

    private SeekBar mSeekBar;
    private TextView mShowKilometers;

    private boolean mMyEventsChecked;
    private boolean mOngoingChecked;
    private boolean mNearMeChecked;

    // Mock
    private List<Event> mMockEventsList;
    private List<Event> mCurrentList;
    private static String mMyName = "Robich";
    private Location mMyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        mMyLocation = new Location("Mock location");

        mMyEventsChecked = false;
        mOngoingChecked = false;
        mNearMeChecked = false;

        mShowKilometers = (TextView) findViewById(R.id.showEventKilometers);
        // By default, the seek bar is disabled. This is done programmatically as android:enabled="false" doesn't work
        // out in xml
        mSeekBar = (SeekBar) findViewById(R.id.showEventSeekBar);
        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() < SEEK_BAR_MIN_VALUE) {
                    seekBar.setProgress(SEEK_BAR_MIN_VALUE);
                }
                mShowKilometers.setText(mSeekBar.getProgress() + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        mMockEventsList = new ArrayList<Event>();

        GregorianCalendar timeE0 = new GregorianCalendar();
        timeE0.add(GregorianCalendar.MINUTE, -5);
        GregorianCalendar timeEndE0 = new GregorianCalendar();
        timeEndE0.add(GregorianCalendar.HOUR_OF_DAY, 5);

        UserEvent e0 = new UserEvent("Now Event", 2, "Robich", timeE0, timeEndE0, new Location("Robich provider"));
        e0.setPositionName("Pully");

        GregorianCalendar timeE1 = new GregorianCalendar();
        timeE1.add(GregorianCalendar.DAY_OF_YEAR, 5);
        GregorianCalendar timeEndE1 = new GregorianCalendar();
        timeEndE1.add(GregorianCalendar.DAY_OF_YEAR, 10);

        UserEvent e1 = new UserEvent("Swag party", 2, "Robich", timeE1, timeEndE1, new Location("Robich provider"));
        e1.setPositionName("Lausanne");

        GregorianCalendar timeE2 = new GregorianCalendar();
        timeE2.add(GregorianCalendar.DAY_OF_YEAR, 1);
        GregorianCalendar timeEndE2 = new GregorianCalendar();
        timeEndE2.add(GregorianCalendar.DAY_OF_YEAR, 2);

        UserEvent e2 = new UserEvent("LOL Tournament", 1, "Alain", timeE2, timeEndE2, new Location("Robich provider"));
        e2.setPositionName("EPFL");

        GregorianCalendar timeE3 = new GregorianCalendar();
        GregorianCalendar timeEndE3 = new GregorianCalendar();
        timeEndE3.add(GregorianCalendar.DAY_OF_YEAR, 1);

        UserEvent e3 = new UserEvent("Freeride World Tour", 1, "Alain", timeE3, timeEndE3, new Location(
                "Robich provider"));
        e3.setPositionName("Verbier");

        mMockEventsList.add(e0);
        mMockEventsList.add(e1);
        mMockEventsList.add(e2);
        mMockEventsList.add(e3);

        // Create custom Adapter and pass it to the Activity
        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mMockEventsList);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCheckboxClicked(View v) {
        CheckBox checkBox = (CheckBox) v;

        switch (v.getId()) {
            case R.id.ShowEventsCheckBoxNearMe:
                SeekBar seekBar = (SeekBar) findViewById(R.id.showEventSeekBar);
                if (checkBox.isChecked()) {
                    mNearMeChecked = true;
                    updateCurrentList();
                    // Show the seek bar
                    seekBar.setEnabled(true);
                } else {
                    mNearMeChecked = false;
                    updateCurrentList();
                    // Hide the seek bar
                    seekBar.setEnabled(false);
                }
                break;
            case R.id.ShowEventsCheckBoxMyEv:
                if (checkBox.isChecked()) {
                    mMyEventsChecked = true;
                    updateCurrentList();
                } else {
                    mMyEventsChecked = false;
                    updateCurrentList();
                }
                break;
            case R.id.ShowEventscheckBoxStatus:
                if (checkBox.isChecked()) {
                    mOngoingChecked = true;
                    updateCurrentList();
                } else {
                    mOngoingChecked = false;
                    updateCurrentList();
                }
                break;
            default:
                break;
        }
    }

    /**
     * This runs in O(n), can we do better?
     */
    private void updateCurrentList() {
        mCurrentList = new ArrayList<Event>();

        // Copy complete list into current list
        for (Event e : mMockEventsList) {
            mCurrentList.add(e);
        }

        for (Event e : mMockEventsList) {
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
                double distanceMeEvent = distance(e.getLocation().getLatitude(), e.getLocation().getLongitude(),
                        mMyLocation.getLatitude(), mMyLocation.getLongitude(), 'K');
                Toast.makeText(this,
                        "The distance between me and event " + e.getName() + " is " + distanceMeEvent + " km",
                        Toast.LENGTH_LONG).show();
                if (distanceMeEvent < (Double.parseDouble(mShowKilometers.getText().toString()))) {

                }
            }
        }

        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mCurrentList);
        setListAdapter(adapter);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /*
     * System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'M') + " Miles\n");
     * System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'K') + " Kilometers\n");
     * System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'N') + " Nautical Miles\n");
     */
}
