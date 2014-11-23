package ch.epfl.smartmap.background;

import java.io.IOException;
import java.util.ArrayList;
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
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * A background service that updates friends' position periodically
 * 
 * @author ritterni
 */
public class UpdateService extends Service {
    /**
     * AsyncTask to send the user's own position to the server
     * 
     * @author ritterni
     */
    private class AsyncFriendsInit extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            mHelper.initializeAllFriends();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mReady = true;
        }
    }

    /**
     * AsyncTask to get friends' positions
     * 
     * @author ritterni
     */
    private class AsyncFriendsPos extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... args0) {
            int rows = 0;
            rows = mHelper.refreshFriendsPos();
            return rows;
        }

        @Override
        protected void onPostExecute(Integer result) {
            mFriendsPosIntent.putExtra(UPDATED_ROWS, result);
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
                mClient.authServer(mManager.getUserName(), mManager.getFacebookID(), mManager.getToken());
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't log in!");
            }
            return null;
        }
    }

    /**
     * AsyncTask to send the user's own position to the server
     * 
     * @author ritterni
     */
    private class AsyncOwnPos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                mClient.updatePos(mManager.getLocation());
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Position update failed!");
            }
            return null;
        }
    }

    /**
     * AsyncTask to check if a friend request was received
     * 
     * @author ritterni
     */
    private class AsyncReplyCheck extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... arg0) {
            List<User> list = new ArrayList<User>();
            try {
                // Second list, the list of accepted invitations
                list = mClient.getInvitations().get(1);
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't retrieve replies!");
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<User> result) {
            if (!result.isEmpty()) {
                for (User user : result) {
                    mHelper.addUser(user);
                    mHelper.deletePendingFriend(user.getID());
                    UpdateService.this.showAcceptedNotif(user);
                }
            }
        }
    }

    /**
     * AsyncTask to check if a friend request was received
     * 
     * @author ritterni
     */
    private class AsyncRequestCheck extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... arg0) {
            List<User> list = new ArrayList<User>();
            try {
                // First list, the list of received invitations
                list = mClient.getInvitations().get(0);
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't retrieve invites!");
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<User> result) {
            if (!result.isEmpty()) {
                for (User user : result) {
                    // TWEAK ! MUST BE REPLACED BY PROPER STORAGE IN THE
                    // DATABASE !
                    if (mHelper.addInvitation(user) > 0) {
                        UpdateService.this.showFriendNotif(user);
                    }
                }
            }
        }
    }

    /**
     * A location listener
     * 
     * @author ritterni
     */
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            mManager.setLocation(locFromGps);
            if (mOwnPosEnabled) {
                new AsyncOwnPos().execute();
            }
            // Sets the location name
            try {
                List<Address> addresses =
                    mGeocoder.getFromLocation(locFromGps.getLatitude(), locFromGps.getLongitude(), 1);

                String locName = SettingsManager.DEFAULT_LOC_NAME;
                if (!addresses.isEmpty()) {
                    locName = addresses.get(0).getLocality();
                }
                mManager.setLocationName(locName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            mOwnPosEnabled = false;
        }

        @Override
        public void onProviderEnabled(String provider) {
            mOwnPosEnabled = true;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // stop sending position if provider isn't available
            if ((status == LocationProvider.OUT_OF_SERVICE)
                || (status == LocationProvider.TEMPORARILY_UNAVAILABLE)) {
                mOwnPosEnabled = false;
            } else if (status == LocationProvider.AVAILABLE) {
                mOwnPosEnabled = true;
            }
        }
    }

    public static final String BROADCAST_POS = "ch.epfl.smartmap.background.broadcastPos";
    public static final String UPDATED_ROWS = "UpdatedRows";
    private static final int HANDLER_DELAY = 1000;
    private static final int POS_UPDATE_DELAY = 10000;
    private static final int INVITE_UPDATE_DELAY = 30000;
    private static final float MIN_DISTANCE = 5; // minimum distance to update
                                                 // position
    private static final int RESTART_DELAY = 2000;
    private final Handler mHandler = new Handler();
    private Intent mFriendsPosIntent;
    private LocationManager mLocManager;

    private boolean mFriendsPosEnabled = true;
    private boolean mOwnPosEnabled = true;

    private boolean mReady = false;

    private DatabaseHelper mHelper;
    private SettingsManager mManager;
    private Geocoder mGeocoder;

    private final NetworkSmartMapClient mClient = NetworkSmartMapClient.getInstance();

    private final Runnable friendsPosUpdate = new Runnable() {
        @Override
        public void run() {
            if (mFriendsPosEnabled) {
                if (mReady) {
                    new AsyncFriendsPos().execute();
                    UpdateService.this.sendBroadcast(mFriendsPosIntent);
                }
                mHandler.postDelayed(this, POS_UPDATE_DELAY);
            }
        }
    };

    private final Runnable showFriendNotif = new Runnable() {
        @Override
        public void run() {
            new AsyncRequestCheck().execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    };

    private final Runnable getReplies = new Runnable() {
        @Override
        public void run() {
            new AsyncReplyCheck().execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = SettingsManager.initialize(this.getApplicationContext());
        mHelper = DatabaseHelper.initialize(this.getApplicationContext());
        mGeocoder = new Geocoder(this.getBaseContext(), Locale.US);
        mFriendsPosIntent = new Intent(BROADCAST_POS);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocManager.requestLocationUpdates(mLocManager.getBestProvider(criteria, true), POS_UPDATE_DELAY,
            MIN_DISTANCE, new MyLocationListener());
        new AsyncFriendsInit().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.removeCallbacks(friendsPosUpdate);
        mHandler.postDelayed(friendsPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(showFriendNotif);
        mHandler.postDelayed(showFriendNotif, HANDLER_DELAY);
        mHandler.removeCallbacks(getReplies);
        mHandler.postDelayed(getReplies, HANDLER_DELAY);
        new AsyncLogin().execute();
        Log.d("UpdateService", "Service started");

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
        mFriendsPosEnabled = isEnabled;
        if (isEnabled) {
            mHandler.postDelayed(friendsPosUpdate, HANDLER_DELAY);
        }
    }

    private void showAcceptedNotif(User user) {
        Notifications.acceptedFriendNotification(this, user);
    }

    private void showFriendNotif(User user) {
        Notifications.newFriendNotification(this, user);
    }
}