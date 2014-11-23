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
	private final List<Displayable> mUsers;
	private final List<Displayable> mEvents;

	/**
	 * Constructor
	 */
	public MockSearchEngine() {
		mHistory = new SortedByDayHistory();
		mEvents = MockDB.EVENTS_LIST;
		mUsers = MockDB.FRIENDS_LIST;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.SearchEngine#getHistory()
	 */
	@Override
	public History getHistory() {
		return mHistory;
	}

	/*
	 * (non-Javadoc)
	 * @see ch.epfl.smartmap.cache.SearchEngine#sendQuery(java.lang.String)
	 */
	@Override
	public List<Displayable> sendQuery(String query, SearchEngine.Type searchType) {
		query = query.toLowerCase(Locale.US);
		ArrayList<Displayable> result = new ArrayList<Displayable>();

		if (searchType == Type.ALL) {
			if (query.equals("")) {
				result.addAll(mUsers);
				result.addAll(mEvents);
			} else {
				for (Displayable f : mUsers) {
					if (f.getName().toLowerCase(Locale.US).contains(query)) {
						result.add(f);
					}
				}
				for (Displayable f : mEvents) {
					if (f.getName().toLowerCase(Locale.US).contains(query)) {
						result.add(f);
					}
				}
			}
		} else if (searchType == Type.FRIENDS) {
			if (query.equals("")) {
				result.addAll(mUsers);
			} else {
				for (Displayable f : mUsers) {
					if (f.getName().toLowerCase(Locale.US).contains(query)) {
						result.add(f);
					}
				}
			}
		} else if (searchType == Type.EVENTS) {
			if (query.equals("")) {
				result.addAll(mEvents);
			} else {
				for (Displayable f : mEvents) {
					if (f.getName().toLowerCase(Locale.US).contains(query)) {
						result.add(f);
					}
				}
			}
		}

		return result;
	}
}
