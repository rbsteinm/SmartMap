package ch.epfl.smartmap.background;

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
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author jfperren
 */
public class OwnPositionService extends Service {

    private static final String TAG = OwnPositionService.class.getSimpleName();

    private LocationManager mLocManager;

    // minimum distance to update position
    private static final float MIN_NETWORK_DISTANCE = 0;
    // minimum distance before gps updates are requested
    private static final float MIN_GPS_DISTANCE = 50;
    // Time between position updates on GPS
    private int mGPSRefreshFrequency;
    // Time between position updates on Network
    private int mNetworkRefreshFrequency;

    // Time before restart
    private static final int RESTART_DELAY = 2000;

    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new StartUp().execute();

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
     * A location listener
     * 
     * @author ritterni
     */
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(final Location newLocation) {
            // Name of our location
            String locName = Utils.getCityFromLocation(newLocation);
            // Give new location to SettingsManager
            ServiceContainer.getSettingsManager().setLocationName(locName);
            // Sends new Position to server
            new AsyncTask<Void, Void, Void>() {
                @Override
                public Void doInBackground(Void... params) {
                    try {
                        ServiceContainer.getNetworkClient().updatePos(newLocation);
                        Log.d(TAG, "Location Update");
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Error in LocationListener : " + e);
                    }
                    return null;
                }
            }.execute();

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

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Creates a Criteria, used to chose LocationManager settings
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

                mLocManager.requestSingleUpdate(criteria, new MyLocationListener(), null);

                // Try to run LocationManager with Network Provider
                if (mLocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        mNetworkRefreshFrequency, MIN_NETWORK_DISTANCE, new MyLocationListener());
                }

                // And try to run LocationManager with GPS Provider
                if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mGPSRefreshFrequency,
                        MIN_GPS_DISTANCE, new MyLocationListener());
                }
            } else {
                // FIXME : Handle this case
                // Shouldn't the service always be authentified when launched ?
            }
        }
    }
}
