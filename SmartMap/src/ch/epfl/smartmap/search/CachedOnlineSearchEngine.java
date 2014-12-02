package ch.epfl.smartmap.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.location.Location;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.History;
import ch.epfl.smartmap.cache.ImmutableUser;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author jfperren
 */
public final class CachedOnlineSearchEngine implements SearchEngine {

    private static final CachedOnlineSearchEngine ONE_INSTANCE = new CachedOnlineSearchEngine();

    private static final float LOCATION_DISTANCE_THRESHHOLD = 0;

    private final Map<String, List<Long>> mPreviousOnlineStrangerSearches;
    private final List<Location> mPreviousOnlineEventSearchQueries;
    private final List<List<Long>> mPreviousOnlineEventSearchResults;

    private CachedOnlineSearchEngine() {
        mPreviousOnlineStrangerSearches = new HashMap<String, List<Long>>();

        mPreviousOnlineEventSearchQueries = new LinkedList<Location>();
        mPreviousOnlineEventSearchResults = new LinkedList<List<Long>>();
    }

    public List<User> findStrangerByQuery(String query) throws SmartMapClientException {
        List<User> result = new ArrayList<User>();

        if (mPreviousOnlineStrangerSearches.get(query) != null) {
            // Fetch in cache
            List<Long> localResult = mPreviousOnlineStrangerSearches.get(query);
            for (Long id : localResult) {
                User cachedUser = Cache.getInstance().getStrangerById(id);
                if (cachedUser != null) {
                    result.add(cachedUser);
                }
            }
        } else {
            // Fetch online
            List<ImmutableUser> networkResult = NetworkSmartMapClient.getInstance().findUsers(query);

            for (ImmutableUser user : networkResult) {
                User cachedUser = Cache.getInstance().getStrangerById(user.getId());
                if (cachedUser != null) {
                    result.add(cachedUser);
                }
            }
        }
        return result;
    }

    public List<Event> getAllNearEvents(Location location, double radius) throws SmartMapClientException {
        List<Long> resultIds = new ArrayList<Long>();
        boolean foundInCache = false;
        for (int i = 0; i < mPreviousOnlineEventSearchQueries.size(); i++) {
            if (mPreviousOnlineEventSearchQueries.get(i).distanceTo(location) < LOCATION_DISTANCE_THRESHHOLD) {
                resultIds = mPreviousOnlineEventSearchResults.get(i);
                foundInCache = true;
            }
        }

        if (!foundInCache) {
            resultIds =
                NetworkSmartMapClient.getInstance().getPublicEvents(location.getLatitude(),
                    location.getLongitude(), radius);
        }

        Cache.getInstance().addNearEvents(resultIds);
        List<Event> result = Cache.getInstance().getAllNearEvents();

        return result;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.search.SearchEngine#getHistory()
     */
    @Override
    public History getHistory() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#sendQuery(java.lang.String,
     * ch.epfl.smartmap.cache.SearchEngine.Type)
     */
    @Override
    public List<Displayable> sendQuery(String query, Type searchType) {
        query = query.toLowerCase(Locale.US);
        ArrayList<Displayable> results = new ArrayList<Displayable>();

        switch (searchType) {
            case ALL:
                results.addAll(this.sendQuery(query, Type.FRIENDS));
                results.addAll(this.sendQuery(query, Type.EVENTS));
                results.addAll(this.sendQuery(query, Type.TAGS));
                break;
            case FRIENDS:
                for (User f : Cache.getInstance().getAllFriends()) {
                    if (f.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(f);
                    }
                }
                break;
            case EVENTS:
                for (Event e : Cache.getInstance().getAllEvents()) {
                    if (e.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(e);
                    }
                }
                break;
            case TAGS:

                break;
            default:
                break;
        }
        return results;
    }

    public static CachedOnlineSearchEngine getInstance() {
        return ONE_INSTANCE;
    }
}
