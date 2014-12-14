package ch.epfl.smartmap.background;

import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import ch.epfl.smartmap.util.Utils;

/**
 * @author jfperren
 * @author ritterni
 */
public class OwnPositionService extends Service {

    private static final String TAG = OwnPositionService.class.getSimpleName();

    private LocationManager mLocManager;
    // Distance between position updates on network
    private static final float MIN_NETWORK_DISTANCE = 0;
    // Distance between position updates on GPS
    private static final float MIN_GPS_DISTANCE = 50;
    // Time between updates (milliseconds)
    private static final int GPS_UPDATE_TIME = 5 * 60 * 1000;
    private static final int DEFAULT_NETWORK_TIME = 10000;
    private int mUpdateTime = DEFAULT_NETWORK_TIME;
    // Time to wait before restarting the service
    private static final int RESTART_DELAY = 2000;
    // Accuracy (in meters) of the latest location update
    private static final float INITIAL_ACCURACY = 1000;
    private float mCurrentAccuracy = INITIAL_ACCURACY;
    // Delay before trying to reconnect
    private static final int RECONNECT_DELAY = 10 * 1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Ugly workaround because of KitKat stopping services when app gets closed
    // (Android issue #63618)
    @Override
    public void onCreate() {
        super.onCreate();
        mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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

        mUpdateTime = ServiceContainer.getSettingsManager().getRefreshFrequency();

        new StartUp().execute();

        return START_STICKY;
    }

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
     * A location listener
     * 
     * @author ritterni
     */
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(final Location newLocation) {
            // check if new location is accurate enough
            if ((ServiceContainer.getSettingsManager().getLocation().distanceTo(newLocation) >= newLocation
                .getAccuracy()) || (newLocation.getAccuracy() <= mCurrentAccuracy)) {
                mCurrentAccuracy = newLocation.getAccuracy();
                // Give new location to SettingsManager
                ServiceContainer.getSettingsManager().setLocation(newLocation);
            }
            // Sends new Position to server
            if (!ServiceContainer.getSettingsManager().isOffline()) {
                ServiceContainer.getSettingsManager().setLastSeen(new GregorianCalendar().getTimeInMillis());
                ServiceContainer.getSettingsManager().setLocationName(
                    Utils.getCityFromLocation(ServiceContainer.getSettingsManager().getLocation()));
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    public Void doInBackground(Void... params) {
                        try {
                            ServiceContainer.getNetworkClient().updatePos(
                                ServiceContainer.getSettingsManager().getLocation());
                            Log.d(TAG, "Location Update");
                        } catch (SmartMapClientException e) {
                            Log.e(TAG, "Error in LocationListener : " + e);
                        }
                        return null;
                    }
                }.execute();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.w(TAG, provider + " was disabled.");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, provider + " was enabled.");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, provider + " status: " + status);
        }
    }

    /**
     * Performs tasks that are needed when the service starts
     * 
     * @author ritterni
     */
    private class StartUp extends AsyncTask<Void, Void, Boolean> {
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

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Creates a Criteria, used to chose LocationManager settings
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                mLocManager.requestSingleUpdate(criteria, new MyLocationListener(), null);
                // Try to run LocationManager with Network Provider
                if (mLocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mUpdateTime,
                        MIN_NETWORK_DISTANCE, new MyLocationListener());
                }

                // And try to run LocationManager with GPS Provider
                if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME,
                        MIN_GPS_DISTANCE, new MyLocationListener());
                }
            } else {
                // Retry connection
                try {
                    Thread.sleep(RECONNECT_DELAY);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread interrupted: " + e);
                }
                new StartUp().execute();
            }
        }
    }
}
