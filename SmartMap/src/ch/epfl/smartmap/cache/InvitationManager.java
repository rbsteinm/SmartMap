package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.util.LongSparseArray;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.InvitationListener;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.NotificationBag;

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

		for (long userId : notifBag.getInvitingUsers()) {
			mInvitingUserIds.add(userId);
		}

		// Notify listeners
		for (InvitationListener l : mListeners) {
			l.onPendingFriendListUpdate();
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
		// TODO STATIC?
		// Cache.getFriendById(id);

		for (InvitationListener listener : mListeners) {
			listener.onPendingFriendListUpdate();
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
		// TODO STATIC?
		// Cache.getPublicEventById(id);
		for (InvitationListener listener : mListeners) {
			listener.onPendingEventListUpdate();
		}
	}

	/**
	 * @return
	 */
	public Set<Long> getPendingFriends() {
		Set<Long> pendingFriends = new HashSet<Long>();
		for (Long id : mPendingFriendIds) {
			User friend = mFriendInstances.get(id);
			if (friend != null) {
				pendingFriends.add(friend.getId());
			}
		}
		return pendingFriends;
	}

	/**
	 * @return
	 */
	public Set<Long> getPendingEvents() {
		Set<Long> pendingEvent = new HashSet<Long>();
		for (Long id : mPendingEventIds) {
			Event event = mPublicEventInstances.get(id);
			if (event != null) {
				pendingEvent.add(event.getId());
			}
		}
		return pendingEvent;
	}

	/**
	 * @return
	 */
	public Set<Long> getInvitedUsers() {
		return mInvitingUserIds;
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean acceptFriend(long id) {
		return false;

	}

	/**
	 * @return
	 */
	public int getNumberOfUnreadNotifs() {
		return 0;
	}
}
