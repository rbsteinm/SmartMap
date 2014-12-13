package ch.epfl.smartmap.background;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.location.Location;
import android.util.Log;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author jfperren
 */
public class NearEventsThread extends Thread {

    private static final String TAG = NearEventsThread.class.getSimpleName();
    private static final int REFRESH_DELAY = 20000;

    @Override
    public void run() {
        while (true) {
            try {
                sleep(REFRESH_DELAY);
            } catch (InterruptedException e) {
                Log.e(TAG, "Can't sleep: " + e);
            }

            Location pos = ServiceContainer.getSettingsManager().getLocation();
            try {
                List<Long> nearEventIds = ServiceContainer.getNetworkClient().getPublicEvents(pos.getLatitude(),
                        pos.getLongitude(), ServiceContainer.getSettingsManager().getNearEventsMaxDistance());

                Set<EventContainer> nearEvents = new HashSet<EventContainer>();
                for (long id : nearEventIds) {
                    nearEvents.add(ServiceContainer.getNetworkClient().getEventInfo(id));
                }
                ServiceContainer.getCache().putEvents(nearEvents);

                Log.d(TAG, "Fetch Near Events : " + nearEventIds + " radius "
                        + ServiceContainer.getSettingsManager().getNearEventsMaxDistance());

            } catch (SmartMapClientException e) {
                Log.e(TAG, "Couldn't retrieve public events: " + e);
            }
        }
    }
}
