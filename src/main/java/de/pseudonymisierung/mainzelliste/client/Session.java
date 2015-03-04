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

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.pseudonymisierung.mainzelliste.client.MainzellisteConnection.RequestMethod;
import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;

/**
 * Represents a session on a Mainzelliste instance. Sessions store data
 * associated with a client, such as temporary identifiers or authorization
 * tokens.
 * 
 * Sessions are created by calling
 * {@link MainzellisteConnection#createSession()}.
 */
public class Session {

    /**
     * Identifier of this session.
     */
    private final String id;
    /**
     * Whether this session has been invalidated by a call to
     * {@link Session#destroy()}.
     */
    private boolean invalidated = false;
    /**
     * Connection to the Mainzelliste on which this session exists.
     */
    private MainzellisteConnection connection;
    /**
     * List of field names that are used by default in connection with temporary
     * identifiers.
     * 
     * @see Session#getTempId(ID)
     * @see Session#setDefaultResultFields(Collection)
     */
    private Set<String> defaultResultFields;
    /**
     * List of id types that are used by default in connection with temporary
     * identifiers.
     * 
     * @see Session#getTempId(ID)
     * @see Session#setDefaultResultIds(Collection)
     */
    private Set<String> defaultResultIds;

    /**
     * Cache for mapping of permanent temporary identifiers.
     */
    private Map<ID, String> tempIdById;
    /**
     * Cache for mapping of temporary to permanent identifiers.
     */
    private Map<String, ID> idByTempId;

    /**
     * Create a session with the specified ID and MainzellisteConnector. Used
     * internally only by {@link MainzellisteConnection#createSession()}.
     * 
     * @param id
     *            The session id as returned by the Mainzelliste.
     * @param connection
     *            A MainzellisteConnections object that represents the instance
     *            on which this session was created.
     */
    Session(String id, MainzellisteConnection connection) {
        this.id = id;
        this.connection = connection;
        this.defaultResultFields = null;
        this.defaultResultIds = null;
        this.tempIdById = new HashMap<ID, String>();
        this.idByTempId = new HashMap<String, ID>();
    }

    /**
     * Get the id of this session.
     * 
     * @return The identifier of this session object.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the URI of this session.
     * 
     * @return The URI of this session object.
     */
    public URI getURI() {
        try {
            /*
             * The trailing slash is important so that further .resolve calls
             * give the intended result. (Otherwise the last portion of the URI
             * would be removed).
             */
            return connection.getMainzellisteURI().resolve("sessions/").resolve(id + "/");
        } catch (Exception e) { // URISyntaxException, MalformedURLException
            /*
             * If an invalid URL is constructed here, something is serioursly
             * wrong.
             */
            throw new Error(e);
        }
    }

    /**
     * Check whether this session is valid, i.e. still exists on the
     * Mainzelliste instance. This is verified by a GET request on the session
     * URI.
     * 
     * @return True if this session is valid, false otherwise.
     * 
     * @throws MainzellisteNetworkException
     *             If a network error occurs while making the request.
     */
    public boolean isValid() throws MainzellisteNetworkException {
        /*
         * Check internal flag first in order to avoid network access if this
         * session was invalidated by calling destroy().
         */
        if (this.invalidated)
            return false;
        MainzellisteResponse response = this.connection.doRequest(RequestMethod.GET, this.getURI().toString(), null);
        return (response.getStatusCode() == 200);
    }

    /**
     * Get the current default fields for {@link Session#getTempId(ID)}.
     * 
     * @return Set of names of default result fields or null if none are
     *         defined.
     */
    public Set<String> getDefaultResultFields() {
        return defaultResultFields;
    }

    /**
     * Set the default IDAT fields for {@link Session#getTempId(ID)}.
     * 
     * @param defaultResultFields
     *            A list of field names, must correspond to field names
     *            configured on the connected Mainzelliste instance.
     */
    public void setDefaultResultFields(Collection<String> defaultResultFields) {
        this.defaultResultFields = new HashSet<String>(defaultResultFields);
    }

    /**
     * Get the current default identifiers for {@link Session#getTempId(ID)}.
     * 
     * @return String of names of default result identifiers or null if none are
     *         defined.
     */
    public Set<String> getDefaultResultIds() {
        return defaultResultIds;
    }

    /**
     * Set the default identifiers for {@link Session#getTempId(ID)}.
     * 
     * @param defaultResultIds
     *            A list of identifier names, must correspond to id types
     *            configured on the connected Mainzelliste instance.
     */
    public void setDefaultResultIds(Collection<String> defaultResultIds) {
        this.defaultResultIds = new HashSet<String>(defaultResultIds);
    }

