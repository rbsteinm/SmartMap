package ch.epfl.smartmap.listeners;

import ch.epfl.smartmap.cache.Displayable;

/**
 * @author jfperren
 */
public interface CacheListener {

    void onDisplayableUpdate(Displayable updated);

    void onFriendListUpdate();

    void onGoingEventListUpdate();

    void onMyEventListUpdate();

    void onNearEventListUpdate();

    void onPendingFriendListUpdate();

    void onVisibleLocalisableListUpdate();

}
