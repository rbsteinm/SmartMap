package ch.epfl.smartmap.test.severcom;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;

public class ParseEventListMalformedJSONParsingTest extends TestCase {

	private static final String EVENT_LIST_JSON = "{\n" + " \"events\" : [\n"
			+ "{\n" + " \"id\" : \"13\", \n" + " \"creatorId\" : \"3\", \n"
			+ " \"startingDate\" : \"2014-10-23 05:07:54\", \n"
			+ " \"endingDate\" : \"2014-11-12 23:54:22\", \n"
			+ " \"longitude\" : \"26.85\", \n"
			+ " \"latitude\" : \"20.03\", \n" + " \"name\" : \"MyEvent\", \n"
			+ " \"description\" : \"description\", \n"
			+ " \"participants\" : [\n 3, 4, 1 \n] , \n"
			+ " \"positionName\" : \"Tokyo\" \n" + "},\n" + "{\n"
			+ " \"id\" : \"11\", \n" + " \"creatorId\" : \"1\", \n"
			+ " \"startingDate\" : \"2015-01-02 06:10:54\", \n"
			+ " \"endingDate\" : \"2015-02-02 22:00:11\", \n"
			+ " \"latitude\" : \"40.0\", \n" + " \"longitude\" : \"3.0\", \n"
			+ " \"participants\" : [\n 1 \n] , \n"
			+ " \"name\" : \"YourEvent\", \n"
			+ " \"description\" : \"description2\", \n"
			+ " \"positionName\" : \"London\" \n" + "}\n" + "  ]\n" + "}\n";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testParseEventListEmptyJson() {
		SmartMapParser parser = new JsonSmartMapParser();
		try {
			parser.parseEventList(new JSONObject().toString());
			fail("parsed empty Json");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventListMissingField() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_LIST_JSON);
		jsonObject.remove("events");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEventList(jsonObject.toString());
			fail("missing field : events");
		} catch (SmartMapParseException e) {
			// success
		}
	}

}