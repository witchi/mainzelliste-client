package de.pseudonymisierung.mainzelliste.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;

/**
 * Encapulates the response to an http request to a Mainzelliste instance.
 * Constructed by
 * {@link MainzellisteConnection#doRequest(de.pseudonymisierung.mainzelliste.client.MainzellisteConnection.RequestMethod, String, String)
 * MainzellisteConnection#doRequest()}
 */
public class MainzellisteResponse {

	private int statusCode;

	private String data;

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
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/** Return data as JSON */
	public JSONObject getDataJSON() throws MainzellisteNetworkException {
		try {
			return new JSONObject(this.data);
		} catch (JSONException e) {
			throw new MainzellisteNetworkException(
					"Error while parsing response from Mainzelliste", e);
		}
	}
}
