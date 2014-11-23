package ch.epfl.smartmap.servercom;

import java.util.List;

import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.User;

/**
 * A SmartMapParser knows how to translate text into friends, server error
 * message (and events?). Parsers may exist for a variety of formats, but they
 * all share this common interface.
 * 
 * @author marion-S
 * @author Pamoi (code reviewed : 9.11.2014)
 */

public interface SmartMapParser {

    /**
     * Parses some text, and returns a friend
     * 
     * @param s
     *            the text to parse, representing a friend
     * @return
     * @throws SmartMapParseException
     */

    User parseFriend(String s) throws SmartMapParseException;

    /**
     * Parses some text, and returns the list of friends
     * 
     * @param s
     *            the list to parse
     * @param key
     *            the key word for the list to parse, if there is one
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
     *            the text to parse, representing a list of positions
     * @return the user's positions
     * @throws SmartMapParseException
     *             if s does not represent a valid list of positions (according
     *             to the format that the parser supports)
     */

    List<User> parsePositions(String s) throws SmartMapParseException;

    /**
     * Checks in the response returned by the server if the server returned
     * Error, and if it is the case throws a SmartMapClientException with the
     * server's message
     * 
     * @param s
     *            the server's response
     * @throws SmartMapClientException
     *             if the server returned Error
     * @throws SmartMapParseException
     *             if s
     *             does not represent a valid server's response
     * @throws ServerFeedbackException
     */
    void checkServerError(String s) throws SmartMapParseException, SmartMapClientException;

    /**
     * Parses some text, and returns a list of ids
     * 
     * @param s
     *            the text to parse, representing a list of ids
     * @param key
     *            the key word for the list to parse, if there is one
     * @return the list of ids
     * @throws SmartMapParseException
     */
    List<Long> parseIds(String s, String key) throws SmartMapParseException;

    /**
     * Parses some text, and returns an event
     * 
     * @param s
     *            the text to parse, representing an event
     * @return the event
     * @throws SmartMapParseException
     */
    Event parseEvent(String s) throws SmartMapParseException;

    /**
     * Parses some text, and returns a list of events
     * 
     * @param s
     *            the text to parse, representing a list of events
     * @return the list of events
     * @throws SmartMapParseException
     */
    List<Event> parseEventList(String s) throws SmartMapParseException;

}