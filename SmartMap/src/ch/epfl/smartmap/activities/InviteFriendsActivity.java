package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.gui.FriendPickerListAdapter;

/**
 * This activity lets the user invite friends to an event. Launched from
 * {@link ch.epfl.smartmap.activities.EventInformationActivity}
 * 
 * @author SpicyCH
 */
public class InviteFriendsActivity extends ListActivity {

    private final String TAG = InviteFriendsActivity.class.getSimpleName();

    private FriendPickerListAdapter mAdapter;
    private List<Boolean> mSelectedPositions;
    private List<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_invite_friends);

        DatabaseHelper.initialize(this.getApplicationContext());
        SettingsManager.initialize(this.getApplicationContext());

        // Makes the logo clickable (clicking it returns to previous activity)
        this.getActionBar().setHomeButtonEnabled(true);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

        this.setAdapter();

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (!mSelectedPositions.get(position)) {
            Log.d(TAG, "Friend at position " + position + " set to true");
            mSelectedPositions.set(position, true);
            v.setBackgroundColor(Color.parseColor("#dcdcdc"));
        } else {
            Log.d(TAG, "Friend at position " + position + " set to false");
            mSelectedPositions.set(position, false);
            v.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.invite_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.setResult(RESULT_CANCELED);
                this.finish();
                break;
            case R.id.invite_friend_send_button:

                this.inviteFriends();

                // Return to caller
                this.setResult(RESULT_OK);
                this.finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Invites the selected friends. Displays a toast describing if the invitations were sent or not.
     * 
     * @author SpicyCH
     */
    private void inviteFriends() {

        // Get selected positions
        List<Integer> posSelected = new ArrayList<Integer>();

        int pos = 0;
        for (Boolean b : mSelectedPositions) {
            if (b) {
                posSelected.add(pos);
            }
            pos++;
        }

        // Get corresponding friend ids
        List<Long> friendsIds = new ArrayList<Long>();
        for (Integer i : posSelected) {
            friendsIds.add(mUserList.get(i).getId());
        }

        Log.d(TAG, "Friends ids to invite: " + friendsIds);

        if (friendsIds.size() > 0) {
            // Invite friends if at least one selected
            Toast.makeText(this, this.getString(R.string.invite_friends_success), Toast.LENGTH_LONG).show();
            // TODO invite friends
            // TODO send invites via InvitationManager
        } else {
            Toast.makeText(this, this.getString(R.string.invite_friends_no_items_selected), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * @author SpicyCH
     */
    private void setAdapter() {
        mUserList = Cache.getInstance().getAllFriends();

        // Create a new list of booleans with the size of the user list and and default value false
        mSelectedPositions = new ArrayList<Boolean>();
        for (@SuppressWarnings("unused")
        User u : mUserList) {
            mSelectedPositions.add(false);
        }

        // Create custom Adapter and pass it to the Activity
        mAdapter = new FriendPickerListAdapter(this, mUserList);
        this.setListAdapter(mAdapter);
    }
}