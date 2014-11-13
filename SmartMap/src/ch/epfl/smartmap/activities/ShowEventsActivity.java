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
import ch.epfl.smartmap.cache.UserEvent;
import ch.epfl.smartmap.gui.EventsListItemAdapter;

/**
 * This activity shows the events and offers to filter them.
 *
 * @author SpicyCH
 *
 */
public class ShowEventsActivity extends ListActivity {

    @SuppressWarnings("unused")
    private final static String TAG = ShowEventsActivity.class.getSimpleName();

    private final static double EARTH_RADIUS = 6378.1;
    private final static int SEEK_BAR_MIN_VALUE = 2;
    private final static int ONE_HUNDRED = 100;

    private SeekBar mSeekBar;
    private TextView mShowKilometers;

    private Context mContext;

    private DatabaseHelper mDbHelper;

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

        // Makes the logo clickable (clicking it returns to previous activity)
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mMyLocation = new Location("Mock location Pully");
        mMyLocation.setLatitude(46.509300);
        mMyLocation.setLongitude(6.661600);

        mContext = getApplicationContext();

        mMyEventsChecked = false;
        mOngoingChecked = false;
        mNearMeChecked = false;

        mShowKilometers = (TextView) findViewById(R.id.showEventKilometers);
        // By default, the seek bar is disabled. This is done programmatically
        // as android:enabled="false" doesn't work
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
                updateCurrentList();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        GregorianCalendar timeE0 = new GregorianCalendar();
        timeE0.add(GregorianCalendar.MINUTE, -5);
        GregorianCalendar timeEndE0 = new GregorianCalendar();
        timeEndE0.add(GregorianCalendar.HOUR_OF_DAY, 5);
        Location lutry = new Location("Lutry");
        lutry.setLatitude(46.506038);
        lutry.setLongitude(6.685314);

        UserEvent e0 = new UserEvent("Now Event", 2, "Robich", timeE0, timeEndE0, lutry);
        e0.setID(0);
        e0.setPositionName("Lutry");

        GregorianCalendar timeE1 = new GregorianCalendar();
        timeE1.add(GregorianCalendar.DAY_OF_YEAR, 5);
        GregorianCalendar timeEndE1 = new GregorianCalendar();
        timeEndE1.add(GregorianCalendar.DAY_OF_YEAR, 10);
        Location lausanne = new Location("Lausanne");
        lausanne.setLatitude(46.519962);
        lausanne.setLongitude(6.633597);

        UserEvent e1 = new UserEvent("Swag party", 2, "Robich", timeE1, timeEndE1, lausanne);
        e1.setID(1);
        e1.setPositionName("Lausanne");

        GregorianCalendar timeE2 = new GregorianCalendar();
        timeE2.add(GregorianCalendar.HOUR_OF_DAY, 3);
        GregorianCalendar timeEndE2 = new GregorianCalendar();
        timeEndE2.add(GregorianCalendar.DAY_OF_YEAR, 2);
        Location epfl = new Location("EPFL");
        epfl.setLatitude(46.526120);
        epfl.setLongitude(6.563778);

        UserEvent e2 = new UserEvent("LOL Tournament", 1, "Alain", timeE2, timeEndE2, epfl);
        e2.setID(2);
        e2.setPositionName("EPFL");

        GregorianCalendar timeE3 = new GregorianCalendar();
        timeE3.add(GregorianCalendar.HOUR_OF_DAY, 1);
        GregorianCalendar timeEndE3 = new GregorianCalendar();
        timeEndE3.add(GregorianCalendar.HOUR, 5);
        Location verbier = new Location("Verbier");
        verbier.setLatitude(46.096076);
        verbier.setLongitude(7.228875);

