package ch.epfl.smartmap.test.severcom;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import ch.epfl.smartmap.servercom.JsonSmartMapParser;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import ch.epfl.smartmap.servercom.SmartMapParseException;
import ch.epfl.smartmap.servercom.SmartMapParser;
import junit.framework.TestCase;

public class CheckServerErrorMalformedJSONParsingTest extends TestCase {

    private static final String SERVER_STATUS_JSON = "{\n" + " \"status\" : \"Ok\", \n"
        + " \"message\" : \"Success!\" \n" + "}\n";

    private List<String> serverStatusJsonFields;

    protected void setUp() throws Exception {
        super.setUp();

        serverStatusJsonFields = new ArrayList<String>();
        serverStatusJsonFields.add("status");
        serverStatusJsonFields.add("message");
    }

    @Test
    public void testCheckServerErrorEmptyJson() throws SmartMapClientException {
        SmartMapParser parser = new JsonSmartMapParser();
        try {
            parser.checkServerError(new JSONObject().toString());
            fail("parsed empty Json");
        } catch (SmartMapParseException e) {
            // success
        }
    }

    @Test
    public void testCheckServerErrorMissingField() throws JSONException, SmartMapClientException {
        for (String field : serverStatusJsonFields) {
            JSONObject jsonObject = new JSONObject(SERVER_STATUS_JSON);
            jsonObject.remove(field);
            SmartMapParser parser = new JsonSmartMapParser();

            try {
                parser.checkServerError(jsonObject.toString());
                fail("missing " + field);
            } catch (SmartMapParseException e) {
                // success
            }

        }
    }

}
