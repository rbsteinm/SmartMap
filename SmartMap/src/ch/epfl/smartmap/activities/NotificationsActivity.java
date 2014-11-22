package ch.epfl.smartmap.activities;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.gui.FriendListItemAdapter;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * This activity displays the notifications received
 * 
 * @author agpmilli
 */
public class NotificationsActivity extends ListActivity {

	private TextView mNotificationText;
	private TextView mNotificationTitle;
	private Context mContext;
	private DatabaseHelper mDbHelper;
	private NetworkSmartMapClient mNetworkClient;
	private List<User> mInvitationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_notifications);

		mContext = this.getBaseContext();
		mDbHelper = DatabaseHelper.getInstance();
		mNetworkClient = NetworkSmartMapClient.getInstance();

		// mInvitationList = mDbHelper.getInvitations();
		new RefreshInvitationsList().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		// This is needed to show an update of the events' list after having
		// created an event
		new RefreshInvitationsList().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.show_events, menu);
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
			case R.id.showEventsMenuNewEvent:
				Intent showEventIntent = new Intent(mContext,
				    AddEventActivity.class);
				this.startActivity(showEventIntent);
			default:
				// No other menu items!
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final User user = (User) this.findViewById(position).getTag();

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(user.getName() + " "
		    + mContext.getString(R.string.notification_invitation_accepted));
		// alertDialog.setMessage(message);
		final Activity activity = this;
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Show invitation",
		    new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface dialog, int id) {

				    Toast.makeText(activity, "Opening event on the map...",
				        Toast.LENGTH_SHORT).show();

				    Intent showFriendIntent = new Intent(mContext,
				        MainActivity.class);
				    NotificationsActivity.this.startActivity(showFriendIntent);

			    }
		    });

		alertDialog.show();

		super.onListItemClick(l, v, position, id);
	}

	/**
	 * AsyncTask that refreshes the invitations list after the user answered to
	 * an invitation and each time the activity is resumed. It also retrieves
	 * accepted invitations and store them in the application cache.
	 * 
	 * @author marion-S
	 */
	private class RefreshInvitationsList extends
	    AsyncTask<String, Void, List<List<User>>> {

		@Override
		protected List<List<User>> doInBackground(String... params) {
			try {

				return mNetworkClient.getInvitations();

			} catch (SmartMapClientException e) {
				return Collections.emptyList();
			}
		}

		@Override
		protected void onPostExecute(List<List<User>> list) {
			super.onPostExecute(list);
			NotificationsActivity.this
			    .setListAdapter(new FriendListItemAdapter(mContext, list.get(0)));
			for (User newFriend : list.get(1)) {
				mDbHelper.addUser(newFriend);
				new AckAcceptedInvitations().execute(newFriend.getID());
			}
		}

	}

	/**
	 * AsyncTask that confirms the server that accepted invitations were
	 * received
	 * 
	 * @author marion-S
	 */
	private class AckAcceptedInvitations extends AsyncTask<Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			try {
				mNetworkClient.ackAcceptedInvitation(params[0]);
			} catch (SmartMapClientException e) {
			}
			return null;
		}

	}
}