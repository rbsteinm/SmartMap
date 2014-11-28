package ch.epfl.smartmap.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import ch.epfl.smartmap.listeners.OnListUpdateListener;

/**
 * @author jfperren
 */
public class UserCache {

    // Class Map containing all unique instances of Friend
    @SuppressLint("UseSparseArrays")
    private static final Map<Long, Friend> FRIEND_INSTANCES = new HashMap<Long, Friend>();
    private static final Map<Long, Stranger> STRANGER_INSTANCES = new HashMap<Long, Stranger>();

    private static final List<OnListUpdateListener> FRIENDLIST_LISTENERS =
        new LinkedList<OnListUpdateListener>();
    private static final List<OnListUpdateListener> STRANGERLIST_LISTENERS =
        new LinkedList<OnListUpdateListener>();

    public static void addFriend(ImmutableUser user) {
        // Create new Friend and puts it in the cache
        Friend newFriend =
            new Friend(user.getID(), user.getName(), user.getPhoneNumber(), user.getLocationString(),
                user.getEmail(), user.getLocation());
        FRIEND_INSTANCES.put(user.getID(), newFriend);

        // Call listeners
        for (OnListUpdateListener listener : FRIENDLIST_LISTENERS) {
            listener.onElementAdded(user.getID());
        }
    }

    public static void addOnListUpdateListener(OnListUpdateListener listener) {
        FRIENDLIST_LISTENERS.add(listener);
    }

    public static void addStranger(ImmutableUser user) {
        Stranger stranger = new Stranger();
        STRANGER_INSTANCES.put(user.getID(), stranger);
    }

    public static List<User> getAllFriends() {
        return Arrays.asList((User[]) FRIEND_INSTANCES.values().toArray());
    }

    public static Friend getFriendFromId(long id) {
        // Try to get friend from cache
        Friend friend = FRIEND_INSTANCES.get(Long.valueOf(id));

        if (friend == User.NOT_FOUND) {
            // Try to get friend from local database
            User user = DatabaseHelper.getInstance().getFriend(id);

            if (user != User.NOT_FOUND) {
                friend =
                    new Friend(user.getID(), user.getName(), user.getPhoneNumber(), user.getEmail(),
                        user.getLocationString(), user.getLocation());

                FRIEND_INSTANCES.put(Long.valueOf(id), friend);
            }
        }

        return friend;
    }

    public static void removeFriend(long id) {
        // Remove from cache
        FRIEND_INSTANCES.remove(id);

        // Call listeners
        for (OnListUpdateListener listener : FRIENDLIST_LISTENERS) {
            listener.onElementRemoved(id);
        }
    }

    public static void removeOnListUpdateListener(OnListUpdateListener listener) {
        FRIENDLIST_LISTENERS.remove(listener);
    }

}
