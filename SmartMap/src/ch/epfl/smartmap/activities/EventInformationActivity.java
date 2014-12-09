package ch.epfl.smartmap.activities;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.gui.EventsListItemAdapter;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.map.DefaultZoomManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This activity shows an event in a complete screens. It display in addition
 * two buttons: one to invite friends, and
 * one to see the event on the map.
 * 
 * @author SpicyCH
 * @author agpmilli
 */
public class EventInformationActivity extends FragmentActivity {

    class LoadEventTask extends AsyncTask<Long, Void, Map<String, Object>> {
        @Override
        protected Map<String, Object> doInBackground(Long... params) {

            Log.d(TAG, "Retrieving event...");

            long eventId = params[0];

            Map<String, Object> output = new HashMap<String, Object>();

            Event event = ServiceContainer.getCache().getEvent(eventId);
            output.put(EVENT_KEY, event);

            output.put(CREATOR_NAME_KEY, event.getCreator().getName());

            return output;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {

            Log.d(TAG, "Processing event...");

            final Event event = (Event) result.get(EVENT_KEY);
            final String creatorName = (String) result.get(CREATOR_NAME_KEY);

            if ((event == null) || (creatorName == null)) {
                Log.e(TAG, "The server returned a null event or creatorName");

                Toast.makeText(mContext, mContext.getString(R.string.show_event_server_error), Toast.LENGTH_SHORT)
                    .show();

            } else {
                mEvent = event;
                EventInformationActivity.this.initializeGUI(event, creatorName);
            }
        }
    }

    private static final String TAG = EventInformationActivity.class.getSimpleName();

    private static final int GOOGLE_PLAY_REQUEST_CODE = 10;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mFragmentMap;
    private Context mContext;
    private Event mEvent;
    private TextView mEventTitle;
    private TextView mEventCreator;
    private TextView mStart;
    private TextView mEnd;
    private TextView mEventDescription;

    private TextView mPlaceNameAndCountry;
    /**
     * Used to get the event id the getExtra of the starting intent, and to pass
     * the retrieved event from doInBackground
     * to onPostExecute.
     */
    private static final String EVENT_KEY = "EVENT";

    private static final String CREATOR_NAME_KEY = "CREATOR_NAME";

    /**
     * Display the map with the current location
     * 
     * @author agpmilli
     */
    public void displayMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getBaseContext());
        // Showing status
        if (status != ConnectionResult.SUCCESS) {
            // Google Play Services are not available
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, GOOGLE_PLAY_REQUEST_CODE);
            dialog.show();
        } else {
            // Google Play Services are available.
            // Getting reference to the SupportMapFragment of activity_main.xml
            mFragmentMap =
                (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.show_event_info_map);
            // Getting GoogleMap object from the fragment
            mGoogleMap = mFragmentMap.getMap();
            // Enabling MyLocation Layer of Google Map
            mGoogleMap.setMyLocationEnabled(true);

            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

            mGoogleMap.addMarker(new MarkerOptions().position(mEvent.getLatLng()));

            new DefaultZoomManager(mFragmentMap).zoomWithAnimation(mEvent.getLatLng());
        }
    }

    /**
     * Initializes the different views of this activity.
     * 
     * @author SpicyCH
     */
    private void initializeGUI(final Event mEvent, String creatorName) {

        this.setTitle(mEvent.getName());

        mEventTitle = (TextView) this.findViewById(R.id.show_event_info_event_name);
        mEventTitle.setText(mEvent.getName());

        mEventCreator = (TextView) this.findViewById(R.id.show_event_info_creator);
        mEventCreator.setText(this.getString(R.string.show_event_by) + " " + creatorName);

        mStart = (TextView) this.findViewById(R.id.show_event_info_start);
        mEnd = (TextView) this.findViewById(R.id.show_event_info_end);

        String[] result = EventsListItemAdapter.getTextFromDate(mEvent.getStartDate(), mEvent.getEndDate(), mContext);

        mStart.setText(result[0]);
        mEnd.setText(result[1]);

        mEventDescription = (TextView) this.findViewById(R.id.show_event_info_description);
        mEventDescription.setText(this.getString(R.string.show_event_info_event_description) + ":\n"
            + mEvent.getDescription());

        mPlaceNameAndCountry = (TextView) this.findViewById(R.id.show_event_info_town_and_country);
        mPlaceNameAndCountry.setText(mEvent.getLocationString() + ", "
            + Utils.getCountryFromLocation(mEvent.getLocation()));

        this.displayMap();

        mGoogleMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                if (mEvent.getLatLng() != null) {
                    mGoogleMap.clear();
                }
                EventInformationActivity.this.openMapAtEventLocation(null);
            }
        });
    }

    /**
     * Triggered when the user clicks the "Invite friends" button.<br />
     * It launches InviteFriendsActivity for a result.
     * 
     * @param v
     * @author SpicyCH
     */
    public void inviteFriendsToEvent(View v) {

        // Hack so that SonarQube doesn't complain that v is not used
        Log.d(TAG, "View with id " + v.getId() + " clicked");
        Intent inviteFriends = new Intent(this, InviteFriendsActivity.class);
        this.startActivityForResult(inviteFriends, 1);
    }

    @Override
    public void onBackPressed() {
        this.onNotificationOpen();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_event_information);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

        mContext = this.getApplicationContext();

        // This activity needs a (positive) event id to process. If none given,
        // we finish it.
        if (this.getIntent().getLongExtra(EVENT_KEY, -1) > 0) {

            long eventId = this.getIntent().getLongExtra(EVENT_KEY, -1);

            Log.d(TAG, "Received event id " + eventId);

            // Need an AsyncTask because getEventById searches on our server if
            // event not stored in cache.
            LoadEventTask loadEvent = new LoadEventTask();
            loadEvent.execute(eventId);
        } else {
            Log.e(TAG, "No event id put in the putextra of the intent that started this activity.");
            Toast.makeText(mContext, mContext.getString(R.string.error_client_side), Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.show_event_information, menu);
        return true;
    }

    /**
     * When this tab is open by a notification
     */
    private void onNotificationOpen() {
        if (this.getIntent().getBooleanExtra("NOTIFICATION", false)) {
            this.startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.onNotificationOpen();
                this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Triggered when the button 'Shop on the map' is pressed. Opens the map at
     * the location of the event.
     * 
     * @author SpicyCH
     */
    public void openMapAtEventLocation(View v) {
        Intent showEventIntent = new Intent(this, MainActivity.class);
        showEventIntent.putExtra(AddEventActivity.LOCATION_EXTRA, mEvent.getLocation());
        this.startActivity(showEventIntent);
    }
}