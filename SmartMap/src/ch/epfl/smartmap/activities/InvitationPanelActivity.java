package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.Notifications;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.GenericInvitation;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.gui.InvitationListItemAdapter;
import ch.epfl.smartmap.listeners.OnCacheListener;

/**
 * This activity displays the notifications received
 * 
 * @author agpmilli
 */
public class InvitationPanelActivity extends ListActivity {

    private List<Invitation> mInvitationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_notifications);

        ServiceContainer.initSmartMapServices(this);

        Notifications.cancelNotification(this);

        mInvitationList = new ArrayList<Invitation>(ServiceContainer.getCache().getAllInvitations());

        this.setListAdapter(new InvitationListItemAdapter(InvitationPanelActivity.this, mInvitationList));

        // Initialize the listener
        ServiceContainer.getCache().addOnCacheListener(new OnCacheListener() {
            @Override
            public void onInvitationListUpdate() {
                InvitationPanelActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InvitationPanelActivity.this.setListAdapter(new InvitationListItemAdapter(
                            InvitationPanelActivity.this, mInvitationList));
                    }
                });
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Intent invitationIntent = ((GenericInvitation) l.getItemAtPosition(position)).getIntent();
        if (invitationIntent != null) {
            this.startActivity(invitationIntent);
            this.finish();
        }

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setListAdapter(new InvitationListItemAdapter(this, new ArrayList<Invitation>(ServiceContainer.getCache()
            .getAllInvitations())));

        ServiceContainer.getCache().readAllInvitations();
    }
}