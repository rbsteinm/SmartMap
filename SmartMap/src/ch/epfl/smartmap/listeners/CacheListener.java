package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface CacheListener {

    void onFriendListUpdate();

    void onNearEventListUpdate();

    void onPendingFriendListUpdate();

    void onVisibleMarkableListUpdate();

}
