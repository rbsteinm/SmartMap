// /**
// *
// */
// package ch.epfl.smartmap.test.severcom;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Set;
//
// import junit.framework.TestCase;
//
// import org.junit.Test;
//
// import ch.epfl.smartmap.servercom.NetworkNotificationBag;
// import ch.epfl.smartmap.servercom.NotificationBag;
//
// /**
// * @author Pamoi
// */
// public class NetworkNotificationBagTest extends TestCase {
//
// @SuppressWarnings("unused")
// @Test
// public void testContructor() {
//
// try {
// NotificationBag nb =
// new NetworkNotificationBag(null, new ArrayList<Long>(), new ArrayList<Long>());
// fail("Exception was not raised by constructor !");
// } catch (IllegalArgumentException e) {
// // Success
// }
//
// try {
// NotificationBag nb =
// new NetworkNotificationBag(new ArrayList<Long>(), null, new ArrayList<Long>());
// fail("Exception was not raised by constructor !");
// } catch (IllegalArgumentException e) {
// // Success
// }
//
// try {
// NotificationBag nb =
// new NetworkNotificationBag(new ArrayList<Long>(), new ArrayList<Long>(), null);
// fail("Exception was not raised by constructor !");
// } catch (IllegalArgumentException e) {
// // Success
// }
// }
//
// @Test
// public void testGetInvitingUsers() {
//
// List<Long> invitersList = Arrays.asList(Long.valueOf(1),Long.valueOf(2));
//
//
// NotificationBag nb =
// new NetworkNotificationBag(invitersList, new ArrayList<Long>(), new ArrayList<Long>());
//
// // Test for structural equality (with method equals), not reference equality !
// assertTrue("The IntitingUsers list is not equal to the one given to the constructor",
// invitersList.equals(nb.getInvitingUsers()));
// }
//
// @Test
// public void testGetNewFriends() {
//
// List<Long> newFriends = Arrays.asList(Long.valueOf(1),Long.valueOf(2));
//
//
// NotificationBag nb =
// new NetworkNotificationBag(new ArrayList<Long>(), newFriends, new ArrayList<Long>());
//
// // Test for structural equality (with method equals), not reference equality !
// assertTrue("The newFriends list is not equal to the one given to the constructor",
// newFriends.equals(nb.getNewFriends()));
// }
//
// @Test
// public void testGetRemovedFriendsIds() {
//
// List<Long> removedIds = new ArrayList<Long>();
// removedIds.add((long) 4);
// removedIds.add((long) 54);
//
// NotificationBag nb =
// new NetworkNotificationBag(new ArrayList<Long>(), new ArrayList<Long>(), removedIds);
//
// // Test for structural equality (with method equals), not reference equality !
// assertTrue("The newFriends list is not equal to the one given to the constructor",
// removedIds.equals(nb.getRemovedFriendsIds()));
// }
//
// @Test
// public void testInvitingFriendsDefensiveCopy() {
//
// List<Long> invitersList = new ArrayList<Long>(Arrays.asList(Long.valueOf(1),Long.valueOf(2)));
//
//
// NotificationBag nb =
// new NetworkNotificationBag(invitersList, new ArrayList<Long>(), new ArrayList<Long>());
//
// invitersList.add(Long.valueOf(3));
//
// assertTrue("The constructor does not make a defensive copy of inviting users list.",
// invitersList.size() != nb.getInvitingUsers().size());
//
// Set<Long> getList = nb.getInvitingUsers();
// getList.add(Long.valueOf(4));
//
// assertTrue("The getter does not return a defensive copy of inviting users list.",
// getList.size() != nb.getInvitingUsers().size());
// }
//
// @Test
// public void testIRemovedFriendsDefensiveCopy() {
//
// List<Long> removedIds = new ArrayList<Long>();
// removedIds.add((long) 4);
// removedIds.add((long) 54);
//
// NotificationBag nb =
// new NetworkNotificationBag(new ArrayList<Long>(), new ArrayList<Long>(), removedIds);
//
// removedIds.add((long) 5);
//
// assertTrue("The constructor does not make a defensive copy of removed ids list.",
// removedIds.size() != nb.getRemovedFriendsIds().size());
//
// Set<Long> getList = nb.getRemovedFriendsIds();
// getList.add((long) 10);
//
// assertTrue("The getter does not return a defensive copy of removed ids list.", getList.size() != nb
// .getRemovedFriendsIds().size());
// }
//
// @Test
// public void testNewFriendsDefensiveCopy() {
//
// List<Long> friendsList = new ArrayList<Long>(Arrays.asList(Long.valueOf(1),Long.valueOf(2))) ;
//
//
// NotificationBag nb =
// new NetworkNotificationBag(new ArrayList<Long>(), friendsList, new ArrayList<Long>());
//
// friendsList.add(Long.valueOf(3));
//
// assertTrue("The constructor does not make a defensive copy of new friends list.",
// friendsList.size() != nb.getNewFriends().size());
//
// Set<Long> getList = nb.getNewFriends();
// getList.add(Long.valueOf(4));
//
// assertTrue("The getter does not return a defensive copy of new friends list.", getList.size() != nb
// .getNewFriends().size());
// }
// }