    /**
     * Get a temporary identifier (temp-id). A temp-id is an identifier for a
     * patient for the duration of a session. It acts furthermore as an
     * authorization token to read the specified identifying data and permanent
     * identifiers of the patient from the Mainzelliste.
     * 
     * @param id
     *            A permanent identifier of the patient for which to obtain a
     *            temporary id.
     * @param resultFields
     *            The IDAT fields that can be retreived by this temp-id. Legal
     *            values are all field names that are configured on the
     *            connected Mainzelliste instance.
     * @param resultIds
     *            The permanent identifiers that can be retreived by this
     *            temp-id. Legal values are all id types that are configured on
     *            the connected Mainzelliste instance.
     * @return A temporary identifier, valid as long as this session is valid,
     *         or null if the given permanent identifier is unknown.
     * @throws InvalidSessionException
     *             If the session does not exist anymore on the Mainzelliste
     *             instance.
     * @throws MainzellisteNetworkException
     *             If a network error occured while making the request.
     * 
     */
    public String getTempId(ID id, Collection<String> resultFields, Collection<String> resultIds) throws MainzellisteNetworkException,
            InvalidSessionException {

        if (id == null)
            throw new NullPointerException("ID object passed to getTempId is null!");

        // Try to find cached value
        String tempId = this.tempIdById.get(id);
        if (tempId != null)
            return tempId;

        // Otherwise get temp-id from Mainzelliste and store in cache
        ReadPatientsToken t = new ReadPatientsToken();
        if (resultFields != null)
            t.setResultFields(resultFields);
        if (resultIds != null)
            t.setResultIds(resultIds);
        t.addSearchId(id);
        tempId = this.getToken(t);
        tempIdById.put(id, tempId);
        idByTempId.put(tempId, id);
        return tempId;
    }

    /**
     * Shortcut for {@link Session#getTempId(ID, Collection, Collection)} that
     * uses default value for the returned result fields and identifiers. The
     * default values are set via
     * 
     * @param id
     *            A permanent identifier of the patient for which to obtain a
     *            temporary id.
     * @return A temporary identifier, valid as long as this session is valid,
     *         or null if the given permanent identifier is unknown.
     * @throws InvalidSessionException
     *             If the session does not exist anymore on the Mainzelliste
     *             instance.
     * @throws MainzellisteNetworkException
     *             If a network error occured while making the request.
     * @see Session#setDefaultResultFields(Collection)
     * @see Session#setDefaultResultIds(Collection)
     * 
     * @see Session#getTempId(ID, Collection, Collection)
     */
    public String getTempId(ID id) throws MainzellisteNetworkException, InvalidSessionException {
        return getTempId(id, defaultResultFields, defaultResultIds);
    }

    /**
     * Get all temporary identifiers of this session.
     * 
     * @return Set of temporary identifiers.
     */
    public Set<String> getTempIds() {
        return idByTempId.keySet();
    }

    /**
     * Remove temporary identifier. The temp-id is removed from the internal
     * cache and deleted on the Mainzelliste instance by invalidating the
     * corresponding token.
     * 
     * @param tempId
     *            A temporary identifier.
     * 
     * @throws MainzellisteNetworkException
     *             If a network error occured while making the request.
     * @throws InvalidSessionException
     *             If the session does not exist anymore on the Mainzelliste
     *             instance.
     */
    public void removeTempId(String tempId) throws MainzellisteNetworkException, InvalidSessionException {
        /*
         * Keep mapping temp-id -> id. Avoids errors when temp-ids are used in
         * URLs and a stale temp-id appears because the user uses the "back"
         * button.
         */
        ID idToDelete = this.idByTempId.get(tempId);
        if (idToDelete != null)
            this.tempIdById.remove(idToDelete);
        MainzellisteResponse response = this.connection.doRequest(RequestMethod.DELETE, getURI().resolve("tokens/").resolve(tempId)
                .toString(), null);
        if (response.getStatusCode() == 404) {
            throw new InvalidSessionException();
        }
    }

    /**
     * Remove temporary identifier. If a temp-id exists for the given patient,
     * it is removed from the internal cache and deleted on the Mainzelliste
     * instance by invalidating the corresponding token.
     * 
     * @param id
     *            A permanent patient identifier.
     * 
     * @throws MainzellisteNetworkException
     *             If a network error occured while making the request.
     * @throws InvalidSessionException
     *             If the session does not exist anymore on the Mainzelliste
     *             instance.
     */
    public void removeTempId(ID id) throws MainzellisteNetworkException, InvalidSessionException {
        if (this.tempIdById.containsKey(id))
            this.removeTempId(tempIdById.get(id));
    }

    /**
     * Get the corresponding permanent patient identifier for a temp-id.
     * 
     * @param tempId
     *            A temporary patient identifier.
     * @return The corresponding permanent identifier for which the temp-id was
     *         created, or null if no such temp-id exists.
     * @throws NullPointerException
     *             If tempId is null.
     */
    public ID getId(String tempId) {
        if (tempId == null)
            throw new NullPointerException("Temp-id passed to getId is null!");
        return this.idByTempId.get(tempId);
    }

