package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface CacheListener {

    void onAllLocationsUpdate();

    void onNewFriend(long id);

    void onNewNotification(long id);

}
