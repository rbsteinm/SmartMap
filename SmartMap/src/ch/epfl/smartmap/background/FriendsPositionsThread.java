package ch.epfl.smartmap.background;

import java.util.HashSet;

import android.util.Log;
import ch.epfl.smartmap.cache.ImmutableUser;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author jfperren
 */
public class FriendsPositionsThread extends Thread {

    private static final String TAG = FriendsPositionsThread.class.getSimpleName();

    @Override
    public void run() {
        while (true) {
            if (!ServiceContainer.getSettingsManager().isOffline()) {
                try {
                    Log.d(TAG, "Update Friends Positions");
                    ServiceContainer.getCache().updateFriends(
                        new HashSet<ImmutableUser>(ServiceContainer.getNetworkClient().listFriendsPos()));
                } catch (SmartMapClientException e) {
                    Log.e(InvitationsService.class.getSimpleName(), "Network error: " + e);
                }
            }
            try {
                sleep(ServiceContainer.getSettingsManager().getRefreshFrequency());
            } catch (InterruptedException e) {
                Log.e(TAG, "Can't sleep: " + e);
            }
        }
    }
}
