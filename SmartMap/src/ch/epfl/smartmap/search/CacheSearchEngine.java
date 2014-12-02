package ch.epfl.smartmap.search;

import java.util.List;

import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.History;

/**
 * @author jfperren
 */
public class CacheSearchEngine implements SearchEngine {

    private final Cache mCache;

    public CacheSearchEngine() {
        mCache = Cache.getInstance();
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
     * @see ch.epfl.smartmap.search.SearchEngine#sendQuery(java.lang.String,
     * ch.epfl.smartmap.search.SearchEngine.Type)
     */
    @Override
    public List<Displayable> sendQuery(String query, Type searchType) {
        // TODO Auto-generated method stub
        return null;
    }

}
