package ch.epfl.smartmap.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.listeners.CacheListener;

/**
 * @author jfperren
 */
public class UserCache {

    // Class Map containing all unique instances of Friend
    @SuppressLint("UseSparseArrays")
    private static final Map<Long, Friend> FRIEND_INSTANCES = new HashMap<Long, Friend>();
    private static final Map<Long, Stranger> STRANGER_INSTANCES = new HashMap<Long, Stranger>();

    private static final List<CacheListener> FRIENDLIST_LISTENERS =
        new LinkedList<CacheListener>();
    private static final List<CacheListener> STRANGERLIST_LISTENERS =
        new LinkedList<CacheListener>();

    public static void addFriend(ImmutableUser user) {
        // Create new Friend and puts it in the cache
        Friend newFriend = new Friend(user);
        FRIEND_INSTANCES.put(user.getId(), newFriend);

        // Call listeners
        for (CacheListener listener : FRIENDLIST_LISTENERS) {
            listener.onElementAdded(user.getId());
        }
    }

    public static void addOnListUpdateListener(CacheListener listener) {
        FRIENDLIST_LISTENERS.add(listener);
    }

    public static void addStranger(ImmutableUser user) {
        Stranger stranger = new Stranger(user);
        STRANGER_INSTANCES.put(stranger.getId(), stranger);
    }

    public static List<User> getAllFriends() {
        return Arrays.asList((User[]) FRIEND_INSTANCES.values().toArray());
    }

    public static Friend getFriendById(long id) {
        // Try to get friend from cache
        Friend friend = FRIEND_INSTANCES.get(Long.valueOf(id));

        if (friend == User.NOT_FOUND) {
            // Try to get friend from local database
            ImmutableUser user = DatabaseHelper.getInstance().getFriend(id);

            if (user != null) {
                friend = new Friend(user);
                FRIEND_INSTANCES.put(Long.valueOf(id), friend);
            }
        }

        return friend;
    }

    public static Stranger getStrangerById(long id) {
        // Try to get friend from cache
        Stranger stranger = STRANGER_INSTANCES.get(Long.valueOf(id));

        if (stranger == User.NOT_FOUND) {
            // Try to get friend from local database
            // ImmutableUser user = DatabaseHelper.getInstance().getStranger(id);
            //
            // if (user != null) {
            // stranger = new Stranger(user);
            //
            // STRANGER_INSTANCES.put(Long.valueOf(id), stranger);
            // }
        }

        return stranger;
    }

    public static User getUserById(long id) {
        User user = getFriendById(id);

        if (user == User.NOT_FOUND) {
            user = getStrangerById(id);
        }

        if (user == User.NOT_FOUND) {
            // Query online
        }

        return user;
    }

    public static void removeFriend(long id) {
        // Remove from cache
        FRIEND_INSTANCES.remove(id);

        // Call listeners
        for (CacheListener listener : FRIENDLIST_LISTENERS) {
            listener.onElementRemoved(id);
        }
    }

    public static void removeOnListUpdateListener(CacheListener listener) {
        FRIENDLIST_LISTENERS.remove(listener);
    }
}
