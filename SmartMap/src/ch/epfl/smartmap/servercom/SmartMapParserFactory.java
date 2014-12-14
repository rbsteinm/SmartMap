package ch.epfl.smartmap.servercom;

/**
 * This factory class knows about supported formats.
 * 
 * @author marion-S
 * @author Pamoi (code reviewed : 9.11.2014)
 */
public final class SmartMapParserFactory {

    public static final String JSON_CONTENT_TYPE = "application/json";

    /**
     * Constructor
     */
    private SmartMapParserFactory() {
        super();
    }

    /**
     * Obtains a parser for the given MIME type.
     * 
     * @param contentType
     *            The MIME type that the parser should understand, e.g.,
     *            "application/json"
     * @return A parser for the given contentType
     * @throws NoSuchFormatException
     *             If no known parser supports this content type
     */
    public static SmartMapParser parserForContentType(String contentType) throws NoSuchFormatException {

        if (contentType.equals(JSON_CONTENT_TYPE)) {
            return new JsonSmartMapParser();

        } else {
            throw new NoSuchFormatException();
        }
    }

}
