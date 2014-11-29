package ch.epfl.smartmap.test.cache;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ch.epfl.smartmap.cache.DefaultFilter;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.PublicEvent;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.database.DatabaseHelper;

/**
 * Tests for the DatabaseHelper class
 * 
 * @author ritterni
 */
public class DatabaseHelperTest extends AndroidTestCase {

    private final String name = "test name";
    private Friend a = new Friend(1234, "qwertz uiop");
    private Friend b = new Friend(0, "hcjkehfkl");
    private Friend c = new Friend(9909, "Abc Def");
    private final PublicEvent event = new PublicEvent("A new event", 1234, "qwertz uiop",
        new GregorianCalendar(), new GregorianCalendar(), new Location("SmartMapProvider"));
    private final PublicEvent event2 = new PublicEvent("Another new event", 4523, "abababab",
        new GregorianCalendar(), new GregorianCalendar(), new Location("SmartMapProvider"));
    private DatabaseHelper dbh;
    private DefaultFilter filter;
    private DefaultFilter filter2;
    private Location loc = new Location("");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dbh = DatabaseHelper.initialize(new RenamingDelegatingContext(this.getContext(), "test_"));
        // to avoid erasing the actual database

        filter = new DefaultFilter(name);
        filter.addUser(a.getId());
        filter.addUser(b.getId());
        filter.addUser(c.getId());

        filter2 = new DefaultFilter(name);
        filter.addUser(b.getId());
        filter.addUser(c.getId());

        event.setID(123123);
        event2.setID(456789);

        loc.setLatitude(12.34);
        loc.setLatitude(3.45);
        event.setLocation(loc);

        dbh.clearAll();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbh.clearAll();
    }

    @Test
    public void testAddEvent() {
        dbh.addEvent(event);
        assertTrue(dbh.getEvent(event.getId()).getCreatorName().equals(event.getCreatorName())
            && (dbh.getEvent(event.getId()).getStartDate().get(GregorianCalendar.MINUTE) == event
                .getStartDate().get(GregorianCalendar.MINUTE))
            && (dbh.getEvent(event.getId()).getLocation().getLatitude() == event.getLocation().getLatitude()));
    }

    @Test
    public void testAddFilter() {
        long id = dbh.addFilter(filter);
        assertTrue(dbh.getFilter(id).getListName().equals(filter.getListName())
            && dbh.getFilter(id).getList().contains(b.getId()));
    }

    @Test
    public void testAddUser() {
        dbh.addUser(a);
        a.setName(name);
        // testing that adding a user with the same id erases the first one
        dbh.addUser(a);
        assertTrue((dbh.getFriend(a.getId()).getId() == a.getId())
            && dbh.getFriend(a.getId()).getName().equals(a.getName())
            && dbh.getFriend(a.getId()).getNumber().equals(a.getNumber())
            && dbh.getFriend(a.getId()).getEmail().equals(a.getEmail())
            && dbh.getFriend(a.getId()).getLocationString().equals(a.getLocationString())
            && (dbh.getFriend(a.getId()).getLocation().getLongitude() == a.getLocation().getLongitude())
            && (dbh.getFriend(a.getId()).getLocation().getLatitude() == a.getLocation().getLatitude())
            && (dbh.getAllFriends().size() == 1));
    }

    @Test
    public void testDeleteEvent() {
        dbh.addEvent(event);
        dbh.addEvent(event2);
        dbh.deleteEvent(event.getId());
        assertTrue((dbh.getAllEvents().size() == 1) && (dbh.getAllEvents().get(0).getId() == event2.getId()));
    }

    @Test
    public void testDeleteFilter() {
        dbh.addFilter(filter);
        dbh.deleteFilter(filter.getId());
        assertTrue(dbh.getAllFilters().isEmpty());
    }

    @Test
    public void testDeleteUser() {
        dbh.addUser(a);
        dbh.addUser(b);
        dbh.addUser(c);
        dbh.deleteUser(b.getId());
        List<User> list = dbh.getAllFriends();
        assertTrue((list.size() == 2));
    }

    @Test
    public void testGetAllEvents() {
        dbh.addEvent(event);
        dbh.addEvent(event2);
        assertTrue(dbh.getAllEvents().size() == 2);
    }

    @Test
    public void testGetAllFilters() {
        dbh.addFilter(filter);
        dbh.addFilter(filter2);
        assertTrue(dbh.getAllFilters().size() == 2);
    }

    @Test
    public void testgetAllFriends() {
        dbh.addUser(a);
        dbh.addUser(b);
        dbh.addUser(c);
        List<User> list = dbh.getAllFriends();
        assertTrue((list.size() == 3) && (dbh.getFriend(c.getId()).getId() == c.getId()));
    }

    @Test
    public void testUpdateEvent() {
        dbh.addEvent(event);
        event.setName(name);
        long rows = dbh.updateEvent(event);
        assertTrue(dbh.getEvent(event.getId()).getName().equals(name) && (rows == 1));
    }

    public void testUpdateUser() {
        a.setEmail("test email");
        dbh.addUser(a);
        dbh.addUser(b);
        int rows = dbh.updateFriend(new Friend(a.getId(), c.getName()));
        assertTrue(dbh.getFriend(a.getId()).getName().equals(c.getName())
            && dbh.getFriend(a.getId()).getEmail().equals("test email") && (rows == 1));
    }
}
