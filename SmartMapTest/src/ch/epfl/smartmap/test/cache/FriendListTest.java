package ch.epfl.smartmap.test.cache;

import org.junit.Test;

import android.test.AndroidTestCase;
import android.util.LongSparseArray;
import ch.epfl.smartmap.cache.DefaultFilter;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

public class FriendListTest extends AndroidTestCase {

	private final String listName = "Name List";
	private final String newListName = "New Name";
	private final LongSparseArray<User> array = new LongSparseArray<User>();
	private final Friend a = new Friend(1234, "qwertz uiop");
	private final Friend b = new Friend(0, "hcjkehfkl");
	private final Friend c = new Friend(9909, "Abc Def");

	@Test
	public void testAddUser() {
		DefaultFilter fl = new DefaultFilter(listName);
		fl.addUser(a.getID());
		assertTrue(fl.getList().get(0) == a.getID());
	}

	@Test
	public void testGetUserList() {
		array.put(1234, a);
		array.put(0, b);
		array.put(9909, c);
		DefaultFilter fl = new DefaultFilter(listName);
		fl.addUser(a.getID());
		fl.addUser(b.getID());
		fl.addUser(c.getID());
		assertTrue(fl.getUserList(array).get(0).equals(c));
	}

	@Test
	public void testRemoveUser() {
		DefaultFilter fl = new DefaultFilter(listName);
		fl.addUser(b.getID());
		fl.addUser(a.getID());
		fl.removeUser(b.getID());
		assertTrue(fl.getList().get(0) == a.getID());
	}

	@Test
	public void testSetListName() {
		DefaultFilter fl = new DefaultFilter(listName);
		fl.setListName(newListName);
		assertTrue(fl.getListName().equals(newListName));
	}
}
