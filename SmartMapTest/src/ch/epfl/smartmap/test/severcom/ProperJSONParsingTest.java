package ch.epfl.smartmap.test.severcom;


import java.util.List;
import java.util.Map;

import org.junit.Test;


import android.annotation.SuppressLint;
import android.location.Location;


import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;

import junit.framework.TestCase;

/** Tests whether the app correctly handles proper JSON
 * 
 *  @author marion-S
 **/
@SuppressLint("UseSparseArrays")
public class ProperJSONParsingTest extends TestCase{
	
	private static final String PROPER_FRIEND_JSON = "{\n"
			+ " \"id\" : \"13\", \n"
			+ " \"name\" : \"Georges\", \n"
			+ " \"email\" : \"georges@gmail.com\", \n"
			+ " \"latitude\" : \"20.03\", \n" 
			+ " \"longitude\" : \"26.85\", \n" 
			+ " \"phoneNumber\" : \"0782678654\" \n"
			+ "}\n";
	
	private static final String PROPER_SUCCESS_STATUS_JSON = "{\n"
			+ " \"status\" : \"Ok\", \n"
			+ " \"message\" : \"Success!\" \n"
			+ "}\n";
	
	private static final String PROPER_ERROR_STATUS_JSON = "{\n"
			+ " \"status\" : \"error\", \n"
			+ " \"message\" : \"wrong parameters\" \n"
			+ "}\n";
	
	private static final String PROPER_FRIEND_LIST_JSON = "{\n"
			+ " \"list\" : [\n"
			+ "{\n"
			+ " \"id\" : \"13\", \n"
			+ " \"name\" : \"Georges\" \n" 
			+ "},\n"
			+ "{\n"
			+ " \"id\" : \"18\", \n"
			+ " \"name\" : \"Alice\" \n" 
			+ "}\n"
			+ "  ]\n"
			+ "}\n";
	
	private static final String PROPER_POSITIONS_LIST_JSON= "{\n"
			+ " \"positions\" : [\n"
			+ "{\n"
			+ " \"id\" : \"13\", \n"
			+ " \"latitude\" : \"20.03\", \n" 
			+ " \"longitude\" : \"26.85\" \n" 
			+ "},\n"
			+ "{\n"
			+ " \"id\" : \"18\", \n"
			+ " \"latitude\" : \"40.0\", \n"
			+ " \"longitude\" : \"3.0\" \n" 
			+ "}\n"
			+ "  ]\n"
			+ "}\n";
	
	private static final String PROPER_FRIEND_EMPTY_LIST_JSON = "{\n"
			+ " \"list\" : [\n"
			+ "  ]\n"
			+ "}\n";
	
	private static final String PROPER_POSITIONS_EMPTY_LIST_JSON= "{\n"
			+ " \"positions\" : [\n"
			+ "  ]\n"
			+ "}\n";
	
	private Location location1=new Location("SmartMapServers");;
	private Location location2=new Location("SmartMapServers");;
	
	   protected void setUp() throws Exception {
	        super.setUp();
	       
	        location1.setLatitude(20.03);
	        location1.setLongitude(26.85);
	      
	        location2.setLatitude(40.0);
	        location2.setLongitude(3.0);
 
	    }
	   
	   @Test
	   public void testParseFriend() throws SmartMapParseException{
		   SmartMapParser parser = new JsonSmartMapParser();
		   User friend = parser.parseFriend(PROPER_FRIEND_JSON);
		   
		   assertEquals("Friend's id does not match", 13, friend.getID());
		   assertEquals("Friend's name does not match", "Georges", friend.getName());
		   assertEquals("Friend's email does not match", "georges@gmail.com", friend.getEmail());
		   assertEquals("Friend's phone number does not match", "0782678654", friend.getNumber());
		   //FIXME
		   //assertEquals("Friend's latitude or longitude does not match", new LatLng(20.03, 26.85), friend.getLatLng());
	   }
	 
	 @Test
	 public void testCheckServerErrorWhenNoError() throws SmartMapParseException,SmartMapClientException{
		 SmartMapParser parser = new JsonSmartMapParser();
		 parser.checkServerError(PROPER_SUCCESS_STATUS_JSON);
	 }
	 
	 @Test
	 public void testCheckServerErrorWhenError() throws SmartMapParseException{
		 SmartMapParser parser = new JsonSmartMapParser();
		 try{
		 parser.checkServerError(PROPER_ERROR_STATUS_JSON);
		 fail("Did not throw a SmartMapClientException whereas the server got an error");
		 }catch(SmartMapClientException e){
			 //success
		 }catch(Exception e){
			 e.printStackTrace();
			 fail("Wrong exception thrown");
		 }
	 }
	 
	 @Test
	 public void testParseFriends() throws SmartMapParseException{
		 SmartMapParser parser = new JsonSmartMapParser();
		 List<User> listFriends = parser.parseFriends(PROPER_FRIEND_LIST_JSON);
		 assertEquals("First friend's id does not match", 13, listFriends.get(0).getID());
		 assertEquals("First friend's name does not match", "Georges", listFriends.get(0).getName());
		 assertEquals("Second friend's id does not match", 18, listFriends.get(1).getID());
		 assertEquals("Second friend's name does not match", "Alice", listFriends.get(1).getName());
	 }
	 
	 @Test
	 public void testParsePositions() throws SmartMapParseException{
		 SmartMapParser parser = new JsonSmartMapParser();
		 Map<Long,Location> positions=parser.parsePositions(PROPER_POSITIONS_LIST_JSON);
		 assertTrue("Did not parse the first position", positions.containsKey((long)13));
		 assertTrue("Did not parse the second position", positions.containsKey((long)18));
		 assertEquals("First location's latitude does not match",location1.getLatitude(),positions.get((long)13).getLatitude());
		 assertEquals("First location's longitude does not match",location1.getLongitude(),positions.get((long)13).getLongitude());
		 assertEquals("Second location's latitude does not match",location2.getLatitude(),positions.get((long)18).getLatitude());
		 assertEquals("Second location's longitude does not match",location2.getLongitude(),positions.get((long)18).getLongitude());

	 }
	 
	 @Test
	 public void testParseFriendsWhenEmptyList() throws SmartMapParseException{
		 SmartMapParser parser = new JsonSmartMapParser();
		 List<User> friends=parser.parseFriends(PROPER_FRIEND_EMPTY_LIST_JSON);
		 assertTrue("Did not parsed empty friends list correctly", friends.isEmpty());
	 }
	 
	 @Test
	 public void testParsePositionsWhenEmptyList() throws SmartMapParseException{
		 SmartMapParser parser = new JsonSmartMapParser();
		 Map<Long,Location> positions=parser.parsePositions(PROPER_POSITIONS_EMPTY_LIST_JSON);
		 assertTrue("Did not parsed empty positions list correctly", positions.isEmpty());
	 }

}
