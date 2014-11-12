package ch.epfl.smartmap.activities;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.gui.TimePickerFragment;

/**
 * This activity lets the user create a new event
 *
 * @author SpicyCH
 *
 */
public class AddEventActivity extends FragmentActivity {

    private EditText mPickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // TODO make keyboard appear when activity is started (facebook like)
        EditText eventName = (EditText) findViewById(R.id.addEventEventName);

        Calendar now = Calendar.getInstance();

        mPickTime = (EditText) findViewById(R.id.addEventEventTime);

        mPickTime.setText(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));

        mPickTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(mPickTime);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_event, menu);
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
}