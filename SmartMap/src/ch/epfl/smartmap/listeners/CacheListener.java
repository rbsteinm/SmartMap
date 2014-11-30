package ch.epfl.smartmap.listeners;

import ch.epfl.smartmap.cache.Localisable;

/**
 * @author jfperren
 */
public interface CacheListener {

    void onFriendListUpdate();

    void onGoingEventListUpdate();

    void onLocalisableUpdate(Localisable l);

    void onNearEventListUpdate();

    void onPendingFriendListUpdate();

    void onVisibleMarkableListUpdate();

}
