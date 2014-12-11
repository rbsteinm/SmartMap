package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface CacheListener {

    void onEventListUpdate();

    void onFilterListUpdate();

    void onInvitationListUpdate();

    void onUserListUpdate();
}
