package ch.epfl.smartmap.cache;

import java.util.List;

/**
 * Provides a method {@code sendQuery(String query)} that returns a list of Friends.
 * 
 * @author jfperren
 */
public interface SearchEngine {
    public List<Friend> sendQuery(String query);
}
