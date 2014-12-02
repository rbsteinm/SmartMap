package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface InvitationListener {

	void onInvitingUserListUpdate();

	void onPendingEventListUpdate();

	void onPendingFriendListUpdate();

}
