package de.pseudonymisierung.mainzelliste.client;

import de.pseudonymisierung.mainzelliste.client.MainzellisteResponse;

/**
 * Exception for error concerning the network connection to the Idat server.
 * 
 * @author borg
 *
 */
public class MainzellisteNetworkException extends Exception {

	public MainzellisteNetworkException() {
		super("A network error occured while accessing the Idat server!");
	}

	public MainzellisteNetworkException(Throwable t) {
		super("A network error occured while accessing the Idat server: "
				+ t.getMessage(), t);
	}

	public MainzellisteNetworkException(String message) {
		super(message);
	}

	public MainzellisteNetworkException(String message, Throwable t) {
		super(message, t);
	}

	public static MainzellisteNetworkException fromResponse(
			MainzellisteResponse response) {
		return new MainzellisteNetworkException(
				"Request to Mainzelliste failed with status code "
						+ response.getStatusCode() + " and message "
						+ response.getData());
	}
}
