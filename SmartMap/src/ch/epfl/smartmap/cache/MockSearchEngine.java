/**
 * 
 */
package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jfperren
 *
 */
public class MockSearchEngine implements SearchEngine {

    /* (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#sendQuery(java.lang.String)
     */
    @Override
    public List<Friend> sendQuery(String query) {
        if(query == ""){
            return new ArrayList<Friend>();
        }
        query = query.toLowerCase(Locale.US);
        ArrayList<Friend> result = new ArrayList<Friend>();
        MockDB mMockDB = new MockDB();
        
        for (Friend f : mMockDB.FRIENDS_LIST){
            if(f.getName().toLowerCase(Locale.US).contains(query)){
                result.add(f);
            }
        }
        
        return result;
    }

}
