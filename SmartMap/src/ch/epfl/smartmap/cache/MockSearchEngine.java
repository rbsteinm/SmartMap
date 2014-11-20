package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Mock class that returns filtered Lists from {@code MockDB}.
 * 
 * @author jfperren
 */
public class MockSearchEngine implements SearchEngine {

    private static final String TAG = "MOCK_SEARCH_ENGINE";
    @SuppressWarnings("unused")
    private static final String AUDIT_TAG = "Audit : " + TAG;

    private final History mHistory;
    private final List<User> mUsers;

    /**
     * Constructor
     */
    public MockSearchEngine(List<User> users) {
        mHistory = new SortedByDayHistory();
        mUsers = users;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#sendQuery(java.lang.String)
     */
    @Override
    public List<Friend> sendQuery(String query) {
        query = query.toLowerCase(Locale.US);
        ArrayList<Friend> result = new ArrayList<Friend>();

        for (User f : mUsers) {
            if (f.getName().toLowerCase(Locale.US).contains(query)
                || query.equals("")) {
                result.add((Friend) f);
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#getHistory()
     */
    @Override
    public History getHistory() {
        return mHistory;
    }
}
