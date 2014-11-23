package ch.epfl.smartmap.test.severcom;

import junit.framework.TestCase;

import org.junit.Test;

import ch.epfl.smartmap.servercom.NoSuchFormatException;
import ch.epfl.smartmap.servercom.SmartMapParserFactory;

public class SmartMapParserFactoryTest extends TestCase {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String TEXT_CONTENT_TYPE = "text/plain";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testSupportedContentType() throws NoSuchFormatException {
        SmartMapParserFactory.parserForContentType(JSON_CONTENT_TYPE);
    }

    @Test
    public void testUnsupportedContentType() {
        try {
            SmartMapParserFactory.parserForContentType(TEXT_CONTENT_TYPE);
            fail("Parser for unsupported format");
        } catch (NoSuchFormatException e) {
            // success
        }

    }

}
