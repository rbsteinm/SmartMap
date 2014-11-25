package ch.epfl.smartmap.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.DatabaseHelper;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.show_events, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent showFriendIntent = new Intent(mContext, FriendsPagerActivity.class);
        NotificationsActivity.this.startActivity(showFriendIntent);

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
        // This is needed to show an update of the events' list after having
        // created an event
        // NotificationsActivity.this.setListAdapter(new
        // InvitationListItemAdapter(mContext, mDbHelper
        // .getInvitations()));
    }
}