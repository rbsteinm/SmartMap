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
public class EventCache {
    // Class Map containing all unique instances of Friend
    @SuppressLint("UseSparseArrays")
    private static final Map<Long, PublicEvent> PUBLIC_EVENT_INSTANCES = new HashMap<Long, PublicEvent>();

    private static final List<CacheListener> INSTANCES_LISTENERS =
        new LinkedList<CacheListener>();

    public static void addOnListUpdateListener(CacheListener listener) {
        INSTANCES_LISTENERS.add(listener);
    }

    public static void addPublicEvent(ImmutableEvent event) {
        // Create new Friend and puts it in the cache
        PublicEvent publicEvent = new PublicEvent(event);
        PUBLIC_EVENT_INSTANCES.put(publicEvent.getId(), publicEvent);

        // Call listeners
        for (CacheListener listener : INSTANCES_LISTENERS) {
            listener.onElementAdded(publicEvent.getId());
        }
    }

    public static List<Event> getAllPublicEvents() {
        return Arrays.asList((Event[]) PUBLIC_EVENT_INSTANCES.values().toArray());
    }

    public static Event getEventById(long id) {
        return Event.NOT_FOUND;
    }

    public static PublicEvent getPublicEventById(long id) {
        // Try to get friend from cache
        PublicEvent publicEvent = PUBLIC_EVENT_INSTANCES.get(Long.valueOf(id));

        if (publicEvent == Event.NOT_FOUND) {
            // Try to get friend from local database
            ImmutableEvent event = DatabaseHelper.getInstance().getEvent(id);

            if (event != null) {
                publicEvent = new PublicEvent(event);
                PUBLIC_EVENT_INSTANCES.put(Long.valueOf(id), publicEvent);
            }
        }

        return publicEvent;
    }

    public static void removeOnListUpdateListener(CacheListener listener) {
        INSTANCES_LISTENERS.remove(listener);
    }

    public static void removePublicEvent(long id) {
        // Remove from cache
        PUBLIC_EVENT_INSTANCES.remove(id);

        // Call listeners
        for (CacheListener listener : INSTANCES_LISTENERS) {
            listener.onElementRemoved(id);
        }
    }

}
