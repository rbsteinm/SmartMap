package ch.epfl.smartmap.background;

import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author jfperren
 */
public class NearEventsThread extends Thread {

    private static final String TAG = NearEventsThread.class.getSimpleName();
    private final static int REFRESH_DELAY = 20000;

    @Override
    public void run() {
        while (true) {
            Location pos = ServiceContainer.getSettingsManager().getLocation();
            try {
                ServiceContainer.getNetworkClient().getPublicEvents(pos.getLongitude(), pos.getLatitude(),
                    ServiceContainer.getSettingsManager().getNearEventsMaxDistance());
                Log.d(TAG, "Fetch Near Events");
            } catch (SmartMapClientException e) {
                Log.e(TAG, "Couldn't retrieve public events: " + e);
            }
            try {
                sleep(REFRESH_DELAY);
            } catch (InterruptedException e) {
                Log.e(TAG, "Can't sleep");
            }
        }
    }
}
