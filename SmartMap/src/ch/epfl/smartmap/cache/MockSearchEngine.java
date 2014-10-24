/**
 * 
 */
package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jfperren
 *
 */
public class MockSearchEngine implements SearchEngine {

    private ArrayList<Friend> database;
    
    
    
    private Friend Julien = new Friend(0, "Julien Perrenoud");
    private Friend Alain = new Friend(1, "Alain Milliet");
    private Friend Robin = new Friend(2, "Robin Genolet");
    private Friend Matthieu = new Friend(3, "Matthieu Girod");
    private Friend Nicolas = new Friend(4, "Nicolas Ritter");
    private Friend Marion = new Friend(5, "Marion Sbai");
    private Friend Raphael = new Friend(6, "Raphael Steinmann");
    private Friend Hugo = new Friend(7, "Hugo Sbai");
    
    
    public MockSearchEngine(){
        database = new ArrayList<Friend>();
        database.add(Julien);
        database.add(Alain);
        database.add(Robin);
        database.add(Matthieu);
        database.add(Nicolas);
        database.add(Marion);
        database.add(Raphael);
        database.add(Hugo);
    }
    /* (non-Javadoc)
     * @see ch.epfl.smartmap.cache.SearchEngine#sendQuery(java.lang.String)
     */
    @Override
    public List<Friend> sendQuery(String query) {
        ArrayList<Friend> result = new ArrayList<Friend>();
        for (Friend f : database){
            if(f.getName().contains(query)){
                result.add(f);
            }
        }
        return result;
    }

}
