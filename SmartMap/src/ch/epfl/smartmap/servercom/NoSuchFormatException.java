package ch.epfl.smartmap.servercom;

/**
 * An exception thrown when a Friend (or an Event?) with an unknown format needs
 * to be processed.
 * 
 * @author marion-S
 * @author Pamoi (code reviewed : 9.11.2014) : We could add a constructor to
 *         allow adding a message to the exception to make more explicite error
 *         reporting ?
 */
public class NoSuchFormatException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoSuchFormatException() {
        super();
    }

    public NoSuchFormatException(String message) {
        super(message);
    }

    public NoSuchFormatException(Throwable throwable) {
        super(throwable);
    }

}