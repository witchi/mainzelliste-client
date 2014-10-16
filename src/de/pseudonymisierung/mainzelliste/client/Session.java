package de.pseudonymisierung.mainzelliste.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.codehaus.jettison.json.JSONException;

import de.pseudonymisierung.mainzelliste.client.MainzellisteConnection.RequestMethod;
import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;

public class Session {

	private MainzellisteConnection connection;
	private String sessionId;
	private Set<String> defaultResultFields;
	private Set<String> defaultResultIds;
	
	Session(String sessionId, MainzellisteConnection connection) {
		this.sessionId = sessionId;
		this.connection = connection;
		this.defaultResultFields = null;		
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public URI getSessionURI() {
		try {
			return connection.getMainzellisteURI()
					.resolve("sessions/")
					.resolve(sessionId + "/");
		} catch (Exception e) { // URISyntaxException, MalformedURLException
			/* If an invalid URL is constructed here, something is serioursly wrong. */
			throw new Error(e);
		}
	}

	public boolean isValid() throws MainzellisteNetworkException {
		MainzellisteResponse response = this.connection.doRequest(RequestMethod.GET, this.getSessionURI().toString(), null);
		return (response.getStatusCode() == 200);			
	}

	public void destroy() {
		// TODO - implement Session.destroy
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param id
	 */
	public String getTempId(ID id) throws MainzellisteNetworkException, NullPointerException {
		if (this.getDefaultResultFields() == null) {
			throw new NullPointerException("Tried to get temp id with default result fields in a session where"
					+ " no default result fields are defined.");
		}
		return getTempId(id, defaultResultFields, null);
	}

	public String getTempId(ID id, Collection<String> resultFields, Collection<String> resultIds) throws MainzellisteNetworkException{
		ReadPatientsToken t = new ReadPatientsToken();
		if (resultFields != null)
			t.setResultFields(resultFields);
		if (resultIds != null) 
			t.setResultIds(resultIds);
		if (id == null)
			throw new NullPointerException("ID object passed to getTempId is null!");
		t.addSearchId(id);
		MainzellisteResponse response = this.connection.doRequest(RequestMethod.POST, 
				getSessionURI().resolve("tokens/").toString(),
				t.toJSON().toString());
		try {
			return response.getDataJSON().getString("id");	
		} catch (JSONException e) {
			throw new Error(e);
		}
	}

	/**
	 * 
	 * @param tempId
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
	 * @param defaultResultFields the defaultResultFields to set
	 */
	public void setDefaultResultFields(Collection<String> defaultResultFields) {
		this.defaultResultFields = new HashSet<String>(defaultResultFields);
	}

}