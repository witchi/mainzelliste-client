package de.pseudonymisierung.mainzelliste.client;

/**
 * Indicates that during a call to a session resource, the session was not
 * found. This is, in most cases, due to the session having timed out or being
 * deleted. In most cases, this case should be handled by creating a new
 * session.
 */
public class InvalidSessionException extends Exception {

}
