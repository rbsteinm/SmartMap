package ch.epfl.smartmap.background;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import ch.epfl.smartmap.cache.ImmutableUser;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.servercom.NotificationBag;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * A background service that updates friends' position periodically
 * 
 * @author ritterni
 */
public class UpdateService extends Service {

    private static final String TAG = "UPDATE_SERVICE";

    private static final int HANDLER_DELAY = 1000;
    private static final int GPS_UPDATE_DELAY = 5 * 60 * 1000;
    private static final int INVITE_UPDATE_DELAY = 30000;

    private static final float MIN_DISTANCE = 0; // minimum distance to update
                                                 // position
    private static final float MIN_GPS_DISTANCE = 50; // minimum distance before
                                                      // gps updates are
                                                      // requested

    private static final int RESTART_DELAY = 2000;
    public static final int IMAGE_QUALITY = 100;

    private final Handler mHandler = new Handler();
    private LocationManager mLocManager;
    private Geocoder mGeocoder;
    private final SmartMapClient mClient = ServiceContainer.getNetworkClient();
    private float mCurrentAccuracy = 0;

    // Settings
    private int mPosUpdateDelay;
    private final Runnable friendsPosUpdate = new Runnable() {
        @Override
        public void run() {
            if (!ServiceContainer.getSettingsManager().isOffline()) {
                AsyncTask<Void, Void, Void> asyncFriendsPos = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... args0) {
                        try {
                            ServiceContainer.getCache().updateFriends(
                                new HashSet<ImmutableUser>(ServiceContainer.getNetworkClient()
                                    .listFriendsPos()));
                        } catch (SmartMapClientException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                asyncFriendsPos.execute();
            }
            mHandler.postDelayed(this, ServiceContainer.getSettingsManager().getRefreshFrequency());
        }
    };

    private final Runnable ownPosUpdate = new Runnable() {
        @Override
        public void run() {
            if (!ServiceContainer.getSettingsManager().isOffline()) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            ServiceContainer.getNetworkClient().updatePos(
                                ServiceContainer.getSettingsManager().getLocation());
                        } catch (SmartMapClientException e) {
                            Log.e("UpdateService", "Position update failed:" + e);
                        }
                        return null;
                    }
                }.execute();
            }
            mHandler.postDelayed(this, ServiceContainer.getSettingsManager().getRefreshFrequency());
        }
    };

    private final Runnable nearEventsUpdate = new Runnable() {
        @Override
        public void run() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                public Void doInBackground(Void... params) {
                    Location pos = ServiceContainer.getSettingsManager().getLocation();
                    try {
                        ServiceContainer.getNetworkClient().getPublicEvents(pos.getLongitude(),
                            pos.getLatitude(), 100);
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Couldn't retrieve public events: " + e);
                    }
                    return null;
                }
            }.execute();
            mHandler.postDelayed(this, 20000);
        }
    };

    private final Runnable updateDatabase = new Runnable() {
        @Override
        public void run() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                public Void doInBackground(Void... params) {
                    DatabaseHelper.getInstance().updateFromCache();
                    return null;
                }
            }.execute();
            mHandler.postDelayed(this, 60000);
        }
    };

    private final Runnable getInvitations = new Runnable() {
        @Override
        public void run() {
            UpdateService.this.loadSettings();
            AsyncTask<Void, Void, NotificationBag> asyncInvitations =
                new AsyncTask<Void, Void, NotificationBag>() {
                    @Override
                    protected NotificationBag doInBackground(Void... arg0) {
                        NotificationBag nb = null;
                        try {
                            nb = mClient.getInvitations();
                            ServiceContainer.getCache().updateFriendInvitations(nb,
                                UpdateService.this.getApplicationContext());
                        } catch (SmartMapClientException e) {
                            Log.e("UpdateService", "Couldn't retrieve invitations due to a server error: "
                                + e);
                        }
                        return nb;
                    }
                };
            asyncInvitations.execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    };

    private final Runnable getEventInvitations = new Runnable() {
        @Override
        public void run() {
            new AsyncTask<Void, Void, List<Long>>() {
                @Override
                protected List<Long> doInBackground(Void... arg0) {
                    List<Long> invitations = null;
                    try {
                        invitations = mClient.getEventInvitations();
                        ServiceContainer.getCache().updateEventInvitations(invitations,
                            UpdateService.this.getApplicationContext());
                    } catch (SmartMapClientException e) {
                        Log.e("UpdateService", "Couldn't retrieve event invitations: " + e);
                    }
                    return invitations;
                }
            }.execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    };

    private void loadSettings() {
        mPosUpdateDelay = ServiceContainer.getSettingsManager().getRefreshFrequency();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Utils.sContext == null) {
            Utils.sContext = this;
        }
        mGeocoder = new Geocoder(this.getBaseContext(), Locale.US);
        mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.loadSettings();
        new AsyncLogin().execute();
        mHandler.removeCallbacks(friendsPosUpdate);
        mHandler.postDelayed(friendsPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(getInvitations);
        mHandler.postDelayed(getInvitations, HANDLER_DELAY);
        mHandler.removeCallbacks(nearEventsUpdate);
        mHandler.postDelayed(nearEventsUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(updateDatabase);
        mHandler.postDelayed(updateDatabase, HANDLER_DELAY);
        mHandler.removeCallbacks(ownPosUpdate);
        mHandler.postDelayed(ownPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(getEventInvitations);
        mHandler.postDelayed(getEventInvitations, HANDLER_DELAY);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        mLocManager.requestSingleUpdate(criteria, new MyLocationListener(), null);
        if (mLocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mPosUpdateDelay,
                MIN_DISTANCE, new MyLocationListener());
        }

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_DELAY,
                MIN_GPS_DISTANCE, new MyLocationListener());
        }
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
     * Enables/disables friends position updates
     * 
     * @param isEnabled
     *            True if updates should be enabled
     */
    public void setFriendsPosUpdateEnabled(boolean isEnabled) {
        if (isEnabled) {
            mHandler.postDelayed(friendsPosUpdate, HANDLER_DELAY);
        }
    }

    /**
     * AsyncTask to log in
     * 
     * @author ritterni
     */
    private class AsyncLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                mClient.authServer(ServiceContainer.getSettingsManager().getUserName(), ServiceContainer
                    .getSettingsManager().getFacebookID(), ServiceContainer.getSettingsManager().getToken());
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't log in: " + e);
            }
            return null;
        }
    }

    /**
     * A location listener
     * 
     * @author ritterni
     */
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location fix) {
            // do nothing if the new location is less accurate
            if ((ServiceContainer.getSettingsManager().getLocation().distanceTo(fix) >= fix.getAccuracy())
                || (fix.getAccuracy() <= mCurrentAccuracy)) {
                ServiceContainer.getSettingsManager().setLocation(fix);
                mCurrentAccuracy = fix.getAccuracy();

                // Sets the location name
                try {
                    List<Address> addresses =
                        mGeocoder.getFromLocation(fix.getLatitude(), fix.getLongitude(), 1);

                    String locName = SettingsManager.DEFAULT_LOC_NAME;
                    if (!addresses.isEmpty()) {
                        locName = addresses.get(0).getLocality();
                    }
                    ServiceContainer.getSettingsManager().setLocationName(locName);

                } catch (IOException e) {
                    Log.e("UpdateService", "Error in LocationListener: " + e);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.w(UpdateService.class.getSimpleName(), provider + " was disabled.");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(UpdateService.class.getSimpleName(), provider + " was enabled.");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(UpdateService.class.getSimpleName(), provider + " status: " + status);
        }
    }
}