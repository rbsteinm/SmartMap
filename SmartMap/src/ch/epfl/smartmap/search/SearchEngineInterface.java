package ch.epfl.smartmap.search;

import java.util.List;
import java.util.Set;

import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.callbacks.SearchRequestCallback;

/**
 * Provides some useful search functions for {@code AddFriendActivity} or {@code SearchLayout}
 * 
 * @author jfperren
 */
public interface SearchEngineInterface {

    /**
     * Use an {@code AsyncTask} to look for the Event with given Id successively in Cache, Database and
     * Server. If found, puts it in the {@code Cache} and give it to the {@code SearchRequestCallback}, and if
     * not, calls {@code SearchRequestCallback.onNotFound()}.
     * 
     * @param id
     *            Id of the Event your are looking for
     * @param callback
     *            Gets notified with the result when it arrives.
     */
    void findEventById(final long id, final SearchRequestCallback<Event> callback);

    /**
     * Use an {@code AsyncTask} to look for the Events with given Ids successively in Cache, Database and
     * Server. Those found will be put in the {@code Cache} and fed to the {@code SearchRequestCallback}
     * 
     * @param id
     *            a {@code Set} with all Ids of the Events you are looking for
     * @param callback
     *            Gets notified with the result when it arrives.
     */
    void findEventsByIds(final Set<Long> ids, final SearchRequestCallback<Set<Event>> callback);

    /**
     * Use an {@code AsyncTask} to look for the {@Stranger}s that match the query successively in
     * Cache, Database and Server. If found, puts it in the {@code Cache} and give it to the
     * {@code SearchRequestCallback}, and if not, calls {@code SearchRequestCallback.onNotFound()}.
     * 
     * @param id
     *            Id of the Event your are looking for
     * @param callback
     *            Gets notified with the result when it arrives.
     */
    void findStrangersByName(final String query, final SearchRequestCallback<Set<User>> callback);

    /**
     * Use an {@code AsyncTask} to look for the Event with given Id successively in Cache, Database and
     * Server. If found, puts it in the {@code Cache} and give it to the {@code SearchRequestCallback}, and if
     * not, calls {@code SearchRequestCallback.onNotFound()}.
     * 
     * @param id
     *            Id of the Event your are looking for
     * @param callback
     *            Gets notified with the result when it arrives.
     */
    void findUserById(final long id, final SearchRequestCallback<User> callback);

    /**
     * Use an {@code AsyncTask} to look for the Users with given Ids successively in Cache, Database and
     * Server. Those found will be put in the {@code Cache} and fed to the {@code SearchRequestCallback}
     * 
     * @param id
     *            a {@code Set} with all Ids of the Users you are looking for
     * @param callback
     *            Gets notified with the result when it arrives.
     */
    void findUsersByIds(final Set<Long> ids, final SearchRequestCallback<Set<User>> callback);

    /**
     * Sends a Query, computes it and return matched results
     * 
     * @param query
     * @return a List containing the result
     */
    List<Displayable> sendQuery(String query, Type searchType);

    /**
     * Define the diffent type of searches that can be performed on
     * the SearchEngine, in case of a cache search
     * 
     * @author jfperren
     */
    public enum Type {
        FRIENDS("Friends"),
        EVENTS("Events"),
        TAGS("Filters"),
        GROUPS("Groups"),
        HISTORY("History"),
        ALL("Everything");

        private final String mTitle;

        private Type(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }
}