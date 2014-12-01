package ch.epfl.smartmap.servercom;

/**
 * Thrown to indicate a problem encountered by a SmartMap client when
 * communicating to the SmartMap server.
 */

/**
 * @author marion-S
 * @author Pamoi (code reviewed : 9.11.2014)
 */
public class SmartMapClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public SmartMapClientException() {
		super();
	}

	public SmartMapClientException(String message) {
		super(message);
	}

	public SmartMapClientException(Throwable throwable) {
		super(throwable);
	}
}
