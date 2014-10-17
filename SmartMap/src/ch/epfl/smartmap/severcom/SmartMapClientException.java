package ch.epfl.smartmap.severcom;

/**
 * Thrown to indicate a problem encountered by a SmartMap client when
 * communicating to the SmartMap server.
 *
 */

/**
 * @author marion-S
 * 
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
