package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.util.LongSparseArray;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * The Cache contains all instances of network objects that are used by the GUI. Therefore, every request to
 * find an
 * user or an event should go through it. It will automatically fill itself with the database on creation, and
 * then
 * updates the database as changes are made.
 * 
 * @author jfperren
 */
public class Cache {

	static final public String TAG = "Cache";

	// Unique instance
	private static final Cache ONE_INSTANCE = new Cache();

	// Other members of the data hierarchy
	private final DatabaseHelper mDatabaseHelper;
	private final NetworkSmartMapClient mNetworkClient;

	// List containing ids of all Friends
	private final Set<Long> mFriendIds;
	private final Set<Long> mStrangerIds;
	private final Set<Long> mPublicEventIds;

	// SparseArrays containing instances
	private final LongSparseArray<Event> mPublicEventInstances;
	private final LongSparseArray<User> mFriendInstances;
	private final LongSparseArray<User> mStrangerInstances;

	// Listeners
	private final List<CacheListener> mListeners;

	private Cache() {
		// Init data hierarchy
		mDatabaseHelper = DatabaseHelper.getInstance();
		mNetworkClient = NetworkSmartMapClient.getInstance();

		// Init lists
		mFriendIds = new HashSet<Long>();
		mStrangerIds = new HashSet<Long>();
		mPublicEventIds = new HashSet<Long>();

		mPublicEventInstances = new LongSparseArray<Event>();
		mFriendInstances = new LongSparseArray<User>();
		mStrangerInstances = new LongSparseArray<User>();

		mListeners = new LinkedList<CacheListener>();
	}

	/**
	 * @param listener
	 *            Listener to be added
	 */
	public void addOnCacheListener(CacheListener listener) {
		mListeners.add(listener);
	}

	public List<Event> getAllEvents() {
		List<Event> result = new ArrayList<Event>();
		for (long id : mPublicEventIds) {
			Event event = mPublicEventInstances.get(id);
			if (event != null) {
				result.add(event);
			} else {
				assert false;
			}
		}

		return result;
	}

	/**
	 * @return a list containing all the user's Friends.
	 */
	public List<User> getAllFriends() {
		List<User> allFriends = new ArrayList<User>();
		for (Long id : mFriendIds) {
			User friend = mFriendInstances.get(id);
			if (friend != null) {
				allFriends.add(friend);
			} else {
				assert false;
			}
		}
		return allFriends;
	}

	/**
	 * @return a list containing all the user's Going Events.
	 */
	public List<Event> getAllGoingEvents() {
		List<Event> allGoingEvents = new ArrayList<Event>();
		long myId = SettingsManager.getInstance().getUserID();
		for (Long id : mPublicEventIds) {
			Event event = mPublicEventInstances.get(id);
			if ((event != null) && event.getParticipants().contains(myId)) {
				allGoingEvents.add(event);
			} else {
				assert false;
			}
		}
		return allGoingEvents;
	}

	public List<Displayable> getAllVisibleEvents() {

		List<Displayable> allVisibleEvents = new ArrayList<Displayable>();
		for (Long id : mPublicEventIds) {
			Event event = mPublicEventInstances.get(id);
			if ((event != null) && event.isVisible()) {
				allVisibleEvents.add(event);
			}
		}
		return allVisibleEvents;
	}

	public List<Displayable> getAllVisibleFriends() {
		List<Displayable> allVisibleUsers = new ArrayList<Displayable>();
		for (Long id : mFriendIds) {
			User user = mFriendInstances.get(id);
			if ((user != null) && user.isVisible()) {
				allVisibleUsers.add(user);
			}
		}

		return allVisibleUsers;
	}

	public User getFriend(long id) {
		return mFriendInstances.get(id);
	}

	public Set<User> getFriends(Set<Long> ids) {
		Set<User> friends = new HashSet<User>();
		for (long id : ids) {
			User friend = this.getStranger(id);
			if (friend != null) {
				friends.add(friend);
			}
		}
		return friends;
	}

	public Event getPublicEvent(long id) {
		return mPublicEventInstances.get(id);
	}

	public Set<Event> getPublicEvents(Set<Long> ids) {
		Set<Event> events = new HashSet<Event>();
		for (long id : ids) {
			Event event = this.getPublicEvent(id);
			if (event != null) {
				events.add(event);
			}
		}
		return events;
	}

	public User getStranger(long id) {
		return mStrangerInstances.get(id);
	}

	public Set<User> getStrangers(Set<Long> ids) {
		Set<User> strangers = new HashSet<User>();
		for (long id : ids) {
			User stranger = this.getStranger(id);
			if (stranger != null) {
				strangers.add(stranger);
			}
		}
		return strangers;
	}

	public void initFromDatabase(DatabaseHelper database) {
		// Clear previous values
		mPublicEventInstances.clear();
		mFriendInstances.clear();
		mStrangerInstances.clear();

		// Clear lists
		mFriendIds.clear();
		mPublicEventIds.clear();
		mStrangerIds.clear();

		// Initialize id Lists
		mFriendIds.addAll(database.getFriendIds());
		// mPublicEventIds.addAll(DatabaseHelper.getInstance().getEventIds());

		// Fill with database values
		for (long id : mFriendIds) {
			mFriendInstances.put(id, new Friend(database.getFriend(id)));
		}
		for (long id : mPublicEventIds) {
			mPublicEventInstances.put(id, new PublicEvent(database.getEvent(id)));
		}

		// Notify listeners
		for (CacheListener listener : mListeners) {
			listener.onEventListUpdate();
			listener.onFriendListUpdate();
		}
	}

