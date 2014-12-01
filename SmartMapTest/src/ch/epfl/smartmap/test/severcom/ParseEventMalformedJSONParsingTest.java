package ch.epfl.smartmap.test.severcom;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;

public class ParseEventMalformedJSONParsingTest extends TestCase {

	private static final String EVENT_JSON = "{\n" + "\"event\":" + "{\n"
			+ " \"id\" : \"13\", \n" + " \"creatorId\" : \"3\", \n"
			+ " \"startingDate\" : \"2014-10-23 05:07:54\", \n"
			+ " \"endingDate\" : \"2014-11-12 23:54:22\", \n"
			+ " \"longitude\" : \"26.85\", \n"
			+ " \"latitude\" : \"20.03\", \n" + " \"name\" : \"MyEvent\", \n"
			+ " \"description\" : \"description\", \n"
			+ " \"participants\" : [\n 3, 4, 1 \n] , \n"
			+ " \"positionName\" : \"Tokyo\" \n" + "}\n" + "}\n";

	private List<String> eventJsonFields;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		eventJsonFields = new ArrayList<String>();
		eventJsonFields.add("id");
		eventJsonFields.add("creatorId");
		eventJsonFields.add("startingDate");
		eventJsonFields.add("endingDate");
		eventJsonFields.add("name");
		eventJsonFields.add("longitude");
		eventJsonFields.add("latitude");
		eventJsonFields.add("positionName");
		eventJsonFields.add("description");
		eventJsonFields.add("participants");

	}

	@Test
	public void testParseEventEmptyJson() {
		SmartMapParser parser = new JsonSmartMapParser();
		try {
			parser.parseEvent(new JSONObject().toString());
			fail("parsed empty Json");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	public void testParseEventMissingField() throws JSONException {
		SmartMapParser parser = new JsonSmartMapParser();
		for (String field : eventJsonFields) {
			JSONObject jsonObject = new JSONObject(EVENT_JSON);
			JSONObject eventJson=jsonObject.getJSONObject("event");
			eventJson.remove(field);
			try {
				parser.parseEvent(eventJson.toString());
				fail("missing field : " + field);
			} catch (SmartMapParseException e) {
				// success
			}
		}

	}

	public void testParseEventWrongEventId() throws JSONException {
		SmartMapParser parser = new JsonSmartMapParser();
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("id", -5);
		try {
			parser.parseEvent(eventJson.toString());
			fail("wrong event id");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	public void testParseEventWrongCreatorId() throws JSONException {
		SmartMapParser parser = new JsonSmartMapParser();
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("creatorId", -5);
		try {
			parser.parseEvent(eventJson.toString());
			fail("wrong creator id");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventWrongDateFormat() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("startingDate", "2014-11-23 23:23:34:23");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed wrong date");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventWrongDateMonth() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("startingDate", "2014-13-23 23:23:34");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed wrong date");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	public void testParseEventStartingDateAfterEnding() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("startingDate", "2014-11-13 23:23:34");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("starting date after ending date");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventWrongLatitude() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("latitude", 1000);
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed wrong latitude");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventWrongLongitude() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("longitude", 1000);
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed wrong longitude");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventEmptyEventName() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("name", "");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed empty event name");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventTooLongEventName() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson
		.put("name",
				"egrhgpiergbpwifbowiegforwgtoiedfbéwgfboéwagrfowéargforwgfowaugfowiegfowaifgoawéietgéwagprigfsgfgjkaegoirgorigéraoigpwrgoéwigaowigfvbrofgroivrhtoghroufgvborthgoéaiegéorifgoga");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed too long event name");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventEmptyPositionName() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson.put("positionName", "");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed empty position name");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventTooLongPositionName() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson
		.put("positionName",
				"egrhgpiergbpwifbowiegforwgtoiedfbéwgfboéwagrfowéargforwgfowaugfowiegfowaifgoawéietgéwagprigfsgfgjkaegoirgorigéraoigpwrgoéwigaowigfvbrofgroivrhtoghroufgvborthgoéaiegéorifgoga");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed too long position name");
		} catch (SmartMapParseException e) {
			// success
		}
	}

	@Test
	public void testParseEventTooLongDescription() throws JSONException {
		JSONObject jsonObject = new JSONObject(EVENT_JSON);
		JSONObject eventJson=jsonObject.getJSONObject("event");
		eventJson
		.put("description",
				"egrhgpiergbpwifbowiegforwgtoiedfbéwgfboéwagrfowéargforwgfowaugfowiegfowaifgoawéietgéwagprigfsgfgjkaegoirgorigéraoigpwrgoéwigaowigfvbrofgroivrhtoghroufgvborthgoéaiegéorifgogareaihfroeitghoireguodfgosuegrouergfoerughuoerhffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
		SmartMapParser parser = new JsonSmartMapParser();

		try {
			parser.parseEvent(eventJson.toString());
			fail("parsed too long description");
		} catch (SmartMapParseException e) {
			// success
		}
	}
}