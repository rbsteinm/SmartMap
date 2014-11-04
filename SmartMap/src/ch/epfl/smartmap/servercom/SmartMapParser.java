package ch.epfl.smartmap.servercom;

import java.util.List;

import ch.epfl.smartmap.cache.User;

/**
 * A SmartMapParser knows how to translate text into friends, server error
 * message (and events?). Parsers may exist for a variety of formats, but they
 * all share this common interface.
 * 
 * @author marion-S
 */

public interface SmartMapParser {

    /**
     * Parses some text, and returns the created Friend.
     * 
     * @param s
     *            The text to parse
     * @return A Friend instance created from parsing s
     * @throws SmartMapParseException
     *             if s does not represent a valid friend (according to the
     *             format that the parser supports)
     */
    User parseFriend(String s) throws SmartMapParseException;

    /**
     * Parses some text, and return the list of friends
     * 
     * @param s
     * @return the list of friends
     * @throws SmartMapParseException
     *             if s does not represent a valid list of friends (according to
     *             the format that the parser supports)
     */
    List<User> parseFriends(String s) throws SmartMapParseException;

    /**
     * Checks in the response returned by the server if the server returned
     * ERROR, and if it is the case throws a SmartMapClientException with the
     * server's message
     * 
     * @param s
     *            the server's response
     * @throws SmartMapClientException
     *             , SmartMapParseException
     */
    void checkServerError(String s) throws SmartMapParseException,
        SmartMapClientException;

    // TODO if necessary add a method to parse an event

}
