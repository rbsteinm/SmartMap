package ch.epfl.smartmap.background;

import java.util.List;

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
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.NotificationBag;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * A background service that updates friends' position periodically
 * 
 * @author ritterni
 */
public class InvitationsService extends Service {

    private static final String TAG = InvitationsService.class.getSimpleName();

    // Time between each invitation fetch
    private static final int INVITE_UPDATE_DELAY = 5000;
    // Time before restarting
    private static final int RESTART_DELAY = 2000;
    // Handler for Runnables
    private final Handler mHandler = new Handler();

    private final Runnable getInvitations = new InvitationsRunnable();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Recreating services if they are not set
        Utils.sContext = this.getApplicationContext();
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

        Log.d(TAG, "StartCommand");
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... arg0) {
                try {
                    // Authentify in order to communicate with NetworkClient
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
     * Retrieves and handles invitations
     * 
     * @author ritterni
     */
    private class InvitationsRunnable implements Runnable {
        @Override
        public void run() {
            new AsyncTask<Void, Void, NotificationBag>() {
                @Override
                protected NotificationBag doInBackground(Void... arg0) {
                    NotificationBag nb = null;
                    try {
                        // Get friends invitations
                        nb = ServiceContainer.getNetworkClient().getInvitations();
                        ServiceContainer.getCache().updateFriendInvitations(nb,
                            InvitationsService.this.getApplicationContext());
                        // Get event invitations
                        List<Long> invitations = ServiceContainer.getNetworkClient().getEventInvitations();
                        ServiceContainer.getCache().updateEventInvitations(invitations);
                        Log.d(TAG, "Successfully fetched invitations");
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Couldn't retrieve invitations due to a server error: " + e);
                    }
                    return nb;
                }
            }.execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    }
}