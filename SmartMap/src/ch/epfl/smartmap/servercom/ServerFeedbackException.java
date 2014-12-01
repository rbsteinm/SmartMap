/**
 * 
 */
package ch.epfl.smartmap.servercom;

/**
 * This exception contains an error message that can be shown to the user.
 * It is sent back by the server but should ideally never happen.
 * 
 * @author Pamoi
 */
public class ServerFeedbackException extends SmartMapClientException {

    private static final long serialVersionUID = 1L;

    public ServerFeedbackException() {
        super();
    }

    public ServerFeedbackException(String message) {
        super(message);
    }

    public ServerFeedbackException(Throwable throwable) {
        super(throwable);
    }
}
