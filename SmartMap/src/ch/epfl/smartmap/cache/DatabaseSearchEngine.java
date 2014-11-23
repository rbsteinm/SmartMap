package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jfperren
 */
public class DatabaseSearchEngine implements SearchEngine {

    private DatabaseHelper mDatabaseHelper;

    private List<Friend> mFriends;
    private List<Event> mEvents;
    private List<UserList> mFilters;

    public DatabaseSearchEngine(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;

        this.updateLists();
    }

    private void updateLists() {
        mFriends = mDatabaseHelper.getAllFriends();
        mEvents = mDatabaseHelper.getAllEvents();
        mFilters = mDatabaseHelper.getAllFilters();
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
                for (Friend f : mFriends) {
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
                for (UserList f : mFilters) {
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

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#getHistory()
     */
    @Override
    public History getHistory() {
        return null;
    }

}
