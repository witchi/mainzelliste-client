/*
 * Copyright (C) 2015 Working Group on Joint Research, University Medical Center
 * Mainz Contact: info@osse-register.de
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * 
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.pseudonymisierung.mainzelliste.client;

import de.pseudonymisierung.mainzelliste.client.MainzellisteResponse;

/**
 * Exception for errors concerning the network connection to the Idat server.
 */
public class MainzellisteNetworkException extends Exception {

	/**
	 * Create an instance from an instance of {@link MainzellisteResponse}. The
	 * HTTP status code and the response entity are included in the error
	 * message.
	 * 
	 * @param response
	 *            The Mainzelliste response for which to create an exception.
	 * @return The created exception.
	 */
	public static MainzellisteNetworkException fromResponse(
			MainzellisteResponse response) {
		return new MainzellisteNetworkException(
				"Request to Mainzelliste failed with status code "
						+ response.getStatusCode() + " and message "
						+ response.getData());
	}

	/**
	 * Create an instance with the default error message.
	 */
	public MainzellisteNetworkException() {
		super("A network error occured while accessing the Idat server!");
	}

	/**
	 * Create an instance with default error message and cause t.
	 * 
	 * @param t
	 *            The original cause of this exception.
	 */
	public MainzellisteNetworkException(Throwable t) {
		super("A network error occured while accessing the Idat server: "
				+ t.getMessage(), t);
	}

	/**
	 * Create an instance with a custom error message.
	 * 
	 * @param message
	 *            A custom error message.
	 */
	public MainzellisteNetworkException(String message) {
		super(message);
	}

	/**
	 * Create an instance with a custom error message and cause t.
	 * 
	 * @param message
	 *            A custom error message.
	 * @param t
	 *            The original cause of this exception.
	 */
	public MainzellisteNetworkException(String message, Throwable t) {
		super(message, t);
	}
}
