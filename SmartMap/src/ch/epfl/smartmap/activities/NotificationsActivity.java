package ch.epfl.smartmap.activities;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.FriendInvitation;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.gui.InvitationListItemAdapter;

/**
 * This activity displays the notifications received
 * 
 * @author agpmilli
 */
public class NotificationsActivity extends ListActivity {

	@SuppressWarnings("unused")
	private static final String TAG = NotificationsActivity.class.getSimpleName();

	private Context mContext;

	private DatabaseHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_notifications);

		mContext = this.getBaseContext();
		mDbHelper = DatabaseHelper.getInstance();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent invitationIntent = ((FriendInvitation) l.getItemAtPosition(position)).getIntent();
		NotificationsActivity.this.startActivity(invitationIntent);

		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		NotificationsActivity.this.setListAdapter(new InvitationListItemAdapter(mContext, mDbHelper
		    .getFriendInvitations()));

		List<FriendInvitation> unreadInvitations = mDbHelper.getFriendInvitationsByStatus(Invitation.UNREAD);
		for (int i = 0; i < unreadInvitations.size(); i++) {
			unreadInvitations.get(i).setStatus(Invitation.READ);
			mDbHelper.updateFriendInvitation(unreadInvitations.get(i));
		}

	}
}