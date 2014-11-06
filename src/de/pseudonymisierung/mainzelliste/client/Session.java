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
	 * Set to false when a session is invalidated by calling destroy().
	 */
	private boolean isValid = true;
	private MainzellisteConnection connection;
	private String sessionId;
	private Set<String> defaultResultFields;
	private Set<String> defaultResultIds;
	private Map<ID, String> tempIdById;

	private Map<String, ID> idByTempId;

	/**
	 * Create a session with the specified ID and MainzellisteConnector. Used
	 * internally only by {@link MainzellisteConnection#createSession()}.
	 * 
	 * @param sessionId
	 *            The session id as returned by the Mainzelliste.
	 * @param connection
	 *            A MainzellisteConnections object that represents the instance
	 *            on which this session was created.
	 */
	Session(String sessionId, MainzellisteConnection connection) {
		this.sessionId = sessionId;
		this.connection = connection;
		this.defaultResultFields = null;
		this.defaultResultIds = null;
		this.tempIdById = new HashMap<ID, String>();
		this.idByTempId = new HashMap<String, ID>();
	}

	/**
	 * Get the id of this session.
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Get the URI of this session.
	 */
	public URI getSessionURI() {
		try {
			/*
			 * The trailing slash is important so that further .resolve calls
			 * give the intended result. (Otherwise the last portion of the URI
			 * would be removed).
			 */
			return connection.getMainzellisteURI().resolve("sessions/")
					.resolve(sessionId + "/");
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
	 * @throws MainzellisteNetworkException
	 *             If a network error occurs while making the request.
	 */
	public boolean isValid() throws MainzellisteNetworkException {
		/*
		 * Check internal flag first in order to avoid network access if this
		 * session was invalidated by calling destroy().
		 */
		if (!this.isValid)
			return false;
		MainzellisteResponse response = this.connection.doRequest(
				RequestMethod.GET, this.getSessionURI().toString(), null);
		return (response.getStatusCode() == 200);
	}

	/**
	 * Delete this session making a DELETE request on the session URI. Further
	 * calls on the session will fail with an exception.
	 * 
	 * @throws MainzellisteNetworkException
	 *             If a network error occurs while making the request.
	 */
	public void destroy() throws MainzellisteNetworkException {
		this.connection.doRequest(RequestMethod.DELETE, this.getSessionURI()
				.toString(), null);
		this.isValid = false;
	}

/**
	 * Shortcut for {@link Session#getTempId(ID, Collection, Collection) that
	 * uses default value for the returned result fields and identifiers. The
	 * default values are set via
	 * 
	 * @link Session#setDefaultResultFields(Collection)} and
	 *       {@link Session#setDefaultResultIds(Collection)}.
	 * 
	 * @see Session#getTempId(ID, Collection, Collection)
	 */
	public String getTempId(ID id) throws MainzellisteNetworkException,
			InvalidSessionException {
		return getTempId(id, defaultResultFields, defaultResultIds);
	}

	/**
	 * Get a temporary identifier (temp-id). A temp-id is an identifier for a
	 * patient for the duration of a session. It acts furthermore as an
	 * authorization token to read the specified identifying data and permanent
	 * identifiers of the patient from the Mainzelliste.
	 * 
	 * @param id
	 *            A permanent identifier of a patient.
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
	public String getTempId(ID id, Collection<String> resultFields,
			Collection<String> resultIds) throws MainzellisteNetworkException,
			InvalidSessionException {

		if (id == null)
			throw new NullPointerException(
					"ID object passed to getTempId is null!");

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
		MainzellisteResponse response = this.connection.doRequest(
				RequestMethod.POST, getSessionURI().resolve("tokens/")
						.toString(), t.toJSON().toString());
		if (response.getStatusCode() == 404) {
			throw new InvalidSessionException();
		}
		try {
			tempId = response.getDataJSON().getString("id");
			tempIdById.put(id, tempId);
			idByTempId.put(tempId, id);
			return tempId;
		} catch (JSONException e) {
			throw new Error(e);
		}
	}

	/**
	 * Get all temporary identifiers of this session.
	 * 
	 * @return
	 */
	public Set<String> getTempIds() {
		return idByTempId.keySet();
	}

	/**
	 * Get all permanent identifiers for which this session holds temp ids.
	 * 
	 * @return
	 */
	public Set<ID> getIDs() {
		return tempIdById.keySet();
	}

	/**
	 * Get the corresponding permanent patient identifier for a temp-id.
	 * 
	 * @param tempId
	 *            A temporary identifier.
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
	 * 
	 * @param patientToEdit
	 * @param redirect
	 * @throws MainzellisteNetworkException 
	 * @throws InvalidSessionException 
	 */
	public String getEditPatientToken(ID patientToEdit, URL redirect) throws MainzellisteNetworkException, InvalidSessionException {
		
		EditPatientToken t = new EditPatientToken(patientToEdit);
		t.setRedirect(redirect);
		MainzellisteResponse response = this.connection.doRequest(RequestMethod.POST, this.getSessionURI().resolve("tokens/").toString(), t.toJSON().toString());
		if (response.getStatusCode() == 404)
			throw new InvalidSessionException();
		else if (response.getStatusCode() != 201)
			throw MainzellisteNetworkException.fromResponse(response);
		
		try {
			return response.getDataJSON().getString("id");
		} catch (JSONException e) {
			throw new MainzellisteNetworkException("Request to create token returned illegal data", e);
		}
	}

	/**
	 * Remove temporary identifier. The temp-id is removed from the internal
	 * cache and deleted on the Mainzelliste instance by invalidating the
	 * corresponding token.
	 * 
	 * @throws MainzellisteNetworkException
	 *             If a network error occured while making the request.
	 * @throws InvalidSessionException
	 *             If the session does not exist anymore on the Mainzelliste
	 *             instance.
	 */
	public void removeTempId(String tempId)
			throws MainzellisteNetworkException, InvalidSessionException {
		ID idToDelete = this.idByTempId.remove(tempId);
		if (idToDelete != null)
			this.tempIdById.remove(idToDelete);
		MainzellisteResponse response = this.connection.doRequest(
				RequestMethod.DELETE, getSessionURI().resolve("tokens/")
						.resolve(tempId).toString(), null);
		if (response.getStatusCode() == 404) {
			throw new InvalidSessionException();
		}
	}

	/**
	 * 
	 * @param idTypes
	 * @param callback
	 */
	public AddPatientToken getAddPatientToken(Set<String> idTypes, URL callback) {
		// TODO - implement Session.getAddPatientToken
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the current default fields for {@link Session#getTempId(ID)}.
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
	 * Set Tokens (Temp-IDs) from JSON array. Used by
	 * {@link MainzellisteConnection#readSession(String)}
	 * 
	 * @param tokensJSON
	 * @throws JSONException
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
					ID id = new ID(idJSON.getString("idType"),
							idJSON.getString("idString"));
					tempIdById.put(id, tempId);
					idByTempId.put(tempId, id);
				}
			}
		}
	}
}