        UserEvent e3 = new UserEvent("Freeride World Tour", 1, "Julien", timeE3, timeEndE3, verbier);
        e3.setID(3);
        e3.setPositionName("Verbier");
        String descrE3 = "It’s a vertical free-verse poem on the mountain. It’s the ultimate expression of all that"
                + "is fun and liberating about sliding on snow in wintertime.";
        e3.setDescription(descrE3);

        mDbHelper = new DatabaseHelper(this);
        mDbHelper.addEvent(e0);
        mDbHelper.addEvent(e1);
        mDbHelper.addEvent(e2);
        mDbHelper.addEvent(e3);

        mMockEventsList = mDbHelper.getAllEvents();

        // Create custom Adapter and pass it to the Activity
        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mMockEventsList, mMyLocation);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.showEventsMenuNewEvent:
                Intent showEventIntent = new Intent(mContext, AddEventActivity.class);
                startActivity(showEventIntent);
            default:
                // No other menu items!
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Event event = (UserEvent) findViewById(position).getTag();

        String message = EventsListItemAdapter.setTextFromDate(event.getStartDate(), event.getEndDate(), "start")
                + " - " + EventsListItemAdapter.setTextFromDate(event.getStartDate(), event.getEndDate(), "end")
                + "\nCreated by " + event.getCreatorName() + "\n\n" + event.getDescription();

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(event.getName()
                + " @ "
                + event.getPositionName()
                + "\n"
                + distance(mMyLocation.getLatitude(), mMyLocation.getLongitude(), event.getLocation().getLatitude(),
                        event.getLocation().getLongitude()) + " km away");
        alertDialog.setMessage(message);
        final Activity activity = this;
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Show on the map", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                // TODO open event in map
                Toast.makeText(activity, "Opening event on the map...", Toast.LENGTH_SHORT).show();

                Intent showEventIntent = new Intent(mContext, MainActivity.class);
                showEventIntent.putExtra("location", event.getLocation());
                startActivity(showEventIntent);

            }
        });

        alertDialog.show();

        super.onListItemClick(l, v, position, id);
    }

    public void onCheckboxClicked(View v) {
        CheckBox checkBox = (CheckBox) v;

        switch (v.getId()) {
            case R.id.ShowEventsCheckBoxNearMe:
                SeekBar seekBar = (SeekBar) findViewById(R.id.showEventSeekBar);
                if (checkBox.isChecked()) {
                    mNearMeChecked = true;
                    // Show the seek bar
                    seekBar.setEnabled(true);
                } else {
                    mNearMeChecked = false;
                    // Hide the seek bar
                    seekBar.setEnabled(false);
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

        updateCurrentList();
    }

    /**
     * This runs in O(n), can we do better?
     */
    private void updateCurrentList() {

        mMockEventsList = mDbHelper.getAllEvents();
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
                        mMyLocation.getLatitude(), mMyLocation.getLongitude());
                String[] showKMContent = mShowKilometers.getText().toString().split(" ");
                double distanceMax = Double.parseDouble(showKMContent[0]);
                if (!(distanceMeEvent < distanceMax)) {
                    mCurrentList.remove(e);
                }
            }
        }

        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mCurrentList, mMyLocation);
        setListAdapter(adapter);
    }

    /**
     * Computes the distance between two GPS locations (takes into consideration the earth radius)
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return the distance in km, with 2 digits
     * @author SpicyCH
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double x1 = Math.toRadians(lat1);
        double y1 = Math.toRadians(lon1);
        double x2 = Math.toRadians(lat2);
        double y2 = Math.toRadians(lon2);

        double sec1 = Math.sin(x1) * Math.sin(x2);
        double dl = Math.abs(y1 - y2);
        double sec2 = Math.cos(x1) * Math.cos(x2);
        // sec1, sec2, dl are in degree, need to convert to radians
        double centralAngle = Math.acos(sec1 + sec2 * Math.cos(dl));
        double distance = centralAngle * EARTH_RADIUS;

        return Math.floor(distance * ONE_HUNDRED) / ONE_HUNDRED;
    }
}
