package ch.epfl.smartmap.background;

import android.util.Log;

/**
 * Periodically updates the database from the cache
 * 
 * @author jfperren
 */
public class UpdateDatabaseThread extends Thread {

    private static final String TAG = UpdateDatabaseThread.class.getSimpleName();
    // Update is done every 5 minutes
    private static final int UPDATE_PERIOD = 5 * 60 * 1000;

    @Override
    public void run() {
        while (true) {
            try {
                sleep(UPDATE_PERIOD);
            } catch (InterruptedException e) {
                Log.e(TAG, "Can't sleep : " + e);
            }

            Log.d(TAG, "Update Database");
            ServiceContainer.getDatabase().updateFromCache();
        }
    }
}
