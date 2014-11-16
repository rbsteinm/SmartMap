package ch.epfl.smartmap.servercom;

import java.util.List;
import java.util.Map;

import android.location.Location;

import ch.epfl.smartmap.cache.User;

/**
 * A SmartMapParser knows how to translate text into friends, server error
 * message (and events?). Parsers may exist for a variety of formats, but they
 * all share this common interface.
 * 
 * @author marion-S
 * 
 * @author Pamoi (code reviewed : 9.11.2014)
 */

public interface SmartMapParser {

	User parseFriend(String s) throws SmartMapParseException;

	/**
	 * Parses some text, and returns the list of friends
	 * 
	 * @param s
	 *            the list to parse
	 * @param key
	 *            the key word for the list to parse, if there is one, and null
	 *            if not
	 * @return the list of friends created from parsing s
	 * @throws SmartMapParseException
	 *             if s does not represent a valid list of friends (according to
	 *             the format that the parser supports)
	 */
	List<User> parseFriends(String s, String key) throws SmartMapParseException;

	/**
	 * Parses some text, and returns a map that maps id to positions
	 * 
	 * @param s
	 *            the text to parse
	 * @return the user's positions
	 * @throws SmartMapParseException
	 *             if s does not represent a valid list of positions (according
	 *             to the format that the parser supports)
	 */
	Map<Long, Location> parsePositions(String s) throws SmartMapParseException;

	/**
	 * Checks in the response returned by the server if the server returned
	 * Error, and if it is the case throws a SmartMapClientException with the
	 * server's message
	 * 
	 * @param s
	 *            the server's response
	 * @throws SmartMapClientException
	 *             if the server returned Error , SmartMapParseException if s
	 *             does not represent a valid server's response
	 */
	void checkServerError(String s) throws SmartMapParseException,
			SmartMapClientException;

	// TODO if necessary add a method to parse an event

}
