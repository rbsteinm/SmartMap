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
    private DatabaseHelper mDatabaseHelper;
    private NetworkSmartMapClient mNetworkClient;

    // List containing ids of all Friends
    private List<Long> mFriendIds;
    // Lists containing ids of all pinned/going events
    private List<Long> mPinnedEventIds;
    private List<Long> mGoingEventIds;

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

        mPinnedEventIds = new ArrayList<Long>();
        mGoingEventIds = new ArrayList<Long>();

        mPublicEventInstances = new LongSparseArray<PublicEvent>();
        mFriendInstances = new LongSparseArray<Friend>();
        mStrangerInstances = new LongSparseArray<Stranger>();

        mListeners = new LinkedList<CacheListener>();
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

    public List<Event> getAllPinnedEvents() {
        List<Event> allPinnedEvents = new ArrayList<Event>();
        for (Long id : mGoingEventIds) {
            Event event = this.getEventById(id);
            if (event != null) {
                allPinnedEvents.add(event);
            }
        }
        return allPinnedEvents;
    }

    public List<Displayable> getAllVisibleLocalisable() {
        List<Localisable> allVisibleDisplayable = new ArrayList<Localisable>();
        for (Long id : mFriendIds) {
            User user = this.getFriendById(id);
            if (user.isVisible()) {
                allVisibleDisplayable.add(user);
            }
        }

        for (Long id : mPinnedEventIds) {
            Event event = this.getEventById(id);
            if (event.isVisible()) {
                allVisibleDisplayable.add(event);
            }
        }

        return allVisibleDisplayable;
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
    public Friend getFriendById(long id) {
        // Check for live instance
        Friend friend = mFriendInstances.get(id);

        if (friend == null) {
            // If not found, check in database
            ImmutableUser databaseResult = mDatabaseHelper.getFriendById(id);

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

    public static Cache getInstance() {
        return ONE_INSTANCE;
    }
}
