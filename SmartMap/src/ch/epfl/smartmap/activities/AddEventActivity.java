package ch.epfl.smartmap.activities;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
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
import ch.epfl.smartmap.cache.PublicEvent;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.gui.DatePickerFragment;
import ch.epfl.smartmap.gui.TimePickerFragment;

import com.google.android.gms.maps.model.LatLng;

/**
 * This activity lets the user create a new event.
 * 
 * @author SpicyCH
 */
public class AddEventActivity extends FragmentActivity {

	@SuppressWarnings("unused")
	private static final String TAG = AddEventActivity.class.getSimpleName();

	static final int PICK_LOCATION_REQUEST = 1;
	private static final String CITY_NAME = "CITY_NAME";
	private static final int ELEMENTS_HH_MM = 2;
	private static final int ELEMENTS_JJ_DD_YYYY = 3;

	private Context mContext;
	private EditText mDescription;
	private EditText mEventName;
	private EditText mLatitude;
	private EditText mLongitude;
	private int mNewEventId = 0;
	private EditText mPickEndDate;
	private EditText mPickEndTime;
	private EditText mPickStartDate;
	private EditText mPickStartTime;
	private EditText mPlaceName;

	private TextWatcher mTextChangedListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_add_event);

		// Makes the logo clickable (clicking it returns to previous activity)
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

		this.initializeGUIComponents();

		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			LatLng latLng = extras.getParcelable(LOCATION_SERVICE);
			if ((latLng != null) && (Math.abs(latLng.latitude) > 0)) {
				// The user long clicked the map in MainActivity and wants to
				// create an event
				this.updateLocation(this.getIntent());
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PICK_LOCATION_REQUEST:
				if (resultCode == RESULT_OK) {
					// All went smoothly, update location in this activity
					this.updateLocation(data);

				} else {
					// Google wasn't able to retrieve the location name associated to the coordinates
					Toast
					    .makeText(mContext,
					        this.getString(R.string.add_event_toast_couldnt_retrieve_location),
					        Toast.LENGTH_LONG).show();
					mLatitude.setText("");
					mLongitude.setText("");
					mPlaceName.setText("");
				}
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.add_event, menu);
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
				this.finish();
				break;
			case R.id.addEventButtonCreateEvent:
				this.createEvent();
				break;
			default:
				// No other menu items!
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void pickLocation(View v) {
		Toast.makeText(mContext,
		    this.getString(R.string.add_event_toast_indication_long_click_map_to_create_event),
		    Toast.LENGTH_LONG).show();

		Intent pickLocationIntent = new Intent(mContext, MainActivity.class);
		pickLocationIntent.putExtra("pickLocationForEvent", true);
		pickLocationIntent.setType(Context.LOCATION_SERVICE);
		this.startActivityForResult(pickLocationIntent, PICK_LOCATION_REQUEST);
	}

	/**
	 * Ensures the end of the event is after its start and end of the event is not in the past. Displays a
	 * toast and
	 * reset the bad field set by the user if necessary.
	 * 
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 * @author SpicyCH
	 */
	private void checkDatesValidity(EditText startDate, EditText startTime, EditText endDate, EditText endTime) {

		if (this.isValidDate(endDate.getText().toString()) && this.isValidTime(endTime.getText().toString())) {
			// The end of the event has been set by the user

			GregorianCalendar start = this.getDateFromTextFormat(startDate.getText().toString(), startTime
			    .getText().toString());

			GregorianCalendar end = this.getDateFromTextFormat(endDate.getText().toString(), endTime
			    .getText().toString());

			GregorianCalendar now = new GregorianCalendar();

			// Needed to let the user click the default time without errors.
			now.add(GregorianCalendar.MINUTE, -1);

			if (end.before(start)) {
				// The user is trying to create the end of the event before its
				// start!

				endDate.setText("");
				endTime.setText("");

				Toast.makeText(mContext,
				    this.getString(R.string.add_event_toast_event_cannot_end_before_starting),
				    Toast.LENGTH_LONG).show();
			} else if (end.before(now)) {
				// The user is trying to create an event in the past

				endDate.setText("");
				endTime.setText("");

				Toast.makeText(mContext,
				    this.getString(R.string.add_event_toast_event_end_cannot_be_in_past), Toast.LENGTH_LONG)
				    .show();
			}
		}

	}

	/**
	 * @author SpicyCH
	 */
	private void createEvent() {

		if (!this.isValidDate(mPickEndDate.getText().toString())
		    || !this.isValidTime(mPickEndTime.getText().toString())
		    || mLatitude.getText().toString().equals("") || mLongitude.getText().toString().equals("")
		    || ((mPlaceName.getText().toString() == null) || mEventName.getText().toString().isEmpty())) {
			Toast.makeText(mContext, this.getString(R.string.add_event_toast_not_all_fields_set),
			    Toast.LENGTH_SHORT).show();
		} else {
			GregorianCalendar startDate = this.getDateFromTextFormat(mPickStartDate.getText().toString(),
			    mPickStartTime.getText().toString());
			GregorianCalendar endDate = this.getDateFromTextFormat(mPickEndDate.getText().toString(),
			    mPickEndTime.getText().toString());

			double latitude = Double.parseDouble(mLatitude.getText().toString());
			double longitude = Double.parseDouble(mLongitude.getText().toString());
			Location location = new Location("Location set by user");
			location.setLatitude(latitude);
			location.setLongitude(longitude);

			SettingsManager setMng = SettingsManager.getInstance();
			PublicEvent event = new PublicEvent(mEventName.getText().toString(), setMng.getUserID(),
			    setMng.getUserName(), startDate, endDate, location);

			// TODO send event to server (server-side code not written yet :( ),
			// and use the returned event id
			// in setID
			event.setID(mNewEventId++);
			event.setDescription(mDescription.getText().toString());
			event.setPositionName(mPlaceName.getText().toString());

			DatabaseHelper dbHelper = DatabaseHelper.getInstance();
			dbHelper.addEvent(event);

			Toast.makeText(mContext, this.getString(R.string.add_event_toast_event_created),
			    Toast.LENGTH_SHORT).show();
			this.finish();

		}
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
		assert this.isValidDate(dayMonthYear) : "The string dayMonthYear isn't in the expected format";
		assert this.isValidTime(hourMinute) : "The string hourMinute isn't in the expected format";

		String[] s1 = dayMonthYear.split("/");
		String[] s2 = hourMinute.split(":");
		// Don't forget to substract 1 to the month in text format
		GregorianCalendar date = new GregorianCalendar(Integer.parseInt(s1[2]), Integer.parseInt(s1[1]) - 1,
		    Integer.parseInt(s1[0]), Integer.parseInt(s2[0]), Integer.parseInt(s2[1]), 0);

		return date;
	}

	/**
	 * @author SpicyCH
	 */
	private void initializeGUIComponents() {
		mContext = this.getApplicationContext();
		mEventName = (EditText) this.findViewById(R.id.addEventEventName);
		mPickStartDate = (EditText) this.findViewById(R.id.addEventEventDate);
		mPickStartTime = (EditText) this.findViewById(R.id.addEventEventTime);
		mPickEndTime = (EditText) this.findViewById(R.id.addEventEndTime);
		mPickEndDate = (EditText) this.findViewById(R.id.addEventEndDate);
		mLatitude = (EditText) this.findViewById(R.id.addEventLatitude);
		mLongitude = (EditText) this.findViewById(R.id.addEventLongitude);
		mDescription = (EditText) this.findViewById(R.id.addEventDescription);
		mPlaceName = (EditText) this.findViewById(R.id.addEventPlaceName);

		mLongitude.setEnabled(false);
		mLatitude.setEnabled(false);

		mTextChangedListener = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// Remove the TextChangedListener to avoid useless calls
				// triggered by the following code
				mPickEndDate.removeTextChangedListener(mTextChangedListener);
				mPickStartDate.removeTextChangedListener(mTextChangedListener);

				AddEventActivity.this.checkDatesValidity(mPickStartDate, mPickStartTime, mPickEndDate,
				    mPickEndTime);

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
				newFragment.show(AddEventActivity.this.getSupportFragmentManager(), "timePicker");
			}

		});

		mPickStartDate.setText(now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/"
		    + now.get(Calendar.YEAR));

		mPickStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(mPickStartDate);
				newFragment.show(AddEventActivity.this.getSupportFragmentManager(), "datePicker");
			}
		});

		mPickEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(mPickEndDate);
				newFragment.show(AddEventActivity.this.getSupportFragmentManager(), "datePicker");
			}
		});

		mPickEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment(mPickEndTime);
				newFragment.show(AddEventActivity.this.getSupportFragmentManager(), "timePicker");
			}
		});
	}

	private boolean isValidDate(String s) {
		String[] sArray = s.split("/");
		return sArray.length == ELEMENTS_JJ_DD_YYYY;
	}

	private boolean isValidTime(String s) {
		String[] sArray = s.split(":");
		return sArray.length == ELEMENTS_HH_MM;
	}

	/**
	 * @author SpicyCH
	 */
	private void updateLocation(Intent data) {
		Bundle extras = data.getExtras();

		LatLng latLng = extras.getParcelable(LOCATION_SERVICE);
		mLatitude.setText(String.valueOf(latLng.latitude));
		mLongitude.setText(String.valueOf(latLng.longitude));

		String cityName = extras.getString(CITY_NAME);
		if ((cityName != null) && !cityName.equals("")) {
			mPlaceName.setText(cityName);
		} else {
			Toast.makeText(mContext, this.getString(R.string.add_event_toast_couldnt_retrieve_location_name),
			    Toast.LENGTH_LONG).show();
			mPlaceName.setText("");
		}
	}
}