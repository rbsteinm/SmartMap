/**
 * 
 */
package ch.epfl.smartmap.test.severcom;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkNotificationBag;
import ch.epfl.smartmap.servercom.NotificationBag;

/**
 * @author Pamoi
 */
public class NetworkNotificationBagTest extends TestCase {

	@SuppressWarnings("unused")
	@Test
	public void testContructor() {

		try {
			NotificationBag nb = new NetworkNotificationBag(null,
					new ArrayList<User>(), new ArrayList<Long>());
			fail("Exception was not raised by constructor !");
		} catch (IllegalArgumentException e) {
			// Success
		}

		try {
			NotificationBag nb = new NetworkNotificationBag(
					new ArrayList<User>(), null, new ArrayList<Long>());
			fail("Exception was not raised by constructor !");
		} catch (IllegalArgumentException e) {
			// Success
		}

		try {
			NotificationBag nb = new NetworkNotificationBag(
					new ArrayList<User>(), new ArrayList<User>(), null);
			fail("Exception was not raised by constructor !");
		} catch (IllegalArgumentException e) {
			// Success
		}
	}

	@Test
	public void testGetInvitingUsers() {

		List<User> invitersList = new ArrayList<User>();
		invitersList.add(new Friend(1, "Toto"));
		invitersList.add(new Friend(2, "Titi"));

		NotificationBag nb = new NetworkNotificationBag(invitersList,
				new ArrayList<User>(), new ArrayList<Long>());

		// Test for structural equality (with method equals), not reference
		// equality !
		assertTrue(
				"The IntitingUsers list is not equal to the one given to the constructor",
				invitersList.equals(nb.getInvitingUsers()));
	}

	@Test
	public void testGetNewFriends() {

		List<User> newFriends = new ArrayList<User>();
		newFriends.add(new Friend(1, "Toto"));
		newFriends.add(new Friend(2, "Titi"));

		NotificationBag nb = new NetworkNotificationBag(new ArrayList<User>(),
				newFriends, new ArrayList<Long>());

		// Test for structural equality (with method equals), not reference
		// equality !
		assertTrue(
				"The newFriends list is not equal to the one given to the constructor",
				newFriends.equals(nb.getNewFriends()));
	}

	@Test
	public void testGetRemovedFriendsIds() {

		List<Long> removedIds = new ArrayList<Long>();
		removedIds.add((long) 4);
		removedIds.add((long) 54);

		NotificationBag nb = new NetworkNotificationBag(new ArrayList<User>(),
				new ArrayList<User>(), removedIds);

		// Test for structural equality (with method equals), not reference
		// equality !
		assertTrue(
				"The newFriends list is not equal to the one given to the constructor",
				removedIds.equals(nb.getRemovedFriendsIds()));
	}

	@Test
	public void testInvitingFriendsDefensiveCopy() {

		List<User> invitersList = new ArrayList<User>();
		invitersList.add(new Friend(1, "Toto"));
		invitersList.add(new Friend(2, "Titi"));

		NotificationBag nb = new NetworkNotificationBag(invitersList,
				new ArrayList<User>(), new ArrayList<Long>());

		invitersList.add(new Friend(3, "Tata"));

		assertTrue(
				"The constructor does not make a defensive copy of inviting users list.",
				invitersList.size() != nb.getInvitingUsers().size());

		List<User> getList = nb.getInvitingUsers();
		getList.add(new Friend(4, "Tutu"));

		assertTrue(
				"The getter does not return a defensive copy of inviting users list.",
				getList.size() != nb.getInvitingUsers().size());
	}

	@Test
	public void testIRemovedFriendsDefensiveCopy() {

		List<Long> removedIds = new ArrayList<Long>();
		removedIds.add((long) 4);
		removedIds.add((long) 54);

		NotificationBag nb = new NetworkNotificationBag(new ArrayList<User>(),
				new ArrayList<User>(), removedIds);

		removedIds.add((long) 5);

		assertTrue(
				"The constructor does not make a defensive copy of removed ids list.",
				removedIds.size() != nb.getRemovedFriendsIds().size());

		List<Long> getList = nb.getRemovedFriendsIds();
		getList.add((long) 10);

		assertTrue(
				"The getter does not return a defensive copy of removed ids list.",
				getList.size() != nb.getRemovedFriendsIds().size());
	}

	@Test
	public void testNewFriendsDefensiveCopy() {

		List<User> friendsList = new ArrayList<User>();
		friendsList.add(new Friend(1, "Toto"));
		friendsList.add(new Friend(2, "Titi"));

		NotificationBag nb = new NetworkNotificationBag(new ArrayList<User>(),
				friendsList, new ArrayList<Long>());

		friendsList.add(new Friend(3, "Tata"));

		assertTrue(
				"The constructor does not make a defensive copy of new friends list.",
				friendsList.size() != nb.getNewFriends().size());

		List<User> getList = nb.getNewFriends();
		getList.add(new Friend(4, "Tutu"));

		assertTrue(
				"The getter does not return a defensive copy of new friends list.",
				getList.size() != nb.getNewFriends().size());
	}
}
