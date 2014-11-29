package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface CacheListener {

    void onNewFriend(long id);

    void onNewNotification(long id);
}
