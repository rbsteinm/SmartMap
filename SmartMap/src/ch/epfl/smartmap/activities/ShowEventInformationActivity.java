package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.gui.EventsListItemAdapter;

/**
 * This activity shows an event in a complete screens.
 * 
 * @author SpicyCH
 */
public class ShowEventInformationActivity extends Activity {

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

		mEvent = this.getIntent().getParcelableExtra("EVENT");

		if (mEvent == null) {
			// This activity needs an event to function properly
			this.finish();
		}

		this.initializeGUI();

	}

	public void inviteFriendsToEvent(View v) {
		Toast.makeText(mContext, "Inviting friends..", Toast.LENGTH_SHORT).show();
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
		switch (id) {
			case R.id.action_settings:
				return true;
			case android.R.id.home:
				if (this.getIntent().getBooleanExtra("NOTIFICATION", false) == true) {
					this.startActivity(new Intent(this, MainActivity.class));
				}
				this.finish();
			default:
				break;
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
		mEventCreator.setText(this.getString(R.string.show_event_by) + " "
		    + Cache.getInstance().getUserById(mEvent.getCreatorId()).getName());

		mStart = (TextView) this.findViewById(R.id.show_event_info_start);
		mStart.setText(EventsListItemAdapter.getTextFromDate(mEvent.getStartDate(), mEvent.getEndDate(),
		    "start"));

		mEnd = (TextView) this.findViewById(R.id.show_event_info_end);
		mEnd.setText(EventsListItemAdapter.getTextFromDate(mEvent.getStartDate(), mEvent.getEndDate(), "end"));

		mEventDescription = (TextView) this.findViewById(R.id.show_event_info_description);
		mEventDescription.setText(this.getString(R.string.show_event_info_event_description) + ": "
		    + mEvent.getDescription());

		mPlaceNameAndCountry = (TextView) this.findViewById(R.id.show_event_info_town_and_country);
		mPlaceNameAndCountry.setText(mEvent.getLocationString() + ", " + "Country");
	}

	@Override
	public void onBackPressed() {
		if (this.getIntent().getBooleanExtra("NOTIFICATION", false) == true) {
			this.startActivity(new Intent(this, MainActivity.class));
		}
		this.finish();
	}
}
