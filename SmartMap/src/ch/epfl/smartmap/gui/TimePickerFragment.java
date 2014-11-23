package ch.epfl.smartmap.gui;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * A simple time picker. Used in {@link ch.epfl.smartmap.activities.AddEventActivity}. When the time is set,
 * the associated EditText is modified accordingly and a tag containing an int
 * array of hour, minute is linked to this EditText.
 * 
 * @author SpicyCH
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	private final static int FIRST_2DIGITS_NUMBER = 10;

	/**
	 * @param time
	 *            a second, minute, hour, day or month
	 * @return the time prefixed with 0 if it was < 10
	 * @author SpicyCH
	 */
	public static String formatForClock(int time) {
		String hourOfDayString = "";
		if (time < FIRST_2DIGITS_NUMBER) {
			hourOfDayString += "0" + time;
		} else {
			hourOfDayString += time;
		}

		return hourOfDayString;
	}

	private final EditText mPickTimeEditText;

	public TimePickerFragment(EditText e) {
		mPickTimeEditText = e;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(this.getActivity(), this, hour, minute, DateFormat.is24HourFormat(this
		    .getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mPickTimeEditText.setText(formatForClock(hourOfDay) + ":" + formatForClock(minute));
		mPickTimeEditText.setTag(new int[] {hourOfDay, minute});
	}
}