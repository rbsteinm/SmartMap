/**
 * 
 */
package ch.epfl.smartmap.cache;

import java.util.List;

/**
 * @author jfperren
 *
 */
public interface SearchEngine {
    public List<Friend> sendQuery(String query);
}
