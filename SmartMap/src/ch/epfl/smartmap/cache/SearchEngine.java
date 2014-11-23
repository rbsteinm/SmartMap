package ch.epfl.smartmap.cache;

import java.util.List;

/**
 * Provides a method {@code sendQuery(String query)} that returns a list of
 * Friends.
 * 
 * @author jfperren
 */
public interface SearchEngine {

    /**
     * @return History of searches of this SearchEngine
     */
    History getHistory();

    /**
     * Sends a Query, computes it and return matched results
     * 
     * @param query
     * @return a List containing the result
     */
    List<Friend> sendQuery(String query);
}
