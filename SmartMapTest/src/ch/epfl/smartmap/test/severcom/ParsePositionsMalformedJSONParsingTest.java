package ch.epfl.smartmap.test.severcom;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;

/**
 * Tests whether the app correctly handles malformed JSON
 * 
 * @author marion-S
 */
public class ParsePositionsMalformedJSONParsingTest extends TestCase {

    private static final String POSITIONS_LIST_JSON = "{\n" + " \"positions\" : [\n" + "{\n" + " \"id\" : \"13\", \n"
        + " \"latitude\" : \"20.03\", \n" + " \"longitude\" : \"26.85\" \n" + "},\n" + "{\n" + " \"id\" : \"18\", \n"
        + " \"latitude\" : \"40.0\", \n" + " \"longitude\" : \"3.0\" \n" + "}\n" + "  ]\n" + "}\n";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @Test
    public void testParsePositionsEmptyJson() {
        SmartMapParser parser = new JsonSmartMapParser();
        try {
            parser.parsePositions(new JSONObject().toString());
            fail("parsed empty Json");
        } catch (SmartMapParseException e) {
            // success
        }
    }

    // @Test
    // public void testParsePositionsMissingField() throws JSONException {
    // JSONObject jsonObject = new JSONObject(POSITIONS_LIST_JSON);
    // jsonObject.remove("positions");
    // SmartMapParser parser = new JsonSmartMapParser();
    //
    // try {
    // parser.parseFriends(jsonObject.toString());
    // fail("missing positions");
    // } catch (SmartMapParseException e) {
    // // success
    // }
    // }

    @Test
    public void testParsePositionsWrongId() throws JSONException {
        JSONObject jsonObject = new JSONObject(POSITIONS_LIST_JSON);
        JSONArray jsonArray = jsonObject.getJSONArray("positions");
        JSONObject jsonPosition = (JSONObject) jsonArray.get(0);
        jsonPosition.put("id", -1);
        SmartMapParser parser = new JsonSmartMapParser();

        try {
            parser.parsePositions(jsonObject.toString());
            fail("parsed wrong id");
        } catch (SmartMapParseException e) {
            // success
        }

    }

    @Test
    public void testParsePositionsWrongLatitude() throws JSONException {
        JSONObject jsonObject = new JSONObject(POSITIONS_LIST_JSON);
        JSONArray jsonArray = jsonObject.getJSONArray("positions");
        JSONObject jsonPosition = (JSONObject) jsonArray.get(0);
        jsonPosition.put("latitude", -189);
        SmartMapParser parser = new JsonSmartMapParser();

        try {
            parser.parsePositions(jsonObject.toString());
            fail("parsed wrong latitude");
        } catch (SmartMapParseException e) {
            // success
        }

    }

    @Test
    public void testParsePositionsWrongLongitude() throws JSONException {
        JSONObject jsonObject = new JSONObject(POSITIONS_LIST_JSON);
        JSONArray jsonArray = jsonObject.getJSONArray("positions");
        JSONObject jsonPosition = (JSONObject) jsonArray.get(0);
        jsonPosition.put("longitude", -189);
        SmartMapParser parser = new JsonSmartMapParser();

        try {
            parser.parsePositions(jsonObject.toString());
            fail("parsed wrong longitude");
        } catch (SmartMapParseException e) {
            // success
        }

    }

}
