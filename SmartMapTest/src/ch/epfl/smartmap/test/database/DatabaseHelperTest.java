package ch.epfl.smartmap.test.database;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.background.SettingsManager;
import ch.epfl.smartmap.cache.EventContainer;
import ch.epfl.smartmap.cache.FilterContainer;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.cache.InvitationContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.database.DatabaseHelper;

/**
 * Tests for the DatabaseHelper class
 * 
 * @author ritterni
 */
public class DatabaseHelperTest extends AndroidTestCase {

    private final String name = "test name";
    private final UserContainer a = new UserContainer(1, "dafdyx", "898909808", "toto@toto.to", new Location(
        "testProvider"), "Ecublens", null, User.BlockStatus.NOT_SET, User.STRANGER);
    private final UserContainer b = new UserContainer(678, "tfxhthsfe", "65423", "tata@toto.to",
        new Location("testProvider"), "Lausanne", null, User.BlockStatus.BLOCKED, User.FRIEND);
    private final UserContainer c = new UserContainer(54554, "hcjkehfkl", "48325", "titi@toto.to",
        new Location("testProvider"), "Geneva", null, User.BlockStatus.UNBLOCKED, User.FRIEND);
    private final EventContainer event = new EventContainer(123, name, a, "description",
        new GregorianCalendar(), new GregorianCalendar(), new Location("testprovider"), "Lausanne",
        new HashSet<Long>());
    private final EventContainer event2 = new EventContainer(1277, "name 2", b, "description",
        new GregorianCalendar(), new GregorianCalendar(), new Location("testprovider"), "Lausanne",
        new HashSet<Long>());
    private final InvitationContainer invitA = new InvitationContainer(2323, a, null, Invitation.UNREAD,
        new GregorianCalendar().getTimeInMillis(), Invitation.FRIEND_INVITATION);
    private final InvitationContainer invitB = new InvitationContainer(2323, null, event,
        Invitation.ACCEPTED, new GregorianCalendar().getTimeInMillis(), Invitation.EVENT_INVITATION);
    private DatabaseHelper dbh;
    private FilterContainer filter;
    private FilterContainer filter2;
    private final Location loc = new Location("");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // need ID from settings manager to initialize database
        SettingsManager manager = Mockito.mock(SettingsManager.class);
        Mockito.when(manager.getUserId()).thenReturn((long) 8);
        ServiceContainer.setSettingsManager(manager);

        // to avoid erasing the actual database
        dbh = new DatabaseHelper(new RenamingDelegatingContext(this.getContext(), "test_"));
        dbh.clearAll();

        Set<Long> list1 = new HashSet<Long>();
        list1.add(a.getId());
        list1.add(b.getId());
        list1.add(c.getId());
        filter = new FilterContainer(3, name, list1, false);

        Set<Long> list2 = new HashSet<Long>();
        list1.add(a.getId());
        list1.add(c.getId());
        filter2 = new FilterContainer(21, "name 2", list2, true);

        loc.setLatitude(12.34);
        loc.setLongitude(3.45);
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
        assertTrue((dbh.getEvent(event.getId()).getStartDate().get(GregorianCalendar.MINUTE) == event
            .getStartDate().get(GregorianCalendar.MINUTE))
            && (dbh.getEvent(event.getId()).getLocation().getLatitude() == event.getLocation().getLatitude()));
    }

    @Test
    public void testAddFilter() {
        long id = dbh.addFilter(filter);
        assertTrue(dbh.getFilter(id).getName().equals(filter.getName())
            && dbh.getFilter(id).getIds().contains(b.getId()));
    }

    @Test
    public void testAddUser() {
        dbh.addUser(a);
        // testing that adding a user with the same id erases the first one
        dbh.addUser(new UserContainer(1, "other name", "898909808", "toto@toto.to", new Location(
            "testProvider"), "Ecublens", null, User.BlockStatus.NOT_SET, User.STRANGER));
        assertTrue((dbh.getUser(a.getId()).getId() == a.getId())
            && dbh.getUser(a.getId()).getName().equals("other name")
            && dbh.getUser(a.getId()).getPhoneNumber().equals(a.getPhoneNumber())
            && dbh.getUser(a.getId()).getEmail().equals(a.getEmail())
            && dbh.getUser(a.getId()).getLocationString().equals(a.getLocationString())
            && (dbh.getUser(a.getId()).getLocation().getLongitude() == a.getLocation().getLongitude())
            && (dbh.getUser(a.getId()).getLocation().getLatitude() == a.getLocation().getLatitude())
            && (dbh.getAllUsers().size() == 1));
    }

    @Test
    public void testDeleteEvent() {
        dbh.addEvent(event);
        dbh.addEvent(event2);
        dbh.deleteEvent(event.getId());
        assertTrue((dbh.getAllEvents().size() == 1)
            && (new ArrayList<EventContainer>(dbh.getAllEvents()).get(0).getId() == event2.getId()));
    }

    @Test
    public void testDeleteFilter() {
        dbh.addFilter(filter);
        dbh.deleteFilter(filter.getId());
        // accounting for default filter #1
        assertTrue(dbh.getAllFilters().size() == 1);
    }

    @Test
    public void testDeleteUser() {
        dbh.addUser(a);
        dbh.addUser(b);
        dbh.addUser(c);
        dbh.deleteUser(b.getId());
        Set<UserContainer> set = dbh.getAllUsers();
        assertTrue((set.size() == 2));
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
        // accounting for default filter #1
        assertTrue(dbh.getAllFilters().size() == 3);
    }

    @Test
    public void testgetAllFriends() {
        dbh.addUser(a);
        dbh.addUser(b);
        dbh.addUser(c);
        Set<UserContainer> set = dbh.getAllUsers();
        assertTrue((set.size() == 3) && (dbh.getUser(c.getId()).getId() == c.getId()));
    }

    @Test
    public void testUpdateEvent() {
        dbh.addEvent(event);
        event.setName(name);
        long rows = dbh.updateEvent(event);
        assertTrue(dbh.getEvent(event.getId()).getName().equals(name) && (rows == 1));
    }

    @Test
    public void testUpdateUser() {
        a.setEmail("test email");
        dbh.addUser(a);
        dbh.addUser(b);
        int rows =
            dbh.updateFriend(new UserContainer(a.getId(), c.getName(), "898909808", "test email",
                new Location("testProvider"), "Ecublens", null, User.BlockStatus.NOT_SET, User.STRANGER));
        assertTrue(dbh.getUser(a.getId()).getName().equals(c.getName())
            && dbh.getUser(a.getId()).getEmail().equals("test email") && (rows == 1));
    }
}