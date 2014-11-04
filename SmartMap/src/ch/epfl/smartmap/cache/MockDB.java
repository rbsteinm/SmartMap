/**
 * 
 */
package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hugo-S
 * 
 */
public class MockDB {
	
    
    public final List<Friend> FRIENDS_LIST;
    
    public final Friend JULIEN = new Friend(0, "Julien Perrenoud", 46.2547877,
        3.2142577);
    public final Friend ALAIN = new Friend(1, "Alain Milliet", 45.2547877,
        7.2142577);
    public final Friend ROBIN = new Friend(2, "Robin Genolet", 42.2547877,
        5.214577);
    public final Friend MATTHIEU = new Friend(3, "Matthieu Girod", 39.2547877,
        5.214577);
    public final Friend NICOLAS = new Friend(4, "Nicolas Ritter", 50.2547877,
        2.004577);
    public final Friend MARION = new Friend(5, "Marion Sbai", 41.5547877,
        2.4255457);
    public final Friend RAPHAEL = new Friend(6, "Raphael Steinmann", 39.5547877,
        7.4255457);
    public final Friend HUGO = new Friend(7, "Hugo Sbai", 47.5547877,
        4.4255457);

    public MockDB(){
      FRIENDS_LIST = new ArrayList<Friend>();
      FRIENDS_LIST.add(JULIEN);
      FRIENDS_LIST.add(ALAIN);
      FRIENDS_LIST.add(ROBIN);
      FRIENDS_LIST.add(MATTHIEU);
      FRIENDS_LIST.add(MARION);
      FRIENDS_LIST.add(NICOLAS);
      FRIENDS_LIST.add(RAPHAEL);
      FRIENDS_LIST.add(HUGO);
    }
}

