package ch.epfl.smartmap.activities;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.cache.UserEvent;
import ch.epfl.smartmap.gui.DatePickerFragment;
import ch.epfl.smartmap.gui.TimePickerFragment;

/**
 * This activity lets the user create a new event.
 *
 * @author SpicyCH
 *
 */
public class AddEventActivity extends FragmentActivity {

    private EditText mEventName;
    private EditText mPickStartTime;
    private EditText mPickStartDate;
    private EditText mPickEndTime;
    private EditText mPickEndDate;
    private EditText mLatitude;
    private EditText mLongitude;
    private EditText mDescription;
    private EditText mPlaceName;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Makes the logo clickable (clicking it returns to previous activity)
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();
        mEventName = (EditText) findViewById(R.id.addEventEventName);
        mPickStartDate = (EditText) findViewById(R.id.addEventEventDate);
        mPickStartTime = (EditText) findViewById(R.id.addEventEventTime);
        mPickEndTime = (EditText) findViewById(R.id.addEventEndTime);
        mPickEndDate = (EditText) findViewById(R.id.addEventEndDate);
        mLatitude = (EditText) findViewById(R.id.addEventLatitude);
        mLongitude = (EditText) findViewById(R.id.addEventLongitude);
        mDescription = (EditText) findViewById(R.id.addEventDescription);
        mPlaceName = (EditText) findViewById(R.id.addEventPlaceName);

        TextWatcher textChangedListener = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Good

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Good

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkDatesValidity(mPickStartDate, mPickStartTime, mPickEndDate, mPickEndTime);

            }
        };

        mPickStartDate.addTextChangedListener(textChangedListener);
        mPickStartTime.addTextChangedListener(textChangedListener);
        mPickEndDate.addTextChangedListener(textChangedListener);
        mPickEndTime.addTextChangedListener(textChangedListener);

        Calendar now = Calendar.getInstance();

        mPickStartTime.setText(TimePickerFragment.formatForClock(now.get(Calendar.HOUR_OF_DAY)) + ":"
                + TimePickerFragment.formatForClock(now.get(Calendar.MINUTE)));
        mPickStartTime.setTag(new int[] { now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE) });

        mPickStartTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(mPickStartTime);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }

        });

        mPickStartDate.setText(now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/"
                + now.get(Calendar.YEAR));
        mPickStartDate.setTag(new int[] { now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH) });

        mPickStartDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(mPickStartDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mPickEndDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(mPickEndDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mPickEndTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(mPickEndTime);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

    }

    /**
     * Ensures the end of the event is after its start.
     *
     * @param startDate
     * @param startTime
     * @param endDate
     * @param endTime
     * @author SpicyCH
     */
    private void checkDatesValidity(EditText startDate, EditText startTime, EditText endDate, EditText endTime) {
        int[] startDateTag = (int[]) startDate.getTag();
        int[] startTimeTag = (int[]) startTime.getTag();

        int[] endDateTag = (int[]) endDate.getTag();
        int[] endTimeTag = (int[]) endTime.getTag();

        if (endDateTag != null && endTimeTag != null) {
            // The end of the event has been set by the user

            GregorianCalendar start = new GregorianCalendar(startDateTag[0], startDateTag[1], startDateTag[2],
                    startTimeTag[0], startTimeTag[1], 0);
            GregorianCalendar end = new GregorianCalendar(endDateTag[0], endDateTag[1], endDateTag[2], endTimeTag[0],
                    endTimeTag[1], 0);

            if (end.before(start)) {
                // The user is trying to create the end of the event before its start!
                endDate.setTag(null);
                endTime.setTag(null);
                endDate.setText("End Date");
                endTime.setText("End Time");

                Toast.makeText(mContext, "The event cannot end before it begins!", Toast.LENGTH_LONG).show();
            } else if (end.before(Calendar.getInstance())) {
                // The user is trying to create an event in the past
                endDate.setTag(null);
                endTime.setTag(null);
                endDate.setText("End Date");
                endTime.setText("End Time");

                Toast.makeText(mContext, "The event's end cannot be in the past!", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_event, menu);
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
            case R.id.addEventButtonCreateEvent:
                createEvent();
                break;
            default:
                // No other menu items!
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @author SpicyCH
     */
    private void createEvent() {
        int[] startDateTag = (int[]) mPickStartDate.getTag();
        int[] startTimeTag = (int[]) mPickStartTime.getTag();

        int[] endDateTag = (int[]) mPickEndDate.getTag();
        int[] endTimeTag = (int[]) mPickEndTime.getTag();

        if (endDateTag == null || endTimeTag == null || mLatitude.getText().toString() == null
                || mLongitude.getText().toString() == null || mPlaceName == null) {
            Toast.makeText(mContext, "Cannot create event: please specify all fields!", Toast.LENGTH_SHORT).show();
        } else {
            GregorianCalendar startDate = new GregorianCalendar(startDateTag[0], startDateTag[1], startDateTag[2],
                    startTimeTag[0], startTimeTag[1], 0);
            GregorianCalendar endDate = new GregorianCalendar(endDateTag[0], endDateTag[1], endDateTag[2],
                    endTimeTag[0], endTimeTag[1], 0);

            double latitude = Double.parseDouble(mLatitude.getText().toString());
            double longitude = Double.parseDouble(mLongitude.getText().toString());
            Location location = new Location("Location set by user");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            SettingsManager setMng = new SettingsManager(getApplicationContext());
            UserEvent event = new UserEvent(mEventName.getText().toString(), setMng.getUserID(), setMng.getUserName(),
                    startDate, endDate, location);

            // TODO send event to server (server-side code not written yet :( ), and use the returned event id
            // in setID
            event.setID(5);
            event.setDescription(mDescription.getText().toString());
            event.setPositionName(mPlaceName.getText().toString());

            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            dbHelper.addEvent(event);

            Toast.makeText(mContext, "Event created!", Toast.LENGTH_SHORT).show();
            finish();

        }
    }
}