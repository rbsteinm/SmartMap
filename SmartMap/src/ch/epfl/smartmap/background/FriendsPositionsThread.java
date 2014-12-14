package ch.epfl.smartmap.background;

import java.util.HashSet;

import android.util.Log;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * A thread that periodically retrieves friends' locations
 * 
 * @author jfperren
 */
public class FriendsPositionsThread extends Thread {

    private static final String TAG = FriendsPositionsThread.class.getSimpleName();
    private boolean mEnabled = true;

    /**
     * Disables position updates
     */
    public void disable() {
        mEnabled = false;
    }

    /**
     * Enables position updates (they are enabled by default)
     */
    public void enable() {
        mEnabled = true;
    }

    @Override
    public void run() {
        while (true) {
            if ((!ServiceContainer.getSettingsManager().isOffline()) && mEnabled) {
                try {
                    Log.d(TAG, "Update Friends Positions");
                    ServiceContainer.getCache().putUsers(
                        new HashSet<UserContainer>(ServiceContainer.getNetworkClient().listFriendsPos()));
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
