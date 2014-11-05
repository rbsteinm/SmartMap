package ch.epfl.smartmap.test.cache;

import org.junit.Test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.LongSparseArray;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.FriendList;
import ch.epfl.smartmap.cache.User;

/**
 * Tests for the DatabaseHelper class
 * @author ritterni
 */
public class DatabaseHelperTest extends AndroidTestCase {
    
    private final String name = "test name";
    private Friend a = new Friend(1234, "qwertz uiop");
    private Friend b = new Friend(0, "hcjkehfkl");
    private Friend c = new Friend(9909, "Abc Def");
    private DatabaseHelper dbh;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dbh = new DatabaseHelper(new RenamingDelegatingContext(getContext(), "test_")); 
        //to avoid erasing the actual database
        
        //dbh.getWritableDatabase();
        dbh.clearAll();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbh.clearAll();
        dbh.close();
    }
    
    @Test
    public void testAddUser() {
        dbh.addUser(a);
        assertTrue(dbh.getUser(a.getID()).getID() == a.getID() && dbh.getUser(a.getID()).getName().equals(a.getName()));
    }
    
    @Test
    public void testGetAllUsers() {
        dbh.addUser(a);
        dbh.addUser(b);
        dbh.addUser(c);
        LongSparseArray<User> list = dbh.getAllUsers();
        assertTrue(list.get(c.getID()).getID() == c.getID());
    }
    
    @Test
    public void testUpdateUser() {
        dbh.addUser(a);
        dbh.addUser(b);
        a.setName(name);
        int rows = dbh.updateUser(a);
        assertTrue(dbh.getUser(a.getID()).getName().equals(name) && rows == 1);
    }
    
    @Test
    public void testDeleteUser() {
        dbh.addUser(a);
        dbh.addUser(b);
        dbh.addUser(c);
        dbh.deleteUser(b.getID());
        LongSparseArray<User> list = dbh.getAllUsers();
        assertTrue(list.size() == 2 && list.get(a.getID()).getID() == a.getID());
    }
    
    @Test
    public void testAddFilter() {
        FriendList filter = new FriendList(name);
        filter.addUser(a.getID());
        filter.addUser(b.getID());
        filter.addUser(c.getID());
        long id = dbh.addFilter(filter);
        assertTrue(dbh.getFilter(id).getListName().equals(filter.getListName())
                && dbh.getFilter(id).getList().contains(b.getID()));
    }

    @Test
    public void testDeleteFilter() {
        FriendList filter = new FriendList(name);
        filter.addUser(a.getID());
        filter.addUser(b.getID());
        filter.addUser(c.getID());
        dbh.addFilter(filter);
        dbh.deleteFilter(filter.getID());
        assertTrue(dbh.getAllFilters().isEmpty());
    }
}
