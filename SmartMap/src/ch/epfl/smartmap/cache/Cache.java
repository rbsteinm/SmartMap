package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.util.LongSparseArray;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * Acts as a Cache for every item the GUI needs to access. When a value is not found, search through the local
 * database or the server and puts it in cache automatically.
 * 
 * @author jfperren
 */
public final class Cache {

    // Unique instance
    private static final Cache ONE_INSTANCE = new Cache();

    // Other members of the data hierarchy
    private final DatabaseHelper mDatabaseHelper;
    private final NetworkSmartMapClient mNetworkClient;

    // List containing ids of all Friends
    private List<Long> mFriendIds;
    private List<Long> mPendingFriendIds;

    // Lists containing ids of all pinned/going events
    private List<Long> mNearEventIds;
    private List<Long> mGoingEventIds;
    private List<Long> mMyEventIds;

    // SparseArrays containing instances
    private final LongSparseArray<PublicEvent> mPublicEventInstances;
    private final LongSparseArray<Friend> mFriendInstances;
    private final LongSparseArray<Stranger> mStrangerInstances;

    // Listeners
    private final List<CacheListener> mListeners;

    private Cache() {
        // Init data hierarchy
        mDatabaseHelper = DatabaseHelper.getInstance();
        mNetworkClient = NetworkSmartMapClient.getInstance();

        // Init lists
        mFriendIds = new ArrayList<Long>();

        mNearEventIds = new ArrayList<Long>();
        mGoingEventIds = new ArrayList<Long>();

        mPublicEventInstances = new LongSparseArray<PublicEvent>();
        mFriendInstances = new LongSparseArray<Friend>();
        mStrangerInstances = new LongSparseArray<Stranger>();

        mListeners = new LinkedList<CacheListener>();
    }

    public void addFriend(long id) {
        mFriendIds.add(id);
        mPendingFriendIds.remove(id);
        mStrangerInstances.remove(id);
        for (CacheListener listener : mListeners) {
            listener.onFriendListUpdate();
        }
    }

    public void addGoingEvent(long id) {
        mGoingEventIds.add(id);
        for (CacheListener listener : mListeners) {
            listener.onGoingEventListUpdate();
        }
    }

    public void addMyEvent(long id) {
        mMyEventIds.add(id);
    }

    public void addNearEvent(long id) {
        mNearEventIds.add(id);
        for (CacheListener listener : mListeners) {
            listener.onNearEventListUpdate();
        }
    }

    public void addPendingFriend(long id) {
        mPendingFriendIds.add(id);
        for (CacheListener listener : mListeners) {
            listener.onPendingFriendListUpdate();
        }
    }

    public List<Friend> getAllFriends() {
        List<Friend> allFriends = new ArrayList<Friend>();
        for (Long id : mFriendIds) {
            Friend friend = this.getFriendById(id);
            if (friend != null) {
                allFriends.add(friend);
            }
        }
        return allFriends;
    }

    public List<Event> getAllGoingEvents() {
        List<Event> allGoingEvents = new ArrayList<Event>();
        for (Long id : mGoingEventIds) {
            Event event = this.getEventById(id);
            if (event != null) {
                allGoingEvents.add(event);
            }
        }
        return allGoingEvents;
    }

    public List<Event> getAllNearEvents() {
        List<Event> allPinnedEvents = new ArrayList<Event>();
        for (Long id : mNearEventIds) {
            Event event = this.getEventById(id);
            if (event != null) {
                allPinnedEvents.add(event);
            }
        }
        return allPinnedEvents;
    }

    public List<Localisable> getAllVisibleMarkables() {
        List<Localisable> allVisibleMarkables = new ArrayList<Localisable>();
        for (Long id : mFriendIds) {
            User user = this.getFriendById(id);
            if (user.isVisible()) {
                allVisibleMarkables.add(user);
            }
        }

        for (Long id : mNearEventIds) {
            Event event = this.getEventById(id);
            if (event.isVisible()) {
                allVisibleMarkables.add(event);
            }
        }

        return allVisibleMarkables;
    }

    // public List<Event> getAllPublicEventsNear(Location location, double radius)
    // throws SmartMapClientException {
    // List<Event> ids =
    // mNetworkClient.getPublicEvents(location.getLatitude(), location.getLongitude(), radius);
    // List<Event> allPublicEventsNear = new ArrayList<Event>();
    //
    // for (Event event : ids) {
    // allPublicEventsNear.add(this.getPublicEventById(event.getId()));
    // }
    //
    // return allPublicEventsNear;
    // }

    public Event getEventById(long id) {
        // For the moment only check in public events
        return this.getPublicEventById(id);
    }

    /**
     * Search for a {@code Friend} with this id. For performance concerns, this method should be called in an
     * {@code AsyncTask}.
     * 
     * @param id
     *            long id of {@code Friend}
     * @return the {@code Friend} with given id, or {@code null} if there was no match.
     */
    public Friend getFriendById(long id) {
        // Check for live instance
        Friend friend = mFriendInstances.get(id);

        if (friend == null) {
            // If not found, check in database
            ImmutableUser databaseResult = mDatabaseHelper.getFriend(id);

            if (databaseResult == null) {
                // If not found, check on the server
                ImmutableUser networkResult;
                try {
                    networkResult = mNetworkClient.getFriendInfo(id);
                } catch (SmartMapClientException e) {
                    networkResult = null;
                }
                if (networkResult != null) {
                    // Match on server
                    friend = new Friend(networkResult);
                } else {
                    // No match anywhere
                    return null;
                }
            } else {
                // Match in database
                friend = new Friend(databaseResult);
            }
        }
        // At this point a friend was found either online or in the database, so we cache him
        mFriendInstances.put(Long.valueOf(friend.getId()), friend);
        return friend;
    }

