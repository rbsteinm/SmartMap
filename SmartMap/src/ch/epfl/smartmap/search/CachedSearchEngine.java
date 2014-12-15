package ch.epfl.smartmap.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.FilterInterface;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.callbacks.SearchRequestCallback;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * Provides search functions for classes {@code SearchLayout} and {@code AddFriendActivity}. Usually search in
 * Cache, then in Database, then on Server. Use a small Cache to store the result of some previous queries.
 * 
 * @author jfperren
 */
public final class CachedSearchEngine implements SearchEngineInterface {

    private static final String TAG = CachedSearchEngine.class.getSimpleName();

    private final Map<String, Set<Long>> mPreviousOnlineStrangerSearches;
    private final List<Location> mPreviousOnlineEventSearchQueries;
    private final List<Set<Long>> mPreviousOnlineEventSearchResults;

    public CachedSearchEngine() {
        mPreviousOnlineStrangerSearches = new HashMap<String, Set<Long>>();

        mPreviousOnlineEventSearchQueries = new LinkedList<Location>();
        mPreviousOnlineEventSearchResults = new LinkedList<Set<Long>>();
    }

    @Override
    public void findEventById(final long id, final SearchRequestCallback<Event> callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Check for live instance
                Event event = ServiceContainer.getCache().getEvent(id);

                if (event != null) {
                    // Found in cache, return
                    if (callback != null) {
                        callback.onResult(event);
                    }
                    return null;
                } else {
                    // If not found, check in database
                    EventContainer databaseResult = ServiceContainer.getDatabase().getEvent(id);

                    if (databaseResult != null) {
                        // Match in database, put it in cache
                        ServiceContainer.getCache().putEvent(databaseResult);
                        if (callback != null) {
                            callback.onResult(ServiceContainer.getCache().getEvent(id));
                        }
                        return null;
                    } else {
                        // If not found, check on the server
                        EventContainer networkResult;
                        try {
                            networkResult = ServiceContainer.getNetworkClient().getEventInfo(id);
                        } catch (SmartMapClientException e) {
                            networkResult = null;
                            Log.e(TAG, "Error while finding public events by Ids" + e);
                        }

                        if (networkResult != null) {
                            // Match on server, put it in cache
                            ServiceContainer.getCache().putEvent(networkResult);
                            if (callback != null) {
                                callback.onResult(ServiceContainer.getCache().getEvent(id));
                            }
                            return null;
                        } else {
                            // No match anywhere
                            if (callback != null) {
                                callback.onNotFound();
                            }
                            return null;
                        }
                    }
                }
            }
        }.execute();
    }

    @Override
    public void findEventsByIds(final Set<Long> ids, final SearchRequestCallback<Set<Event>> callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Set<EventContainer> immutableResult = new HashSet<EventContainer>();
                Set<Event> result = new HashSet<Event>();

                for (long id : ids) {
                    // Check for live instance
                    Event event = ServiceContainer.getCache().getEvent(id);

                    if (event != null) {
                        // Found in cache, add to set of live instances
                        result.add(event);
                    } else {
                        // If not found, check in database
                        EventContainer databaseResult = ServiceContainer.getDatabase().getEvent(id);

                        if (databaseResult != null) {
                            immutableResult.add(databaseResult);
                        } else {
                            // If not found, check on the server
                            EventContainer networkResult;
                            try {
                                Log.d(TAG, "Try to get event online");
                                networkResult = ServiceContainer.getNetworkClient().getEventInfo(id);
                                Log.d(TAG, "Successfull");
                            } catch (SmartMapClientException e) {
                                networkResult = null;
                                Log.e(TAG, "Error while finding public events by Ids" + e);
                            }

                            if (networkResult != null) {
                                // Match on server, put it in cache
                                immutableResult.add(networkResult);
                            }
                        }
                    }
                }

                // Get all results that weren't in cache and add them all at
                // once (Avoid to send multiple
                // listener
                // calls)
                ServiceContainer.getCache().putEvents(immutableResult);
                // Retrieve live instances from cache
                for (EventContainer event : immutableResult) {
                    result.add(ServiceContainer.getCache().getEvent(event.getId()));
                }
                if (callback != null) {
                    callback.onResult(result);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void findUserById(final long id, final SearchRequestCallback<User> callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Check for live instance
                User user = ServiceContainer.getCache().getUser(id);

                if (user != null) {
                    // Found in cache, return
                    if (callback != null) {
                        callback.onResult(user);
                    }
                    return null;
                } else {
                    // If not found, check in database
                    UserContainer databaseResult = ServiceContainer.getDatabase().getUser(id);

                    if (databaseResult != null) {
                        // Match in database, put it in cache
                        ServiceContainer.getCache().putUser(databaseResult);
                        if (callback != null) {
                            callback.onResult(ServiceContainer.getCache().getUser(id));
                        }
                        return null;
                    } else {
                        // If not found, check on the server
                        UserContainer networkResult;
                        try {
                            networkResult = ServiceContainer.getNetworkClient().getUserInfo(id);
                            if (networkResult != null) {
                                // Match on server, put it in cache
                                ServiceContainer.getCache().putUser(networkResult);
                                if (callback != null) {

                                    callback.onResult(ServiceContainer.getCache().getUser(id));
                                }
                                return null;
                            } else {
                                // No match anywhere
                                if (callback != null) {
                                    callback.onNotFound();
                                }
                                return null;
                            }
                        } catch (SmartMapClientException e) {
                            Log.e(TAG, "Error while finding strangers by Ids" + e);
                            if (callback != null) {
                                callback.onNetworkError(e);
                            }
                            return null;
                        }
                    }
                }
            }
        }.execute();
    }

    @Override
    public void findStrangersByName(final String query, final SearchRequestCallback<Set<User>> callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Set<User> result = new HashSet<User>();

                if (mPreviousOnlineStrangerSearches.get(query) != null) {
                    // Fetch in cache
                    Set<Long> localResult = mPreviousOnlineStrangerSearches.get(query);
                    for (Long id : localResult) {
                        User cachedUser = ServiceContainer.getCache().getUser(id);
                        if (cachedUser != null) {
                            if ((cachedUser.getFriendship() != User.FRIEND)
                                && (cachedUser.getFriendship() != User.SELF)) {
                                result.add(cachedUser);
                            }
                        }
                    }
                } else {
                    // Fetch online
                    List<UserContainer> networkResult;
                    try {
                        networkResult = ServiceContainer.getNetworkClient().findUsers(query);
                        for (UserContainer user : networkResult) {
                            if (user != null) {
                                ServiceContainer.getCache().putUser(user);
                                result.add(ServiceContainer.getCache().getUser(user.getId()));
                            }
                        }
                    } catch (SmartMapClientException e) {
                        Log.e(TAG, "Error while finding strangers by query" + e);
                        if (callback != null) {
                            callback.onNetworkError(e);
                        }
                    }
                }
                if (callback != null) {
                    callback.onResult(result);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void findUsersByIds(final Set<Long> ids, final SearchRequestCallback<Set<User>> callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Set<UserContainer> immutableResult = new HashSet<UserContainer>();
                Set<User> result = new HashSet<User>();

                for (long id : ids) {
                    // Check for live instance
                    User stranger = ServiceContainer.getCache().getUser(id);

                    if (stranger != null) {
                        // Found in cache, add to set of live instances
                        result.add(stranger);
                    } else {
                        // If not found, check on the server
                        UserContainer networkResult;
                        try {
                            networkResult = ServiceContainer.getNetworkClient().getUserInfo(id);
                        } catch (SmartMapClientException e) {
                            Log.e(TAG, "Error while finding public users by Ids" + e);
                            networkResult = null;
                        }

                        if (networkResult != null) {
                            // Match on server, put it in cache
                            immutableResult.add(networkResult);
                        }
                    }
                }

                ServiceContainer.getCache().putUsers(immutableResult);
                // Retrieve live instances from cache
                for (UserContainer user : immutableResult) {
                    result.add(ServiceContainer.getCache().getUser(user.getId()));
                }

                // Give results to the caller
                if (callback != null) {
                    callback.onResult(result);
                }
                return null;
            }
        }.execute();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#sendQuery(java.lang.String,
     * ch.epfl.smartmap.cache.SearchEngine.Type)
     */
    @Override
    public List<Displayable> sendQuery(String query, Type searchType) {
        query = query.toLowerCase(Locale.US);
        List<Displayable> results = new ArrayList<Displayable>();

        switch (searchType) {
            case ALL:
                results.addAll(this.sendQuery(query, Type.FRIENDS));
                results.addAll(this.sendQuery(query, Type.EVENTS));
                results.addAll(this.sendQuery(query, Type.TAGS));
                break;
            case FRIENDS:
                for (User f : ServiceContainer.getCache().getAllFriends()) {
                    if (f.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(f);
                    }
                }
                break;
            case EVENTS:
                for (Event e : ServiceContainer.getCache().getAllEvents()) {
                    if (e.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(e);
                    }
                }
                break;
            case TAGS:
                for (FilterInterface f : ServiceContainer.getCache().getAllCustomFilters()) {
                    if ((f.getName() != null) && f.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(f);
                    }
                }

                break;
            default:
                break;
        }
        return results;
    }
}
