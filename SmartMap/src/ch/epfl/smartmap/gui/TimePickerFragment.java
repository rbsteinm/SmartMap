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
 * A simple time picker. Used in {@link ch.epfl.smartmap.activities.AddEventActivity}.
 * When the time is set, the
 * associated EditText is modified accordingly and a tag containing an int array of hour, minute is linked to this
 * EditText.
 *
 * @author SpicyCH
 *
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private final static int TEN = 10;

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
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mPickTimeEditText.setText(formatForClock(hourOfDay) + ":" + formatForClock(minute));
        mPickTimeEditText.setTag(new int[] {hourOfDay, minute});
    }

    public static String formatForClock(int time) {
        /**
         * @param time
         *            a second, minute or hour of the format 0, 24
         * @return a String prefixed with 0 and the time if time < 10
         * @author SpicyCH
         */
        String hourOfDayString = "";
        if (time < TEN) {
            hourOfDayString += "0" + time;
        } else {
            hourOfDayString += time;
        }

        return hourOfDayString;
    }
}