	// /**
	// * @return a list containing all the people who has invited the users
	// */
	// public List<User> getAllInvitingUsers() {
	// List<User> allInvitingUsers = new ArrayList<User>();
	// for (Long id : mInvitingUserIds) {
	// User invitingUser = this.getUserById(id);
	// if (invitingUser != null) {
	// allInvitingUsers.add(this.getStrangerById(id));
	// }
	// }
	// return allInvitingUsers;
	// }

	/**
	 * Add a Friend, and fill the cache with its informations.
	 * 
	 * @param id
	 */
	public void putFriend(ImmutableUser newFriend) {
		Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
		singleton.add(newFriend);
		this.putFriends(singleton);
	}

	public void putFriends(Set<ImmutableUser> newFriends) {
		boolean isListModified = false;

		for (ImmutableUser newFriend : newFriends) {
			if (mFriendInstances.get(newFriend.getId()) == null) {
				// Need to add it
				mFriendInstances.put(newFriend.getId(), new Friend(newFriend));
				isListModified = true;
			} else {
				// Only update
				mFriendInstances.get(newFriend.getId()).update(newFriend);
			}
		}

		// Notify listeners if needed
		if (isListModified) {
			for (CacheListener listener : mListeners) {
				listener.onFriendListUpdate();
			}
		}
	}

	/**
	 * Mark an Event as Going and fill the cache with its informations.
	 * 
	 * @param id
	 */
	public void putPublicEvent(ImmutableEvent newEvent) {
		Set<ImmutableEvent> singleton = new HashSet<ImmutableEvent>();
		singleton.add(newEvent);
		this.putPublicEvents(singleton);
	}

	public void putPublicEvents(Set<ImmutableEvent> newEvents) {
		boolean isListModified = false;

		for (ImmutableEvent newEvent : newEvents) {
			if (mPublicEventInstances.get(newEvent.getID()) == null) {
				// Need to add it
				mPublicEventInstances.put(newEvent.getID(), new PublicEvent(newEvent));
				isListModified = true;
			} else {
				// Only update
				mPublicEventInstances.get(newEvent.getID()).update(newEvent);
			}
		}

		// Notify listeners if needed
		if (isListModified) {
			for (CacheListener listener : mListeners) {
				listener.onEventListUpdate();
			}
		}
	}

	/**
	 * Fill Cache with an unknown User's informations.
	 * 
	 * @param user
	 */
	public void putStranger(ImmutableUser newStranger) {
		Set<ImmutableUser> singleton = new HashSet<ImmutableUser>();
		singleton.add(newStranger);
		this.putStrangers(singleton);
	}

	public void putStrangers(Set<ImmutableUser> newStrangers) {
		boolean isListModified = false;

		for (ImmutableUser newStranger : newStrangers) {
			if (mStrangerInstances.get(newStranger.getId()) == null) {
				// Need to add it
				mStrangerInstances.put(newStranger.getId(), new Friend(newStranger));
				isListModified = true;
			} else {
				// Only update
				mStrangerInstances.get(newStranger.getId()).update(newStranger);
			}
		}

		// Notify listeners if needed
		if (isListModified) {
			for (CacheListener listener : mListeners) {
				listener.onFriendListUpdate();
			}
		}
	}

	public void removeFriend(long id) {
		mFriendIds.remove(id);

		// Notify listeners
		for (CacheListener l : mListeners) {
			l.onFriendListUpdate();
		}
	}

	public void updateFromNetwork(SmartMapClient networkClient) throws SmartMapClientException {
		// Fetch friend ids
		HashSet<Long> newFriendIds = new HashSet<Long>(networkClient.getFriendsIds());

		// Remove friends that are no longer friends
		for (long id : mFriendIds) {
			if (!newFriendIds.contains(id)) {
				this.removeFriend(id);
			}
		}

		// Sets new friend ids
		mFriendIds.clear();
		mFriendIds.addAll(newFriendIds);

		// Update each friends
		for (long id : newFriendIds) {
			User friend = this.getFriend(id);

			// Get online values
			ImmutableUser onlineValues = networkClient.getUserInfo(id);
			if (friend != null) {
				// Simply update
				friend.update(onlineValues);
			} else {
				// Add friend
				this.putFriend(onlineValues);
			}
		}

		// TODO : Update Events
	}

	public boolean updatePublicEvent(ImmutableEvent event) {
		// Check in cache
		Event cachedEvent = mPublicEventInstances.get(event.getID());

		if (cachedEvent == null) {
			// Not in cache
			cachedEvent = new PublicEvent(event);
			mPublicEventInstances.put(event.getID(), cachedEvent);
			return true;
		} else {
			// In cache
			cachedEvent.update(event);
			return false;
		}
	}

	public boolean updateFriend(ImmutableUser user) {
		// Check in cache
		User cachedFriend = mFriendInstances.get(user.getId());

		if (cachedFriend == null) {
			// Not in cache
			cachedFriend = new Friend(user);
			mFriendInstances.put(user.getId(), cachedFriend);
			mDatabaseHelper.addUser(user);
			return true;
		} else {
			// In cache
			cachedFriend.update(user);
			return false;
		}
	}

	public static Cache getInstance() {
		return ONE_INSTANCE;
	}
}
