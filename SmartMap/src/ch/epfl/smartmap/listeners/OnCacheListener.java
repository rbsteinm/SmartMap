package ch.epfl.smartmap.listeners;

/**
 * Basic implementation of {@code CacheListener} that does nothing on any notify call. Use it as a superclass
 * to avoid needing to implement all methods if not needed.
 * 
 * @author jfperren
 */
public class OnCacheListener implements CacheListener {

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.listeners.CacheListener#onEventListUpdate()
     */
    @Override
    public void onEventListUpdate() {
        // Nothing
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.listeners.CacheListener#onFilterListUpdate()
     */
    @Override
    public void onFilterListUpdate() {
        // Nothing
    }

    @Override
    public void onInvitationListUpdate() {
        // Nothing
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.listeners.CacheListener#onFriendListUpdate()
     */
    @Override
    public void onUserListUpdate() {
        // Nothing
    }
}
