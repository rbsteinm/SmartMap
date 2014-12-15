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
import ch.epfl.smartmap.util.Utils;

/**
 * A simple date picker. Used in {@link ch.epfl.smartmap.activities.AddEventActivity}. When the date is set, the
 * associated EditText is modified accordingly and the associated <code>Calendar</code> is also updated.
 *
 * @author SpicyCH
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final EditText mPickDate;
    private final Calendar mCalendar;

    /**
     *
     * Constructor
     *
     * @param e
     * @param calendar
     */
    public DatePickerFragment(EditText e, Calendar calendar) {
        mPickDate = e;
        mCalendar = calendar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the previously set date to initilize the picker.
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(this.getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        // We want these changes to have effect on the Calendar passed during construction.
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);

        mPickDate.setText(Utils.getDateString(mCalendar));
    }

}