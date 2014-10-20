package ch.epfl.smartmap.test;

import org.junit.Test;

import android.test.AndroidTestCase;
import android.util.SparseArray;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.FriendList;
import ch.epfl.smartmap.cache.User;

public class FriendListTest extends AndroidTestCase {

    private final String listName = "Name List";
    private final String newListName = "New Name";
    private SparseArray<User> array = new SparseArray<User>();
    private Friend a = new Friend(1234, "qwertz uiop");
    private Friend b = new Friend(0, "hcjkehfkl");
    private Friend c = new Friend(9909, "Abc Def");
    
    
    @Test
    public void testSetListName() {
        FriendList fl = new FriendList(listName);
        fl.setListName(newListName);
        assertTrue(fl.getListName().equals(newListName));
    }

    @Test
    public void testAddUser() {
        FriendList fl = new FriendList(listName);
        fl.addUser(a.getID());
        assertTrue(fl.getList().get(0) == a.getID());
    }

    @Test
    public void testRemoveUser() {
        FriendList fl = new FriendList(listName);
        fl.addUser(b.getID());
        fl.addUser(a.getID());
        fl.removeUser(b.getID());
        assertTrue(fl.getList().get(0) == a.getID());
    }

    @Test
    public void testGetUserList() {
        array.put(1234, a);
        array.put(0, b);
        array.put(9909, c);
        FriendList fl = new FriendList(listName);
        fl.addUser(a.getID());
        fl.addUser(b.getID());
        fl.addUser(c.getID());
        assertTrue(fl.getUserList(array).get(0).equals(c));
    }
}