    /**
     * Get all permanent identifiers for which this session holds temporary
     * identifiers.
     * 
     * @return Set of permanent identifiers.
     * 
     */
    public Set<ID> getIDs() {
        return tempIdById.keySet();
    }

    /**
     * Get a token of type 'addPatient', which authorizes to create one patient
     * on the Mainzelliste. The returned string is typically handed to the
     * user's web browser as an authorization ticket.
     * 
     * @param callback
     *            URL to be called by Mainzelliste upon creating the patient.
     * @param redirect
     *            URL to which the user should be redirected after creating the
     *            patient. Can include template variables where the variable
     *            name equals to an id type, it is replaced by the created
     *            identifier.
     * @return The id of the created token.
     * @throws MainzellisteNetworkException
     *             If a network error occured while making the request.
     * @throws InvalidSessionException
     *             If the session does not exist anymore on the Mainzelliste
     *             instance.
     */
    public String getAddPatientToken(URL callback, String redirect) throws MainzellisteNetworkException, InvalidSessionException {
        AddPatientToken t = new AddPatientToken();
        t.callback(callback).redirect(redirect);
        return getToken(t);
    }

    /**
     * Create a token that allows to edit a patient's identifying data.
     * 
     * @param patientToEdit
     *            The patient that can be edited by using the token.
     * @param redirect
     *            A redirect URL that the user should be referred to after the
     *            edit operation.
     * @return The identifier of the created token.
     * @throws MainzellisteNetworkException
     *             If a network error occured while making the request.
     * @throws InvalidSessionException
     *             If the session does not exist anymore on the Mainzelliste
     *             instance.
     */
    public String getEditPatientToken(ID patientToEdit, URL redirect) throws MainzellisteNetworkException, InvalidSessionException {

        EditPatientToken t = new EditPatientToken(patientToEdit);
        t.redirect(redirect);
        return this.getToken(t);
    }

    /**
     * Create a token with the given token data. Used by convenience functions
     * for particular token types.
     * 
     * @param t
     *            Token object with template data for the token that should be
     *            created.
     * @return The identifier of the created token.
     * @throws MainzellisteNetworkException
     *             If a network error occured while making the request.
     * @throws InvalidSessionException
     *             If the session does not exist anymore on the Mainzelliste
     *             instance.
     */
    public String getToken(Token t) throws MainzellisteNetworkException, InvalidSessionException {

        System.out.println("TOKEN = " + t.toJSON().toString());

        MainzellisteResponse response = this.connection.doRequest(RequestMethod.POST, this.getURI().resolve("tokens/").toString(), t
                .toJSON().toString());

        System.out.println("RCODE = " + response.getStatusCode());

        System.out.println("JSON = " + response.getDataJSON().toString());

        if (response.getStatusCode() == 404)
            throw new InvalidSessionException();
        else if (response.getStatusCode() != 201)
            throw MainzellisteNetworkException.fromResponse(response);

        try {
            // MOOTODO WHY is "id" null?
            // return response.getDataJSON().getString("tokenId");
            return response.getDataJSON().getString("id");
        } catch (JSONException e) {
            throw new MainzellisteNetworkException("Request to create token returned illegal data", e);
        }

    }

    /**
     * Delete this session making a DELETE request on the session URI. Further
     * calls on the session will fail with an exception.
     * 
     * @throws MainzellisteNetworkException
     *             If a network error occurs while making the request.
     */
    public void destroy() throws MainzellisteNetworkException {
        this.connection.doRequest(RequestMethod.DELETE, this.getURI().toString(), null);
        this.invalidated = true;
    }

    /**
     * Set tokens (temp-ids) from JSON array. Used by
     * {@link MainzellisteConnection#readSession(String)}. The provided array of
     * tokens is searched for tokens of type "readPatients" that provide access
     * a single patient only. These are considered as temporary identifiers and
     * added as such to the session object.
     * 
     * @param tokensJSON
     *            JSON array of tokens as returned by reading the tokens from
     *            Mainzelliste by GET /sessions/{sid}/tokens
     * @throws JSONException
     *             if the provided data does not conform to the expected format.
     */
    void setTokens(JSONArray tokensJSON) throws JSONException {
        for (int i = 0; i < tokensJSON.length(); i++) {
            JSONObject thisToken = tokensJSON.getJSONObject(i);
            if (thisToken.getString("type").equals("readPatients")) {
                JSONObject tokenData = thisToken.getJSONObject("data");
                JSONArray searchIDs = tokenData.getJSONArray("searchIds");
                // Token is considered a temp-id if only one ID is searched for
                if (searchIDs.length() == 1) {
                    String tempId = thisToken.getString("id");
                    JSONObject idJSON = searchIDs.getJSONObject(0);
                    ID id = new ID(idJSON.getString("idType"), idJSON.getString("idString"));
                    tempIdById.put(id, tempId);
                    idByTempId.put(tempId, id);
                }
            }
        }
    }
}