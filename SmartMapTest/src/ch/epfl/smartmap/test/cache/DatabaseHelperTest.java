package ch.epfl.smartmap.test.cache;

import android.test.AndroidTestCase;

/**
 * Tests for the DatabaseHelper class
 * 
 * @author ritterni
 */
public class DatabaseHelperTest extends AndroidTestCase {

    // private final String name = "test name";
    // private final Friend a = new Friend(1234, "qwertz uiop");
    // private final Friend b = new Friend(0, "hcjkehfkl");
    // private final Friend c = new Friend(9909, "Abc Def");
    // private final PublicEvent event = new PublicEvent("A new event", 1234, "qwertz uiop",
    // new GregorianCalendar(), new GregorianCalendar(), new Location("SmartMapProvider"));
    // private final PublicEvent event2 = new PublicEvent("Another new event", 4523, "abababab",
    // new GregorianCalendar(), new GregorianCalendar(), new Location("SmartMapProvider"));
    // private final FriendInvitation invitA = new FriendInvitation(0, 8, "Otto Ritter", Invitation.UNREAD);
    // private final FriendInvitation invitB = new FriendInvitation(0, 2, "Robiche", Invitation.REFUSED);
    // private DatabaseHelper dbh;
    // private DefaultFilter filter;
    // private DefaultFilter filter2;
    // private final Location loc = new Location("");
    //
    // @Override
    // protected void setUp() throws Exception {
    // super.setUp();
    // dbh = DatabaseHelper.initialize(new RenamingDelegatingContext(this.getContext(), "test_"));
    // // to avoid erasing the actual database
    //
    // filter = new DefaultFilter(name);
    // filter.addUser(a.getID());
    // filter.addUser(b.getID());
    // filter.addUser(c.getID());
    //
    // filter2 = new DefaultFilter(name);
    // filter.addUser(b.getID());
    // filter.addUser(c.getID());
    //
    // event.setID(123123);
    // event2.setID(456789);
    //
    // loc.setLatitude(12.34);
    // loc.setLatitude(3.45);
    // event.setLocation(loc);
    //
    // dbh.clearAll();
    // }
    //
    // @Override
    // protected void tearDown() throws Exception {
    // super.tearDown();
    // dbh.clearAll();
    // }
    //
    // @Test
    // public void testAddEvent() {
    // dbh.addEvent(event);
    // assertTrue(dbh.getEvent(event.getID()).getCreatorName().equals(event.getCreatorName())
    // && (dbh.getEvent(event.getID()).getStartDate().get(GregorianCalendar.MINUTE) == event
    // .getStartDate().get(GregorianCalendar.MINUTE))
    // && (dbh.getEvent(event.getID()).getLocation().getLatitude() == event.getLocation().getLatitude()));
    // }
    //
    // @Test
    // public void testAddFilter() {
    // long id = dbh.addFilter(filter);
    // assertTrue(dbh.getFilter(id).getListName().equals(filter.getListName())
    // && dbh.getFilter(id).getList().contains(b.getId()));
    // }
    //
    // @Test
    // public void testAddUser() {
    // dbh.addUser(a);
    // a.setName(name);
    // // testing that adding a user with the same id erases the first one
    // dbh.addUser(a);
    // assertTrue((dbh.getUser(a.getID()).getID() == a.getID())
    // && dbh.getUser(a.getID()).getName().equals(a.getName())
    // && dbh.getUser(a.getID()).getNumber().equals(a.getNumber())
    // && dbh.getUser(a.getID()).getEmail().equals(a.getEmail())
    // && dbh.getUser(a.getID()).getLocationString().equals(a.getLocationString())
    // && (dbh.getUser(a.getID()).getLocation().getLongitude() == a.getLocation().getLongitude())
    // && (dbh.getUser(a.getID()).getLocation().getLatitude() == a.getLocation().getLatitude())
    // && (dbh.getAllFriends().size() == 1));
    // }
    //
    // @Test
    // public void testDeleteEvent() {
    // dbh.addEvent(event);
    // dbh.addEvent(event2);
    // dbh.deleteEvent(event.getID());
    // assertTrue((dbh.getAllEvents().size() == 1) && (dbh.getAllEvents().get(0).getID() == event2.getID()));
    // }
    //
    // @Test
    // public void testDeleteFilter() {
    // dbh.addFilter(filter);
    // dbh.deleteFilter(filter.getID());
    // assertTrue(dbh.getAllFilters().isEmpty());
    // }
    //
    // @Test
    // public void testDeleteUser() {
    // dbh.addUser(a);
    // dbh.addUser(b);
    // dbh.addUser(c);
    // dbh.deleteUser(b.getID());
    // List<User> list = dbh.getAllFriends();
    // assertTrue((list.size() == 2));
    // }
    //
    // @Test
    // public void testGetAllEvents() {
    // dbh.addEvent(event);
    // dbh.addEvent(event2);
    // assertTrue(dbh.getAllEvents().size() == 2);
    // }
    //
    // @Test
    // public void testGetAllFilters() {
    // dbh.addFilter(filter);
    // dbh.addFilter(filter2);
    // assertTrue(dbh.getAllFilters().size() == 2);
    // }
    //
    // @Test
    // public void testgetAllFriends() {
    // dbh.addUser(a);
    // dbh.addUser(b);
    // dbh.addUser(c);
    // List<User> list = dbh.getAllFriends();
    // assertTrue((list.size() == 3) && (dbh.getUser(c.getID()).getID() == c.getID()));
    // }
    //
    // @Test
    // public void testGetUnansweredFriendInvitations() {
    // dbh.addFriendInvitation(invitA);
    // dbh.addFriendInvitation(invitB);
    // List<FriendInvitation> list = dbh.getUnansweredFriendInvitations();
    // assertTrue((list.size() == 1) && (list.get(0).getUserId() == 8));
    // }
    //
    // @Test
    // public void testUpdateEvent() {
    // dbh.addEvent(event);
    // event.setName(name);
    // long rows = dbh.updateEvent(event);
    // assertTrue(dbh.getEvent(event.getID()).getName().equals(name) && (rows == 1));
    // }
    //
    // @Test
    // public void testUpdateUser() {
    // a.setEmail("test email");
    // dbh.addUser(a);
    // dbh.addUser(b);
    // int rows = dbh.updateUser(new Friend(a.getID(), c.getName()));
    // assertTrue(dbh.getUser(a.getID()).getName().equals(c.getName())
    // && dbh.getUser(a.getID()).getEmail().equals("test email") && (rows == 1));
    // }
}
