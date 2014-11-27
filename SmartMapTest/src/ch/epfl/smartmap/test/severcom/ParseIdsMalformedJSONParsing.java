package ch.epfl.smartmap.test.severcom;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;

public class ParseIdsMalformedJSONParsing extends TestCase {

    private static final String IDS_LIST_JSON = "{\n" + " \"friends\" : [ 1, 2, 3 ]" + "}\n";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @Test
    public void testParseIdsEmptyJson() {
        SmartMapParser parser = new JsonSmartMapParser();
        try {
            parser.parseIds(new JSONObject().toString(), "friends");
            fail("parsed empty Json");
        } catch (SmartMapParseException e) {
            // success
        }
    }

    @Test
    public void testParseIdsMissingField() throws JSONException {
        JSONObject jsonObject = new JSONObject(IDS_LIST_JSON);
        jsonObject.remove("friends");
        SmartMapParser parser = new JsonSmartMapParser();

        try {
            parser.parseIds(jsonObject.toString(), "friends");
            fail("missing friends");
        } catch (SmartMapParseException e) {
            // success
        }
    }

    @Test
    public void testParseIdsWrongId() throws JSONException {
        JSONObject jsonObject = new JSONObject(IDS_LIST_JSON);
        JSONArray jsonArray = jsonObject.getJSONArray("friends");
        jsonArray.put(0, -1);
        SmartMapParser parser = new JsonSmartMapParser();

        try {
            parser.parseIds(jsonObject.toString(), "friends");
            fail("parsed wrong id");
        } catch (SmartMapParseException e) {
            // success
        }

    }
}
