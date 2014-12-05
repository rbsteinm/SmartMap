package ch.epfl.smartmap.activities;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.FriendInvitation;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.Notifications;
import ch.epfl.smartmap.gui.InvitationListItemAdapter;

/**
 * This activity displays the notifications received
 * 
 * @author agpmilli
 */
public class InvitationPanelActivity extends ListActivity {

    @SuppressWarnings("unused")
    private static final String TAG = InvitationPanelActivity.class.getSimpleName();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_notifications);
        Notifications.cancelNotification(this);

        mContext = this.getBaseContext();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent invitationIntent = ((FriendInvitation) l.getItemAtPosition(position)).getIntent();
        InvitationPanelActivity.this.startActivity(invitationIntent);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InvitationPanelActivity.this.setListAdapter(new InvitationListItemAdapter(mContext, ServiceContainer
            .getDatabase().getFriendInvitations()));

        List<FriendInvitation> unreadInvitations =
            ServiceContainer.getDatabase().getFriendInvitationsByStatus(Invitation.UNREAD);
        for (int i = 0; i < unreadInvitations.size(); i++) {
            unreadInvitations.get(i).setStatus(Invitation.READ);
            ServiceContainer.getDatabase().updateFriendInvitation(unreadInvitations.get(i));
        }

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
}