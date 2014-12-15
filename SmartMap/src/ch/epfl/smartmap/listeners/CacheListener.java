package ch.epfl.smartmap.listeners;

/**
 * Interface that describes how a class that wants to listener to value update in the Cache should be doing
 * so.
 * 
 * @author jfperren
 */
public interface CacheListener {

    /**
     * Called when an {@code Event} is added, removed or updated in the {@code Cache}.
     */
    void onEventListUpdate();

    /**
     * Called when an {@code Filter} is added, removed or updated in the {@code Cache}.
     */
    void onFilterListUpdate();

    /**
     * Called when an {@code Invitation} is added, removed or updated in the {@code Cache}.
     */
    void onInvitationListUpdate();

    /**
     * Called when an {@code User} is added, removed or updated in the {@code Cache}.
     */
    void onUserListUpdate();
}
