package ch.epfl.smartmap.background;

import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.servercom.InvitationBag;
import ch.epfl.smartmap.servercom.NetworkFriendInvitationBag;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * A background service that fetches invitations periodically
 * 
 * @author ritterni
 */
public class InvitationsService extends Service {

    private static final String TAG = InvitationsService.class.getSimpleName();

    // Time between each invitation fetch
    private static final int INVITE_UPDATE_DELAY = 10000;
    // Time before restarting
    private static final int RESTART_DELAY = 2000;
    // Handler for Runnables
    private final Handler mHandler = new Handler();

    private final Runnable getInvitations = new Runnable() {
        @Override
        public void run() {
            new AsyncInvitations().execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    };

    /**
     * Makes notifications when there is no cache (the app is in background)
     * (unused in current build, the cache always exists)
     * 
     * @param userInvitBag
     *            {@code InvitationBag} for friend invitations
     * @param eventInvitBag
     *            {@code InvitationBag} for event invitations
     */
    private void backgroundNotifications(InvitationBag userInvitBag, InvitationBag eventInvitBag) {
        Set<InvitationContainer> friendInvitations = userInvitBag.getInvitations();
        Set<InvitationContainer> eventInvitations = eventInvitBag.getInvitations();
        Set<Long> removedFriends = ((NetworkFriendInvitationBag) userInvitBag).getRemovedFriendsIds();
        for (InvitationContainer invite : friendInvitations) {
            ServiceContainer.getDatabase().addInvitation(invite);
            if (invite.getType() == Invitation.ACCEPTED_FRIEND_INVITATION) {
                ServiceContainer.getDatabase().addUser(invite.getUserInfos());
            }
            Notifications.createNotification(Invitation.createFromContainer(invite), this);
        }
        for (InvitationContainer invite : eventInvitations) {
            ServiceContainer.getDatabase().addInvitation(invite);
            Notifications.createNotification(Invitation.createFromContainer(invite), this);
        }
        for (long id : removedFriends) {
            // TODO
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Recreating services if they are not set
        if (ServiceContainer.getSettingsManager() == null) {
            ServiceContainer.setSettingsManager(new SettingsManager(this.getApplicationContext()));
        }
        if (ServiceContainer.getNetworkClient() == null) {
            ServiceContainer.setNetworkClient(new NetworkSmartMapClient());
        }
        if (ServiceContainer.getDatabase() == null) {
            ServiceContainer.setDatabaseHelper(new DatabaseHelper(this.getApplicationContext()));
        }
        if (ServiceContainer.getCache() == null) {
            ServiceContainer.setCache(new Cache());
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... arg0) {
                try {
                    // Authenticate in order to communicate with NetworkClient
                    ServiceContainer.getNetworkClient().authServer(
                        ServiceContainer.getSettingsManager().getUserName(),
                        ServiceContainer.getSettingsManager().getFacebookID(),
                        ServiceContainer.getSettingsManager().getToken());
                    return true;
                } catch (SmartMapClientException e) {
                    Log.e(TAG, "Couldn't log in: " + e);
                    return false;
                }
            }
        }.execute();

        mHandler.removeCallbacks(getInvitations);
        mHandler.post(getInvitations);
        Log.d(TAG, "Service started");

        return START_STICKY;
    }

    // Ugly workaround because of KitKat stopping services when app gets closed
    // (Android issue #63618)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(this.getApplicationContext(), this.getClass());
        restartService.setPackage(this.getPackageName());
        PendingIntent restartServicePending =
            PendingIntent.getService(this.getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService =
            (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + RESTART_DELAY,
            restartServicePending);
    }

    /**
     * Retrieves invitations
     * 
     * @author ritterni
     */
    private class AsyncInvitations extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                NetworkFriendInvitationBag userInvitBag =
                    (NetworkFriendInvitationBag) ServiceContainer.getNetworkClient().getFriendInvitations();
                InvitationBag eventInvitBag = ServiceContainer.getNetworkClient().getEventInvitations();

                // Acknowledge removed friends
                for (Long id : userInvitBag.getRemovedFriendsIds()) {
                    ServiceContainer.getNetworkClient().ackRemovedFriend(id);
                }

                if (ServiceContainer.getCache() != null) {
                    // Get friends invitations
                    Log.d(TAG, "Friend invitations");
                    if (!userInvitBag.getInvitations().isEmpty()) {
                        ServiceContainer.getCache().putInvitations(userInvitBag.getInvitations());
                    }
                    // Get event invitations

                    Log.d(TAG, "Event invitations");
                    if (!eventInvitBag.getInvitations().isEmpty()) {
                        ServiceContainer.getCache().putInvitations(eventInvitBag.getInvitations());
                    }
                    Log.d(TAG, "Successfully fetched invitations / users : " + userInvitBag.getInvitations()
                        + " / events : " + eventInvitBag.getInvitations());
                } else {
                    InvitationsService.this.backgroundNotifications(userInvitBag, eventInvitBag);
                }
            } catch (SmartMapClientException e) {
                Log.e(TAG, "Couldn't retrieve invitations due to a server error: " + e);
            }
            return null;
        }
    }
}