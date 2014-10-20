/**
 *
 */
package ch.epfl.smartmap.servercom;


/**
 * A client object to a SmartMap server that abstracts the underlying communication protocol. The server protocol can be
 * read <a href="http://girod.ddns.net/protocol.txt">here</a>
 *
 * @author SpicyCH
 */
public interface ServerLoginProtocol {

    /**
     * The different authentication statuses.
     *
     * @author SpicyCH
     */
    public enum AuthenticationStatus {
        CONNECTION_SUCCESS, CONNECTION_FAILED, MALFORMED_URL, LOGIN_FAILED, LOGIN_SUCCESS, STORING_ERROR, SMS_SENT,
        LOGIN_FAILED_CANNOT_SEND
    }

    /**
     * The different connection statuses
     *
     * @author SpicyCH
     *
     */
    public enum ConnectionStatus {
        SENT_SUCESSFUL, SENT_FAILURE, SENT_FAILURE_COOKIEMANAGER_ERROR
    }

}