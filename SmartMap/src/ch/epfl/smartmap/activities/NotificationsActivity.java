package ch.epfl.smartmap.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.gui.FriendListItemAdapter;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.NotificationBag;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * This activity displays the notifications received
 * 
 * @author agpmilli
 */
public class NotificationsActivity extends ListActivity {

    private Context mContext;

    private DatabaseHelper mDbHelper;

    private NetworkSmartMapClient mNetworkClient;

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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent showFriendIntent = new Intent(mContext, FriendsPagerActivity.class);
        NotificationsActivity.this.startActivity(showFriendIntent);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onResume() {
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
        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * AsyncTask that refreshes the invitations list after the user answered to
     * an invitation and each time the activity is resumed. It also retrieves
     * accepted invitations and store them in the application cache.
     * 
     * @author marion-S
     */
    private class RefreshInvitationsList extends AsyncTask<String, Void, NotificationBag> {

        @Override
        protected NotificationBag doInBackground(String... params) {
            try {

                return mNetworkClient.getInvitations();

            } catch (SmartMapClientException e) {
                // FIXME what to return??
                return null;
            }
        }

        @Override
        protected void onPostExecute(NotificationBag notificationBag) {
            super.onPostExecute(notificationBag);
            NotificationsActivity.this.setListAdapter(new FriendListItemAdapter(mContext, notificationBag
                .getInvitingUsers()));
            for (User newFriend : notificationBag.getNewFriends()) {
                mDbHelper.addUser(newFriend);
                new AckAcceptedInvitations().execute(newFriend.getID());
            }
        }

    }
}