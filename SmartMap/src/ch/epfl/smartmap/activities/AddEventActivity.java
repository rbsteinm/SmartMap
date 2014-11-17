package ch.epfl.smartmap.activities;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    static final int PICK_LOCATION_REQUEST = 1;

    private static final String CITY_NAME = "CITY_NAME";

    protected static final String TAG = AddEventActivity.class.getSimpleName();

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
    private TextWatcher mTextChangedListener;

    private int mNewEventId = 0;

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

        if (isValidDate(endDate.getText().toString()) && isValidTime(endTime.getText().toString())) {
            Log.d(TAG, "running the if of checkDatesValidity");
            // The end of the event has been set by the user

            GregorianCalendar start = getDateFromTextFormat(startDate.getText().toString(), startTime.getText()
                    .toString());

            GregorianCalendar end = getDateFromTextFormat(endDate.getText().toString(), endTime.getText().toString());

            GregorianCalendar now = new GregorianCalendar();

            if (end.before(start)) {
                // The user is trying to create the end of the event before its start!

                endDate.setText("End Date");
                endTime.setText("End Time");

                Toast.makeText(mContext, "The event cannot end before it begins!", Toast.LENGTH_LONG).show();
            } else if (end.before(now)) {
                // The user is trying to create an event in the past

                endDate.setText("End Date");
                endTime.setText("End Time");

                Toast.makeText(mContext, "The event's end cannot be in the past!", Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     *
     * @author SpicyCH
     */
    private void createEvent() {

        if (!isValidDate(mPickEndDate.getText().toString()) || !isValidTime(mPickEndTime.getText().toString())
                || mLatitude.getText().toString().equals("") || mLongitude.getText().toString().equals("")
                || mPlaceName.getText().toString().equals("")) {
            Toast.makeText(mContext, "Cannot create event: please specify all fields!", Toast.LENGTH_SHORT).show();
        } else {
            GregorianCalendar startDate = getDateFromTextFormat(mPickStartDate.getText().toString(), mPickStartTime
                    .getText().toString());
            GregorianCalendar endDate = getDateFromTextFormat(mPickEndDate.getText().toString(), mPickEndTime.getText()
                    .toString());

            double latitude = Double.parseDouble(mLatitude.getText().toString());
            double longitude = Double.parseDouble(mLongitude.getText().toString());
            Location location = new Location("Location set by user");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            SettingsManager setMng = SettingsManager.getInstance();
            UserEvent event = new UserEvent(mEventName.getText().toString(), setMng.getUserID(), setMng.getUserName(),
                    startDate, endDate, location);

            // TODO send event to server (server-side code not written yet :( ), and use the returned event id
            // in setID
            event.setID(mNewEventId++);
            event.setDescription(mDescription.getText().toString());
            event.setPositionName(mPlaceName.getText().toString());

            DatabaseHelper dbHelper = DatabaseHelper.getInstance();
            dbHelper.addEvent(event);

            Toast.makeText(mContext, "Event created!", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    /**
     *
     * @author SpicyCH
     */
    private void initializeGUIComponents() {
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

        mLongitude.setEnabled(false);
        mLatitude.setEnabled(false);

        mTextChangedListener = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // Remove the TextChangedListener to avoid useless calls triggered by the following code
                mPickEndDate.removeTextChangedListener(mTextChangedListener);
                mPickStartDate.removeTextChangedListener(mTextChangedListener);

                checkDatesValidity(mPickStartDate, mPickStartTime, mPickEndDate, mPickEndTime);
                Log.d(TAG, "Text changed!");

                // Reset the TextChangedListener
                mPickEndDate.addTextChangedListener(mTextChangedListener);
                mPickStartDate.addTextChangedListener(mTextChangedListener);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        mPickStartDate.addTextChangedListener(mTextChangedListener);
        mPickStartTime.addTextChangedListener(mTextChangedListener);
        mPickEndDate.addTextChangedListener(mTextChangedListener);
        mPickEndTime.addTextChangedListener(mTextChangedListener);

        GregorianCalendar now = new GregorianCalendar();

        mPickStartTime.setText(TimePickerFragment.formatForClock(now.get(Calendar.HOUR_OF_DAY)) + ":"
                + TimePickerFragment.formatForClock(now.get(Calendar.MINUTE)));

        mPickStartTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(mPickStartTime);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }

        });

        mPickStartDate.setText(now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/"
                + now.get(Calendar.YEAR));

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Makes the logo clickable (clicking it returns to previous activity)
        getActionBar().setDisplayHomeAsUpEnabled(true);

        initializeGUIComponents();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            LatLng latLng = extras.getParcelable(LOCATION_SERVICE);
            if (latLng != null && Math.abs(latLng.latitude) > 0) {
                // The user long clicked the map in MainActivity and wants to create an event
                updateLocation(getIntent());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_LOCATION_REQUEST:
                if (resultCode == RESULT_OK) {
                    // All went smoothly, update location in this activity
                    updateLocation(data);

                } else {
                    Toast.makeText(mContext, "Sorry, couldn't get the location of your event", Toast.LENGTH_LONG)
                            .show();
                    mLatitude.setText("");
                    mLongitude.setText("");
                    mPlaceName.setText("");
                }
                break;
            default:
                break;
        }
    }

    /**
     *
     * @author SpicyCH
     */
    private void updateLocation(Intent data) {
        Bundle extras = data.getExtras();

        LatLng latLng = extras.getParcelable(LOCATION_SERVICE);
        mLatitude.setText(String.valueOf(latLng.latitude));
        mLongitude.setText(String.valueOf(latLng.longitude));

        String cityName = extras.getString(CITY_NAME);
        if (cityName != null && !cityName.equals("")) {
            mPlaceName.setText(cityName);
        } else {
            Toast.makeText(mContext,
                    "Sorry, couldn't retrieve the name of your event's place. Please specify it manually.",
                    Toast.LENGTH_LONG).show();
            mPlaceName.setText("");
        }
    }

    public void pickLocation(View v) {
        Toast.makeText(mContext, "Long click the map at the location of your event", Toast.LENGTH_LONG).show();

        Intent pickLocationIntent = new Intent(mContext, MainActivity.class);
        pickLocationIntent.putExtra("pickLocationForEvent", true);
        pickLocationIntent.setType(Context.LOCATION_SERVICE);
        startActivityForResult(pickLocationIntent, PICK_LOCATION_REQUEST);

    }

    /**
     * @param dayMonthYear
     *            a String like "16/09/1993"
     * @param hourMinute
     *            a String like "17:03"
     * @return a GregorianDate constructed from the given parameters
     * @author SpicyCH
     */
    private GregorianCalendar getDateFromTextFormat(String dayMonthYear, String hourMinute) {
        assert isValidDate(dayMonthYear) : "The string dayMonthYear isn't in the expected format";
        assert isValidTime(hourMinute) : "The string hourMinute isn't in the expected format";

        String[] s1 = dayMonthYear.split("/");
        String[] s2 = hourMinute.split(":");
        // Don't forget to substract 1 to the month in text format
        GregorianCalendar date = new GregorianCalendar(Integer.parseInt(s1[2]), Integer.parseInt(s1[1]) - 1,
                Integer.parseInt(s1[0]), Integer.parseInt(s2[0]), Integer.parseInt(s2[1]), 0);

        return date;
    }

    private boolean isValidDate(String s) {
        String[] sArray = s.split("/");
        return sArray.length == 3;
    }

    private boolean isValidTime(String s) {
        String[] sArray = s.split(":");
        return sArray.length == 2;
    }
}