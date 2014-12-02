package ch.epfl.smartmap.cache;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.NotificationBag;

/**
 * @author agpmilli
 */
public final class InvitationManager {

	private List<Invitation> history;

	// Listeners
	private final List<CacheListener> mListeners;

	private final static InvitationManager ONE_INSTANCE = new InvitationManager();

	// Other members of the data hierarchy
	private final DatabaseHelper mDatabaseHelper;
	private final NetworkSmartMapClient mNetworkClient;

	// List containing ids of all Friends
	private final Set<Long> mPendingEventIds;
	private final Set<Long> mPendingFriendIds;
	private final Set<Long> mInvitingUserIds;

	private InvitationManager() {
		// Init data hierarchy
		mDatabaseHelper = DatabaseHelper.getInstance();
		mNetworkClient = NetworkSmartMapClient.getInstance();

		// Init sets
		mPendingEventIds = new HashSet<Long>();
		mPendingFriendIds = new HashSet<Long>();
		mInvitingUserIds = new HashSet<Long>();

		mListeners = new LinkedList<CacheListener>();
	}

	public void update(NotificationBag notifBag) {

	}

	public Set<Long> getPendingFriends() {
		return null;

	}

	public Set<Long> getPendingEvents() {
		return null;
	}

	public Set<Long> getInvitedUsers() {
		return mInvitingUserIds;
	}

	public boolean acceptFriend(long id) {
		return false;

	}

	public int getNumberOfUnreadNotifs() {

		return 0;

	}
}
