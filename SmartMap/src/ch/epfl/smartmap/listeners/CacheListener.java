package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface CacheListener {

    void onFriendListUpdate();

    void onGoingEventListUpdate();

    void onMyEventListUpdate();

    void onNearEventListUpdate();

    void onPendingFriendListUpdate();

}
