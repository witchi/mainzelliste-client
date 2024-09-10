/* 
 * SessionHandlerInterface.java 
 * created by tf 15.01.2018 15:26:22 
 * 
 * Copyright (C) iAS interActive Systems.
 * Gesellschaft fuer interaktive Medien mbH
 * Glogauerstr. 19
 * 10999 Berlin
 * Germany
 * http://www.interActive-Systems.de
 */

package de.pseudonymisierung.mainzelliste.client;

/**
 * Interface to create and read (restore) a {@link Session} from the remote pseudonymisation server.
 * 
 * @author Thomas Fritsche
 * @since 15.01.2018
 */
public interface SessionEditable {

    /**
     * Create a new session on the external list instance represented by this object.
     * 
     * @return The created session.
     * @throws MainzellisteNetworkException If a network error occurs while making the request.
     */
    Session createSession() throws MainzellisteNetworkException;

    /**
     * Restore a session from a server. It is verified that a session with the given session id exists on the Mainzelliste instance and all temp ID's
     * (i.e. "readPatients" tokens that allow for reading a single patient) are retrieved by a GET request and added to the session cache. This method
     * is useful to restore sessions if session data in the calling application is serialised. Neither {@link Session} nor
     * {@link MainzellisteConnection} objects can be serialised due to being bound to a CloseableHttpClient instance.
     * 
     * @param sessionId Id of the session to read.
     * @return A session object representing the requested session, with the mapping of permanent to temporary identifiers restored.
     * @throws MainzellisteNetworkException If a network error occurred while making the request.
     * @throws InvalidSessionException If the session does not exist anymore on the Mainzelliste instance.
     */
    Session readSession(final String sessionId) throws MainzellisteNetworkException, InvalidSessionException;

}