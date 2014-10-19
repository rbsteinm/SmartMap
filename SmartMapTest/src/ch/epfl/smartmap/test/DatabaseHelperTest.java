package ch.epfl.smartmap.test;

import java.util.List;

import org.junit.Test;

import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

import android.test.AndroidTestCase;

/**
 * Tests for the DatabaseHelper class
 * @author ritterni
 */
public class DatabaseHelperTest extends AndroidTestCase {
    
    private final String name = "test name";
    private Friend a = new Friend(1234, "qwertz uiop");
    private Friend b = new Friend(0, "hcjkehfkl");
    private Friend c = new Friend(9909, "Abc Def");
    
    @Test
    public void testAddUser() {
        DatabaseHelper dbh = new DatabaseHelper(getContext());
        dbh.addUser(a);
        assertTrue(dbh.getUser(a.getID()).getID() == a.getID() && dbh.getUser(a.getID()).getName().equals(a.getName()));
    }
    
    @Test
    public void testGetAllUsers() {
        DatabaseHelper dbh = new DatabaseHelper(getContext());
        dbh.addUser(a);
        dbh.addUser(b);
        dbh.addUser(c);
        List<User> list = dbh.getAllUsers();
        assertTrue(list.get(0).getID() == c.getID());
    }
    
    @Test
    public void testUpdateUser() {
        DatabaseHelper dbh = new DatabaseHelper(getContext());
        dbh.addUser(a);
        dbh.addUser(b);
        a.setName(name);
        int rows = dbh.updateUser(a);
        assertTrue(dbh.getUser(a.getID()).getName().equals(name) && rows == 1);
    }
}
