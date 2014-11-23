/**
 *
 */
package ch.epfl.smartmap.gui;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * A simple date picker. Used in {@link ch.epfl.smartmap.activities.AddEventActivity}. When the date is set,
 * the associated EditText is modified accordingly and a tag containing an int
 * array of year, month, day is linked to this EditText.
 * 
 * @author SpicyCH
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final EditText mPickDate;

    /**
     * Constructor
     * 
     * @param e
     *            the EditText associated with this date
     */
    public DatePickerFragment(EditText e) {
        mPickDate = e;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(this.getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mPickDate.setText(TimePickerFragment.formatForClock(day) + "/"
            + TimePickerFragment.formatForClock(month + 1) + "/" + year);
        mPickDate.setTag(new int[]{year, month, day});
    }

}