package ch.epfl.smartmap.servercom;

/**
 * An exception to be thrown when server's answers to parse violate their
 * format.
 * 
 * @author marion-S
 * 
 * */
public class SmartMapParseException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public SmartMapParseException() {
		super();
	}

	public SmartMapParseException(String message) {
		super(message);
	}

	public SmartMapParseException(Throwable throwable) {
		super(throwable);
	}
}
