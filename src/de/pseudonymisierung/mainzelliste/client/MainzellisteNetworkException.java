package de.pseudonymisierung.mainzelliste.client;

import de.pseudonymisierung.mainzelliste.client.MainzellisteResponse;

/**
 * Exception for errors concerning the network connection to the Mainzelliste
 * instance.
 * 
 *
 */
public class MainzellisteNetworkException extends Exception {

	private static final long serialVersionUID = 1L;

	/** Create instance with default message */
	public MainzellisteNetworkException() {
		super("A network error occured while accessing the Idat server!");
	}

	/**
	 * Create instance with cause t. t.getMessage will appear in the error
	 * message.
	 */
	public MainzellisteNetworkException(Throwable t) {
		super("A network error occured while accessing the Idat server: "
				+ t.getMessage(), t);
	}

	/** Create instance with custom error message. */
	public MainzellisteNetworkException(String message) {
		super(message);
	}

	/** Create instance with custom error message and cause t. */
	public MainzellisteNetworkException(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * Create instance from an instance of {@link MainzellisteResponse}. The
	 * returned status code and entity will appear in the error message.
	 */
	public static MainzellisteNetworkException fromResponse(
			MainzellisteResponse response) {
		return new MainzellisteNetworkException(
				"Request to Mainzelliste failed with status code "
						+ response.getStatusCode() + " and message "
						+ response.getData());
	}
}
