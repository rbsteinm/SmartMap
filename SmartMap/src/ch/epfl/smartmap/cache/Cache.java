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
    private List<Long> mInvitingUserIds;

    // Lists containing ids of all pinned/going events
    private List<Long> mNearEventIds;
    private List<Long> mGoingEventIds;
    private List<Long> mMyEventIds;

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
        mFriendIds = new ArrayList<Long>();

        mNearEventIds = new ArrayList<Long>();
        mGoingEventIds = new ArrayList<Long>();

        mPublicEventInstances = new LongSparseArray<Event>();
        mFriendInstances = new LongSparseArray<User>();
        mStrangerInstances = new LongSparseArray<User>();

        mListeners = new LinkedList<CacheListener>();
    }

    public void addFriend(long id) {
        // TODO : Notify database
        mFriendIds.add(id);
        mStrangerInstances.remove(id);
        this.getFriendById(id);

        for (CacheListener listener : mListeners) {
            listener.onFriendListUpdate();
        }
    }

    public void addGoingEvent(long id) {
        // TODO : Notify database
        mGoingEventIds.add(id);
        this.getPublicEventById(id);
        for (CacheListener listener : mListeners) {
            listener.onGoingEventListUpdate();
        }
    }

    public void addMyEvent(long id) {
        // TODO : Notify database
        mMyEventIds.add(id);
        this.getPublicEventById(id);
    }

    public void addNearEvent(long id) {
        mNearEventIds.add(id);
        for (CacheListener listener : mListeners) {
            listener.onNearEventListUpdate();
        }
    }

    public void addOnCacheListener(CacheListener listener) {
        mListeners.add(listener);
    }

    public void addPendingFriend(long id) {
        mPendingFriendIds.add(id);
        for (CacheListener listener : mListeners) {
            listener.onPendingFriendListUpdate();
        }
    }

    public List<User> getAllFriends() {
        List<User> allFriends = new ArrayList<User>();
        for (Long id : mFriendIds) {
            User friend = this.getFriendById(id);
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

    public List<User> getAllInvitingUsers() {
        List<User> allInvitingUsers = new ArrayList<User>();
        for (Long id : mInvitingUserIds) {
            allInvitingUsers.add(this.getStrangerById(id));
        }
        return allInvitingUsers;
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

    public List<Displayable> getAllVisibleEvents() {
        List<Displayable> allVisibleEvents = new ArrayList<Displayable>();
        for (Long id : mFriendIds) {
            Event event = this.getEventById(id);
            if (event.isVisible()) {
                allVisibleEvents.add(event);
            }
        }
        return allVisibleEvents;
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

    public List<Displayable> getAllVisibleFriends() {
        List<Displayable> allVisibleUsers = new ArrayList<Displayable>();
        for (Long id : mFriendIds) {
            User user = this.getFriendById(id);
            if (user.isVisible()) {
                allVisibleUsers.add(user);
            }
        }

        return allVisibleUsers;
    }

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
    public User getFriendById(long id) {
        // Check for live instance
        User friend = mFriendInstances.get(id);

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

    public Event getPublicEventById(long id) {
        // Check for live instance
        Event publicEvent = mPublicEventInstances.get(id);

        if (publicEvent == null) {
            // If not found, check on the server
            ImmutableEvent networkResult = null;
            // try {
            // networkResult = mNetworkClient.getEventInfo();
            // } catch (SmartMapClientException e) {
            // networkResult = null;
            // }

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
    public User getStrangerById(long id) {
        // Check for live instance
        User stranger = mStrangerInstances.get(id);

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
        User friend = this.getFriendById(id);
        if (friend == null) {
            // Search as Stranger
            User stranger = this.getStrangerById(id);
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

    public void setFriendList(List<Long> newIds) {
        mFriendIds.clear();
        mFriendIds.addAll(newIds);

        // Gets all friends
        for (Long id : newIds) {
            this.getFriendById(id);
        }
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
        User cachedFriend = mFriendInstances.get(user.getId());

        if (cachedFriend == null) {
            // Not in cache
            cachedFriend = new Friend(user);
            mFriendInstances.put(user.getId(), cachedFriend);
            return true;
        } else {
            // In cache
            cachedFriend.update(user);
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
