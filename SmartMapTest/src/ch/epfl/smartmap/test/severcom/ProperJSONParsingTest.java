package ch.epfl.smartmap.test.severcom;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

import android.annotation.SuppressLint;
import android.location.Location;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;

/**
 * Tests whether the app correctly handles proper JSON
 * 
 * @author marion-S
 **/
@SuppressLint("UseSparseArrays")
public class ProperJSONParsingTest extends TestCase {

    private static final String PROPER_FRIEND_JSON = "{\n" + " \"id\" : \"13\", \n" + " \"name\" : \"Georges\", \n"
        + " \"email\" : \"georges@gmail.com\", \n" + " \"latitude\" : \"20.03\", \n" + " \"longitude\" : \"26.85\", \n"
        + " \"phoneNumber\" : \"0782678654\" \n" + "}\n";

    private static final String PROPER_SUCCESS_STATUS_JSON = "{\n" + " \"status\" : \"Ok\", \n"
        + " \"message\" : \"Success!\" \n" + "}\n";

    private static final String PROPER_ERROR_STATUS_JSON = "{\n" + " \"status\" : \"error\", \n"
        + " \"message\" : \"wrong parameters\" \n" + "}\n";

    private static final String PROPER_FRIEND_LIST_JSON = "{\n" + " \"list\" : [\n" + "{\n" + " \"id\" : \"13\", \n"
        + " \"name\" : \"Georges\" \n" + "},\n" + "{\n" + 
        " \"id\" : \"18\", \n" + " \"name\" : \"Alice\" \n" + "}\n"
        + "  ]\n" + "}\n";

    private static final String PROPER_POSITIONS_LIST_JSON = "{\n" + " \"positions\" : [\n" + "{\n"
        + " \"id\" : \"13\", \n" + " \"latitude\" : \"20.03\", \n" + " \"longitude\" : \"26.85\", \n"
        + "\"lastUpdate\": \"2014-11-12 23:54:22\"" + "},\n" + "{\n"
        + " \"id\" : \"18\", \n" + " \"latitude\" : \"40.0\", \n" + " \"longitude\" : \"3.0\", \n"
        + "\"lastUpdate\": \"2014-10-23 05:07:54\"" + "}\n" + "  ]\n"
        + "}\n";

    private static final String PROPER_FRIEND_EMPTY_LIST_JSON = "{\n" + " \"list\" : [\n" + "  ]\n" + "}\n";

    private static final String PROPER_POSITIONS_EMPTY_LIST_JSON = "{\n" + " \"positions\" : [\n" + "  ]\n" + "}\n";

    private Location location1 = new Location("SmartMapServers");
    private Location location2 = new Location("SmartMapServers");
    
    private GregorianCalendar date1 = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));
    private GregorianCalendar date2 = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        location1.setLatitude(20.03);
        location1.setLongitude(26.85);

        location2.setLatitude(40.0);
        location2.setLongitude(3.0);
        
        date1.set(2014, 10, 12, 23, 54, 22);
        date2.set(2014, 9, 23, 5, 7, 54);

    }

    @Test
    public void testParseFriend() throws SmartMapParseException {
        SmartMapParser parser = new JsonSmartMapParser();
        User friend = parser.parseFriend(PROPER_FRIEND_JSON);

        assertEquals("Friend's id does not match", 13, friend.getID());
        assertEquals("Friend's name does not match", "Georges", friend.getName());
        assertEquals("Friend's email does not match", "georges@gmail.com", friend.getEmail());
        assertEquals("Friend's phone number does not match", "0782678654", friend.getNumber());
        assertEquals("Friend's latitude does not match", 20.03, friend.getLatLng().latitude);
        assertEquals("Friend's longitude does not match", 26.85, friend.getLatLng().longitude);
    }

    @Test
    public void testCheckServerErrorWhenNoError() throws SmartMapParseException, SmartMapClientException {
        SmartMapParser parser = new JsonSmartMapParser();
        parser.checkServerError(PROPER_SUCCESS_STATUS_JSON);
    }

    @Test
    public void testCheckServerErrorWhenError() throws SmartMapParseException {
        SmartMapParser parser = new JsonSmartMapParser();
        try {
            parser.checkServerError(PROPER_ERROR_STATUS_JSON);
            fail("Did not throw a SmartMapClientException whereas the server got an error");
        } catch (SmartMapClientException e) {
            // success
        } catch (Exception e) {
            e.printStackTrace();
            fail("Wrong exception thrown");
        }
    }

    @Test
    public void testParseFriends() throws SmartMapParseException {
        SmartMapParser parser = new JsonSmartMapParser();
        List<User> listFriends = parser.parseFriends(PROPER_FRIEND_LIST_JSON, "list");
        assertEquals("First friend's id does not match", 13, listFriends.get(0).getID());
        assertEquals("First friend's name does not match", "Georges", listFriends.get(0).getName());
        assertEquals("Second friend's id does not match", 18, listFriends.get(1).getID());
        assertEquals("Second friend's name does not match", "Alice", listFriends.get(1).getName());
    }

    @Test
    public void testParseFriendsWhenEmptyList() throws SmartMapParseException {
        SmartMapParser parser = new JsonSmartMapParser();
        List<User> friends = parser.parseFriends(PROPER_FRIEND_EMPTY_LIST_JSON, "list");
        assertTrue("Did not parsed empty friends list correctly", friends.isEmpty());
    }

    @Test
    public void testParsePositions() throws SmartMapParseException {
        SmartMapParser parser = new JsonSmartMapParser();
        List<User> users = parser.parsePositions(PROPER_POSITIONS_LIST_JSON);
        assertTrue("Did not parse the two positions", users.size() == 2);
        assertEquals("First location's latitude does not match", location1.getLatitude(), users.get(0)
            .getLocation().getLatitude());
        assertEquals("First location's longitude does not match", location1.getLongitude(), users.get(0)
            .getLocation().getLongitude());
        // GMT+01:00 conversion changes a few milliseconds in GregorainCalendar, so we cannot test for
        // exact equality...
        assertTrue("Last seen of first user does not match", Math.abs(date1.getTimeInMillis() - 
            users.get(0).getLastSeen().getTimeInMillis()) < 1000);
        assertEquals("Second location's latitude does not match", location2.getLatitude(), users.get(1)
            .getLocation().getLatitude());
        assertEquals("Second location's longitude does not match", location2.getLongitude(), users.get(1)
            .getLocation().getLongitude());
        assertTrue("Last seen of second user does not match", Math.abs(date2.getTimeInMillis() - 
            users.get(1).getLastSeen().getTimeInMillis()) < 1000);

    }

    @Test
    public void testParsePositionsWhenEmptyList() throws SmartMapParseException {
        SmartMapParser parser = new JsonSmartMapParser();
        List<User> users = parser.parsePositions(PROPER_POSITIONS_EMPTY_LIST_JSON);
        assertTrue("Did not parsed empty positions list correctly", users.isEmpty());
    }

}
