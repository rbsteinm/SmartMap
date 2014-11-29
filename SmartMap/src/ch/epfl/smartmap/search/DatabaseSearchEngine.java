package ch.epfl.smartmap.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.History;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserCache;
import ch.epfl.smartmap.database.DatabaseHelper;

/**
 * @author jfperren
 */
public class DatabaseSearchEngine implements SearchEngine {

    private DatabaseHelper mDatabaseHelper;

    private List<User> mFriends;
    private List<Event> mEvents;
    private List<Filter> mFilters;

    public DatabaseSearchEngine(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;

        this.updateLists();
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#getHistory()
     */
    @Override
    public History getHistory() {
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
                for (User f : mFriends) {
                    if (f.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(f);
                    }
                }
                break;
            case EVENTS:
                for (Event e : mEvents) {
                    if (e.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(e);
                    }
                }
                break;
            case TAGS:
                for (Filter f : mFilters) {
                    if (f.getName().toLowerCase(Locale.US).contains(query)) {
                        results.add(f);
                    }
                }
                break;
            default:
                break;
        }
        return results;
    }

    private void updateLists() {
        mFriends = UserCache.getAllFriends();
        // mEvents = EventCache.getAllEvents();
        // mFilters = mDatabaseHelper.getAllFilters();
    }

}
