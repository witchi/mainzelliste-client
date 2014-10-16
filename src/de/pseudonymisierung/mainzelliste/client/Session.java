package de.pseudonymisierung.mainzelliste.client;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

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

	private MainzellisteConnection connection;
	private String sessionId;
	private Set<String> defaultResultFields;
	private Set<String> defaultResultIds;

	/**
	 * Create a session with the specified ID and MainzellisteConnector. Used
	 * internally only by {@link MainzellisteConnection#createSession()}.
	 * 
	 * @param sessionId
	 *            The session id as returned by the Mainzelliste.
	 * @param connection
	 *            A MainzellisteConnections object that represent the instance
	 *            on which this session was created.
	 */
	Session(String sessionId, MainzellisteConnection connection) {
		this.sessionId = sessionId;
		this.connection = connection;
		this.defaultResultFields = null;
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
		MainzellisteResponse response = this.connection.doRequest(
				RequestMethod.GET, this.getSessionURI().toString(), null);
		return (response.getStatusCode() == 200);
	}

	/**
	 * Delete this session making a DELETE request on the session URI. Further
	 * calls on the session will fail with an exception.
	 */
	public void destroy() {
		// TODO - implement Session.destroy
		throw new UnsupportedOperationException();
	}

	public String getTempId(ID id) throws MainzellisteNetworkException,
			NullPointerException {
		if (this.getDefaultResultFields() == null) {
			throw new NullPointerException(
					"Tried to get temp id with default result fields in a session where"
							+ " no default result fields are defined.");
		}
		return getTempId(id, defaultResultFields, null);
	}

	/**
	 * Get a temporary identifier (temp-id). A temp-id is an identifier for a
	 * patient for the duration of a session. It acts furthermore as an
	 * authorization token to read the specified identifying data and permanent
	 * identifiers of the patient from the Mainzelliste.
	 * 
	 * @param id
	 *            A permanent identifier of a patient.
	 * @return A temporary identifier, valid as long as this session is valid,
	 *         or null if the given permanent identifier is unknown.
	 */
	public String getTempId(ID id, Collection<String> resultFields,
			Collection<String> resultIds) throws MainzellisteNetworkException {
		ReadPatientsToken t = new ReadPatientsToken();
		if (resultFields != null)
			t.setResultFields(resultFields);
		if (resultIds != null)
			t.setResultIds(resultIds);
		if (id == null)
			throw new NullPointerException(
					"ID object passed to getTempId is null!");
		t.addSearchId(id);
		MainzellisteResponse response = this.connection.doRequest(
				RequestMethod.POST, getSessionURI().resolve("tokens/")
						.toString(), t.toJSON().toString());
		try {
			return response.getDataJSON().getString("id");
		} catch (JSONException e) {
			throw new Error(e);
		}
	}

	/**
	 * Get the corresponding permanent patient identifier for a temp id.
	 * 
	 * @param tempId
	 * @return The corresponding permanent identifier for which the temp id was
	 *         created, or null if no such temp id exists.
	 */
	public ID getId(String tempId) {
		// TODO - implement Session.getId
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param patientToEdit
	 * @param redirect
	 */
	public EditPatientToken getEditPatientToken(ID patientToEdit, URL redirect) {
		// TODO - implement Session.getEditPatientToken
		throw new UnsupportedOperationException();
	}

	public void removeTempId() {
		// TODO - implement Session.removeTempId
		throw new UnsupportedOperationException();
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
	 * @return the defaultResultFields
	 */
	public Set<String> getDefaultResultFields() {
		Arrays.asList("vorname", "nachname");
		return defaultResultFields;
	}

	/**
	 * @param defaultResultFields
	 *            the defaultResultFields to set
	 */
	public void setDefaultResultFields(Collection<String> defaultResultFields) {
		this.defaultResultFields = new HashSet<String>(defaultResultFields);
	}

}