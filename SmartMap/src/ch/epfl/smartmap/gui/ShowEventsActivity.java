package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.UserEvent;

/**
 * This activity shows the events and offers to filter them.
 *
 * @author SpicyCH
 *
 */
public class ShowEventsActivity extends ListActivity {

    private final static String TAG = ShowEventsActivity.class.getSimpleName();

    private final static double EARTH_RADIUS = 6378.1;

    private final static int SEEK_BAR_MIN_VALUE = 2;

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

        mMyLocation = new Location("Mock location Pully");
        mMyLocation.setLatitude(46.509300);
        mMyLocation.setLongitude(6.661600);

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
                updateCurrentList();
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
        Location lutry = new Location("Lutry");
        lutry.setLatitude(46.506038);
        lutry.setLongitude(6.685314);

        UserEvent e0 = new UserEvent("Now Event", 2, "Robich", timeE0, timeEndE0, lutry);
        e0.setPositionName("Lutry");

        GregorianCalendar timeE1 = new GregorianCalendar();
        timeE1.add(GregorianCalendar.DAY_OF_YEAR, 5);
        GregorianCalendar timeEndE1 = new GregorianCalendar();
        timeEndE1.add(GregorianCalendar.DAY_OF_YEAR, 10);
        Location lausanne = new Location("Lausanne");
        lausanne.setLatitude(46.519962);
        lausanne.setLongitude(6.633597);

        UserEvent e1 = new UserEvent("Swag party", 2, "Robich", timeE1, timeEndE1, lausanne);
        e1.setPositionName("Lausanne");

        GregorianCalendar timeE2 = new GregorianCalendar();
        timeE2.add(GregorianCalendar.HOUR_OF_DAY, 3);
        GregorianCalendar timeEndE2 = new GregorianCalendar();
        timeEndE2.add(GregorianCalendar.DAY_OF_YEAR, 2);
        Location epfl = new Location("EPFL");
        epfl.setLatitude(46.526120);
        epfl.setLongitude(6.563778);

        UserEvent e2 = new UserEvent("LOL Tournament", 1, "Alain", timeE2, timeEndE2, epfl);
        e2.setPositionName("EPFL");

        GregorianCalendar timeE3 = new GregorianCalendar();
        timeE3.add(GregorianCalendar.HOUR_OF_DAY, 1);
        GregorianCalendar timeEndE3 = new GregorianCalendar();
        timeEndE3.add(GregorianCalendar.HOUR, 5);
        Location verbier = new Location("Verbier");
        verbier.setLatitude(46.096076);
        verbier.setLongitude(7.228875);

        UserEvent e3 = new UserEvent("Freeride World Tour", 1, "Alain", timeE3, timeEndE3, verbier);
        e3.setPositionName("Verbier");
        String descrE3 = "It’s a vertical free-verse poem on the mountain. It’s the ultimate expression of all that is fun and liberating about sliding on snow in wintertime.";
        e3.setDescription(descrE3);

        mMockEventsList.add(e0);
        mMockEventsList.add(e1);
        mMockEventsList.add(e2);
        mMockEventsList.add(e3);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Event event = (UserEvent) findViewById(position).getTag();

        String message = EventsListItemAdapter.setTextFromDate(event.getStartDate(), "start") + " - "
                + EventsListItemAdapter.setTextFromDate(event.getEndDate(), "end") + "\nCreated by "
                + event.getCreatorName() + "\n\n" + event.getDescription();

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
                        mMyLocation.getLatitude(), mMyLocation.getLongitude());
                /*
                 * Toast.makeText( this, "The distance between me and event " + e.getName() + " @ " +
                 * e.getPositionName() + " is " + distanceMeEvent + " km", Toast.LENGTH_LONG).show();
                 */
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

    protected static double distance(double lat1, double lon1, double lat2, double lon2) {
        double x1 = Math.toRadians(lat1);
        double y1 = Math.toRadians(lon1);
        double x2 = Math.toRadians(lat2);
        double y2 = Math.toRadians(lon2);

        double sec1 = Math.sin(x1) * Math.sin(x2);
        double dl = Math.abs(y1 - y2);
        double sec2 = Math.cos(x1) * Math.cos(x2);
        // sec1,sec2,dl are in degree, need to convert to radians
        double centralAngle = Math.acos(sec1 + sec2 * Math.cos(dl));
        // Radius of Earth: 6378.1 kilometers
        double distance = centralAngle * EARTH_RADIUS;

        return Math.floor(distance * 100) / 100;
    }
}
