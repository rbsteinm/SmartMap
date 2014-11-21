package ch.epfl.smartmap.cache;

import java.util.List;

/**
 * Provides a method {@code sendQuery(String query)} that returns a list of
 * Friends.
 * 
 * @author jfperren
 */
public interface SearchEngine {

    public enum Type {
        FRIENDS("Friends"),
        EVENTS("Events"),
        TAGS("Filters"),
        GROUPS("Groups"),
        HISTORY("History"),
        ALL("All");

        private final String mTitle;

        private Type(String title) {
            this.mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    /**
     * Sends a Query, computes it and return matched results
     * 
     * @param query
     * @return a List containing the result
     */
    List<Displayable> sendQuery(String query, Type searchType);

    /**
     * @return History of searches of this SearchEngine
     */
    History getHistory();
}
