package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.gui.EventsListItemAdapter;

/**
 * This activity shows an event in a complete screens.
 * 
 * 
 * @author SpicyCH
 */
public class ShowEventInformationActivity extends Activity {

    private static final String TAG = ShowEventInformationActivity.class.getSimpleName();
    private Context mContext;
    private Event mEvent;
    private TextView mEventTitle;
    private TextView mEventCreator;
    private TextView mStart;
    private TextView mEnd;
    private TextView mEventDescription;
    private TextView mPlaceNameAndCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_event_information);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

        if (this.getIntent().getParcelableExtra("event") != null) {
            mEvent = this.getIntent().getParcelableExtra("event");
            Log.d(TAG, "City name: " + mEvent.getPositionName());
            Log.d(TAG, "Country name: " + mEvent.getPositionCountryName());
        }

        if (mEvent != null) {
            this.initializeGUI();
        }

    }

    /**
     * Triggered when the user clicks the "Invite friends" button.<br />
     * It launches InviteFriendsActivity for a result.
     * 
     * @param v
     * 
     * @author SpicyCH
     */
    public void inviteFriendsToEvent(View v) {
        Intent inviteFriends = new Intent(mContext, InviteFriendsActivity.class);
        this.startActivityForResult(inviteFriends, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.show_event_information, menu);
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

    public void openMapAtEventLocation(View v) {
        Intent showEventIntent = new Intent(mContext, MainActivity.class);
        showEventIntent.putExtra("location", mEvent.getLocation());
        this.startActivity(showEventIntent);
    }

    /**
     * Initializes the different views of this activity.
     * 
     * @author SpicyCH
     */
    private void initializeGUI() {

        mContext = this.getApplicationContext();
        this.setTitle(mEvent.getName());

        mEventTitle = (TextView) this.findViewById(R.id.show_event_info_event_name);
        mEventTitle.setText(mEvent.getName());

        mEventCreator = (TextView) this.findViewById(R.id.show_event_info_creator);
        mEventCreator.setText(this.getString(R.string.show_event_by) + " " + mEvent.getCreatorName());

        mStart = (TextView) this.findViewById(R.id.show_event_info_start);
        mStart.setText(EventsListItemAdapter.getTextFromDate(mEvent.getStartDate(), mEvent.getEndDate(), "start"));

        mEnd = (TextView) this.findViewById(R.id.show_event_info_end);
        mEnd.setText(EventsListItemAdapter.getTextFromDate(mEvent.getStartDate(), mEvent.getEndDate(), "end"));

        mEventDescription = (TextView) this.findViewById(R.id.show_event_info_description);
        mEventDescription.setText(this.getString(R.string.show_event_info_event_description) + ":\n"
                + mEvent.getDescription());

        mPlaceNameAndCountry = (TextView) this.findViewById(R.id.show_event_info_town_and_country);
        mPlaceNameAndCountry.setText(mEvent.getPositionName() + ", " + mEvent.getPositionCountryName());
    }
}