    public PublicEvent getPublicEventById(long id) {
        // Check for live instance
        PublicEvent publicEvent = mPublicEventInstances.get(id);

        if (publicEvent == null) {
            // If not found, check on the server
            ImmutableEvent networkResult;
            try {
                networkResult = mNetworkClient.getEventInfo(id);
            } catch (SmartMapClientException e) {
                networkResult = null;
            }

            if (networkResult != null) {
                // Match on server
                publicEvent = new PublicEvent(networkResult);
            } else {
                // No match anywhere
                return null;
            }
        }
        // At this point a friend was found either online or in the database, so we cache him
        mPublicEventInstances.put(Long.valueOf(publicEvent.getId()), publicEvent);
        return publicEvent;
    }

    /**
     * Search for a {@code Friend} with this id. For performance concerns, this method should be called in an
     * {@code AsyncTask}.
     * 
     * @param id
     *            long id of {@code Friend}
     * @return the {@code Friend} with given id, or {@code null} if there was no match.
     */
    public Stranger getStrangerById(long id) {
        // Check for live instance
        Stranger stranger = mStrangerInstances.get(id);

        if (stranger == null) {
            // If not found, check on the server
            ImmutableUser networkResult;
            try {
                networkResult = mNetworkClient.getFriendInfo(id);
            } catch (SmartMapClientException e) {
                networkResult = null;
            }

            if (networkResult != null) {
                // Match on server
                stranger = new Stranger(networkResult);
            } else {
                // No match anywhere
                return null;
            }
        }
        // At this point a friend was found either online or in the database, so we cache him
        mStrangerInstances.put(Long.valueOf(stranger.getId()), stranger);
        return stranger;
    }

    /**
     * Search for a {@code Friend} with this id. For performance concerns, this method should be called in an
     * {@code AsyncTask}.
     * 
     * @param id
     *            long id of {@code Friend}
     * @return the {@code Friend} with given id, or {@code null} if there was no match.
     */
    public User getUserById(long id) {
        // Search as Friend first
        Friend friend = this.getFriendById(id);
        if (friend == null) {
            // Search as Stranger
            Stranger stranger = this.getStrangerById(id);
            if (stranger != null) {
                // Match as Stranger
                return stranger;
            } else {
                // No match
                return null;
            }
        } else {
            // Match as Friend
            return friend;
        }
    }

    public void removeFriend(long id) {
    }

    public void removeGoingEvent(long id) {
        mGoingEventIds.remove(id);
    }

    public void removeNearEvent(long id) {
        mNearEventIds.remove(id);
    }

    public void removePendingFriend(long id) {
        mPendingFriendIds.remove(id);
    }

    public void updateFriendList(List<ImmutableUser> newFriends) {
        // Delete previous list
        mFriendIds.clear();

        for (ImmutableUser user : newFriends) {
            mFriendIds.add(user.getId());
            this.updateFriend(user);
        }

        // Notify listeners
        for (CacheListener l : mListeners) {
            l.onFriendListUpdate();
        }
    }

    public void updateGoingEventList(List<ImmutableEvent> newEvents) {
        // Delete previous list
        mGoingEventIds.clear();

        for (ImmutableEvent event : newEvents) {
            mGoingEventIds.add(event.getID());
            this.updatePublicEvent(event);
        }

        // Notify listeners
        for (CacheListener l : mListeners) {
            l.onGoingEventListUpdate();
        }
    }

    public void updateNearPublicEventList(List<ImmutableEvent> newEvents) {
        // Delete previous list
        mNearEventIds.clear();

        for (ImmutableEvent event : newEvents) {
            mNearEventIds.add(event.getID());
            this.updatePublicEvent(event);
        }

        // Notify listeners
        for (CacheListener l : mListeners) {
            l.onNearEventListUpdate();
        }
    }

    public void updatePendingFriendList(List<ImmutableUser> newPendingFriends) {
        // Delete previous list
        mFriendIds.clear();

        for (ImmutableUser user : newPendingFriends) {
            mPendingFriendIds.add(user.getId());
            this.updateFriend(user);
        }

        // Notify listeners
        for (CacheListener l : mListeners) {
            l.onPendingFriendListUpdate();
        }
    }

    private boolean updateFriend(ImmutableUser user) {
        // Check in cache
        User cachedUser = mFriendInstances.get(user.getId());

        if (cachedUser == null) {
            // Not in cache
            cachedUser = new Friend(user);
            mFriendInstances.put(user.getId(), cachedUser);
            return true;
        } else {
            // In cache
            cachedUser.update(user);
            return false;
        }
    }

    private boolean updatePublicEvent(ImmutableEvent event) {
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

    public static Cache getInstance() {
        return ONE_INSTANCE;
    }
}
