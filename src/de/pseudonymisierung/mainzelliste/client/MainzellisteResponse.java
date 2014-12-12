package de.pseudonymisierung.mainzelliste.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;

/**
 * Encapsulates the response to an http request to a Mainzelliste instance.
 * Constructed by
 * {@link MainzellisteConnection#doRequest(de.pseudonymisierung.mainzelliste.client.MainzellisteConnection.RequestMethod, String, String)
 * MainzellisteConnection#doRequest()}
 */
class MainzellisteResponse {

	/** The returned HTTP status code. */
	private int statusCode;

	/** The returned entity. */
	private String data;

	/** Create instance from an HTTP response. */
	MainzellisteResponse(CloseableHttpResponse response)
			throws MainzellisteNetworkException {
		this.statusCode = response.getStatusLine().getStatusCode();
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null)
				data = EntityUtils.toString(response.getEntity());
			else
				data = "";
		} catch (IOException e) {
			throw new MainzellisteNetworkException(
					"IO error while reading response from Mainzelliste", e);
		}
	}

	/**
	 * Get the HTTP status code of this response.
	 * 
	 * @return The HTTP status code of this response.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Get the returned entity.
	 * 
	 * @return The returned entity. If the response does not contain an entity,
	 *         an empty String.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Get data as JSON object.
	 * 
	 * @return Response entity as JSONObject.
	 * 
	 * @throws MainzellisteNetworkException
	 *             If the response is not valid JSON.
	 */
	public JSONObject getDataJSON() throws MainzellisteNetworkException {
		try {
			return new JSONObject(this.data);
		} catch (JSONException e) {
			throw new MainzellisteNetworkException(
					"Error while parsing response from Mainzelliste", e);
		}
	}
}
