package ch.epfl.smartmap.test.severcom;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;

public class ParseFriendsMalformedJSONParsingTest extends TestCase {

	private static final String FRIEND_LIST_JSON = "{\n" + " \"list\" : [\n"
			+ "{\n" + " \"id\" : \"13\", \n" + " \"name\" : \"Georges\" \n"
			+ "},\n" + "{\n" + " \"id\" : \"18\", \n"
			+ " \"name\" : \"Alice\" \n" + "}\n" + "  ]\n" + "}\n";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void ignoredTestParseFriendsWrongEmail() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		JSONObject jsonFriend = (JSONObject) jsonArray.get(0);
		jsonFriend.put("email", "wrong email");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed wrong email");
		} catch (SmartMapParseException e) {
			// success
		}

	}

	@Test
	public void ignoredTestParseFriendsWrongPhoneNumber() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		JSONObject jsonFriend = (JSONObject) jsonArray.get(0);
		jsonFriend.put("phoneNumber", "1");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed wrong phonen number");
		} catch (SmartMapParseException e) {
			// success
		}

	}

	@Test
	public void testParseFriendsEmptyJson() {
		SmartMapParser parser = new JsonSmartMapParser();
		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed empty Json");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseFriendsEmptyName() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		JSONObject jsonFriend = (JSONObject) jsonArray.get(0);
		jsonFriend.put("name", "");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed empty name");
		} catch (SmartMapParseException e) {
			// success
		}

	}

	@Test
	public void testParseFriendsMissingField() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		jsonObject.remove("list");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(jsonObject.toString(), "list");
			fail("missing field : list");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseFriendsTooLongName() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		JSONObject jsonFriend = (JSONObject) jsonArray.get(0);
		jsonFriend
		.put("name",
				"righipwrgpwggvprwrgbvwripgoibfveigbhpàqiewhgpéewfqqpoghvnhpwghlkbherihprhgnpownagpéowahgpofnbghpràahwrpohgpàaroghpoàwhagàfwhségohrwèhgwrèhg");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed too long name");
		} catch (SmartMapParseException e) {
			// success
		}

	}

	@Test
	public void testParseFriendsWrongId() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		JSONObject jsonFriend = (JSONObject) jsonArray.get(0);
		jsonFriend.put("id", -3);
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed wrong id");
		} catch (SmartMapParseException e) {
			// success
		}

	}

	@Test
	public void testParseFriendsWrongLatitude() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		JSONObject jsonFriend = (JSONObject) jsonArray.get(0);
		jsonFriend.put("latitude", 200);
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed wrong latitude");
		} catch (SmartMapParseException e) {
			// success
		}

	}

	@Test
	public void testParseFriendsWrongLongitude() throws JSONException {
		JSONObject jsonObject = new JSONObject(FRIEND_LIST_JSON);
		JSONArray jsonArray = jsonObject.getJSONArray("list");
		JSONObject jsonFriend = (JSONObject) jsonArray.get(0);
		jsonFriend.put("longitude", 200);
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseFriends(new JSONObject().toString(), "list");
			fail("parsed wrong longitude");
		} catch (SmartMapParseException e) {
			// success
		}

	}

}