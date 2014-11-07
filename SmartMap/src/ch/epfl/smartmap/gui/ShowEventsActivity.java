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

    private final static int SEEK_BAR_MIN_VALUE = 5;

    private SeekBar mSeekBar;
    private TextView mShowKilometers;

    // Mock
    private List<Event> mockEventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

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

        mockEventsList = new ArrayList<Event>();

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

        mockEventsList.add(e1);
        mockEventsList.add(e2);

        // Create custom Adapter and pass it to the Activity
        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mockEventsList);
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
                    // Show the seek bar
                    seekBar.setEnabled(true);
                } else {
                    // Hide the seek bar
                    seekBar.setEnabled(false);
                }
                break;
            case R.id.ShowEventsCheckBoxMyEv:
                Toast.makeText(this, "Near me", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ShowEventscheckBoxStatus:
                Toast.makeText(this, "Ongoing", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
