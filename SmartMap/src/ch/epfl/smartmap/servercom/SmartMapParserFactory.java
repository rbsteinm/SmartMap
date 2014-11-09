package ch.epfl.smartmap.servercom;

/**
 * This factory class knows about supported formats.
 * 
 * @author marion-S
 * 
 * @author Pamoi (code reviewed : 9.11.2014)
 */
public class SmartMapParserFactory {

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
	public static SmartMapParser parserForContentType(String contentType)
			throws NoSuchFormatException {

		if (contentType.equals("application/json")) {
			return new JsonSmartMapParser();

		} else {
			throw new NoSuchFormatException();
		}
	}

}
