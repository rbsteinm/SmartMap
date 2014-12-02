package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.util.LongSparseArray;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.InvitationListener;
import ch.epfl.smartmap.search.CachedOnlineSearchEngine;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.NotificationBag;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author agpmilli
 */
public final class InvitationManager {

	private List<Invitation> history;

	private final static InvitationManager ONE_INSTANCE = new InvitationManager();

	// Other members of the data hierarchy
	private final DatabaseHelper mDatabaseHelper;
	private final NetworkSmartMapClient mNetworkClient;

	// List containing ids of all Friends
	private final Set<Long> mPendingEventIds;
	private final Set<Long> mPendingFriendIds;
	private final Set<Long> mInvitingUserIds;

	// SparseArrays containing instances
	private final LongSparseArray<Event> mPublicEventInstances;
	private final LongSparseArray<User> mFriendInstances;

	// Listeners
	private final List<InvitationListener> mListeners;

	private InvitationManager() {
		// Init data hierarchy
		mDatabaseHelper = DatabaseHelper.getInstance();
		mNetworkClient = NetworkSmartMapClient.getInstance();

		// Init sets
		mPendingEventIds = new HashSet<Long>();
		mPendingFriendIds = new HashSet<Long>();
		mInvitingUserIds = new HashSet<Long>();

		// Init sparseArray
		mPublicEventInstances = new LongSparseArray<Event>();
		mFriendInstances = new LongSparseArray<User>();

		mListeners = new LinkedList<InvitationListener>();
	}

	/**
	 * @param notifBag
	 */
	public void update(NotificationBag notifBag) {
		notifBag.getInvitingUsers();
		notifBag.getNewFriends();
		notifBag.getRemovedFriendsIds();

		// Delete previous lists
		mPendingFriendIds.clear();
		mPendingEventIds.clear();
		mInvitingUserIds.clear();

		for (long userId : notifBag.getInvitingUsers()) {
			mInvitingUserIds.add(userId);
		}

		// findFriendsByIds and findStrangersByIds already puts them in the cache
		CachedOnlineSearchEngine.getInstance().findFriendsByIds(notifBag.getNewFriends());
		CachedOnlineSearchEngine.getInstance().findStrangersByIds(notifBag.getNewFriends());

		// Notify listeners
		for (InvitationListener l : mListeners) {
			l.onInvitationListUpdate();
		}
	}

	/**
	 * Add a pending Friend, and fill the cache with its informations.
	 * 
	 * @param id
	 *            the id of user
	 */
	public void addPendingFriend(long id) {
		mPendingFriendIds.add(id);
		CachedOnlineSearchEngine.getInstance().findStrangerById(id);

		for (InvitationListener listener : mListeners) {
			listener.onInvitationListUpdate();
		}
	}

	/**
	 * Add a pending Event, and fill the cache with its information
	 * 
	 * @param id
	 *            the id of event
	 */
	public void addPendingEvent(long id) {
		mPendingEventIds.add(id);
		CachedOnlineSearchEngine.getInstance().findPublicEventById(id);

		for (InvitationListener listener : mListeners) {
			listener.onInvitationListUpdate();
		}
	}

	/**
	 * @return the id of pending friends
	 */
	public Set<User> getPendingFriends() {
		return Cache.getInstance().getStrangers(mPendingFriendIds);
	}

	/**
	 * @return the id of pending events
	 */
	public Set<Event> getPendingEvents() {
		return Cache.getInstance().getPublicEvents(mPendingEventIds);
	}

	/**
	 * @return the id of invited users
	 */
	public Set<User> getInvitedUsers() {
		return Cache.getInstance().getStrangers(mInvitingUserIds);
	}

	/**
	 * Return true if the invitation has been accepted (has to be called in an AsyncTask<>)
	 * 
	 * @param id
	 *            the id of user
	 * @return
	 *         true whether it has been accepted and false in the other case
	 */
	public boolean acceptFriend(final long id) {
		ImmutableUser result;
		try {
			result = NetworkSmartMapClient.getInstance().acceptInvitation(id);
			if (result != null) {
				Cache.getInstance().putFriend(result);
			} else {
				return false;
			}
			return true;
		} catch (SmartMapClientException e) {
			return false;
		}
	}

	/**
	 * @return number of unread invitations
	 */
	public int getNumberOfUnreadInvitations() {
		return 0;
	}
}
