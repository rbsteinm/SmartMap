package ch.epfl.smartmap.search;

import java.util.List;

import ch.epfl.smartmap.cache.Displayable;

/**
 * Provides a method {@code sendQuery(String query)} that returns a list of
 * Friends.
 * 
 * @author jfperren
 */
public interface SearchEngine {

    /**
     * Sends a Query, computes it and return matched results
     * 
     * @param query
     * @return a List containing the result
     */
    List<Displayable> sendQuery(String query, Type searchType);

    /**
     * Define the diffent type of searches that can be performed on
     * the SearchEngine
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