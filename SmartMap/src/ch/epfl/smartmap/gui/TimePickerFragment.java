package ch.epfl.smartmap.gui;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;
import ch.epfl.smartmap.util.Utils;

/**
 * A simple time picker. Used in {@link ch.epfl.smartmap.activities.AddEventActivity}. When the time is set, the
 * associated EditText is modified accordingly and a tag containing an int array of hour, minute is linked to this
 * EditText.
 * 
 * @author SpicyCH
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private final EditText mPickTimeEditText;
    private final Calendar mCalendar;

    public TimePickerFragment(EditText e, Calendar calendar) {
        mPickTimeEditText = e;
        mCalendar = calendar;
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
        Calendar timeSet = GregorianCalendar.getInstance(TimeZone.getTimeZone(Utils.GMT_SWITZERLAND));
        timeSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
        timeSet.set(Calendar.MINUTE, minute);

        // We want these changes to have effect on the Calendar passed during construction.
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);

        mPickTimeEditText.setText(Utils.getTimeString(timeSet));
    }
}