package de.pseudonymisierung.mainzelliste.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;

/**
 * Manages connections to one Mainzelliste instance, authenticated by a specific
 * api key. Provides a public method to create new Sessions and package methods
 * providing access to the Mainzelliste instance to be used by session objects.
 * 
 */
public class MainzellisteConnection {

	/**
	 * API version to used. This is fixed for a specific version of this
	 * library.
	 */
	private static final String mainzellisteApiVersion = "2.0";
	/** API key by which to authenticate */
	private final String mainzellisteApiKey;
	/** URI of Mainzelliste instance to connect to. */
	private final URI mainzellisteURI;

	private CloseableHttpClient httpClient;

	/**
	 * Initialize connection to Mainzelliste with default Http client.
	 * 
	 * @param mainzellisteURI
	 *            Base URL of the Mainzelliste instance.
	 * @param mainzellisteApiKey
	 *            Api key to authenticate against the Mainzelliste instance.
	 * @throws URISyntaxException
	 *             If mainzellisteURI is not a valid URI.
	 */
	public MainzellisteConnection(String mainzellisteURI,
			String mainzellisteApiKey) throws URISyntaxException {
		this(mainzellisteURI, mainzellisteApiKey, HttpClientBuilder.create()
				.build());
	}

	/**
	 * Initialize connection to Mainzelliste with a provided Http client. This
	 * constructor should be used if special properties have to be set for Http
	 * connections (e.g. a proxy server).
	 * 
	 * @param mainzellisteURI
	 *            Base URI of the Mainzelliste instance.
	 * @param mainzellisteApiKey
	 *            Api key to authenticate against the Mainzelliste instance.
	 * @param httpClient
	 *            A CloseableHttpClient instance. All connections to the
	 *            Mainzelliste instance are made through this object.
	 * @throws URISyntaxException
	 *             If mainzellisteURI is not a valid URI.
	 */
	public MainzellisteConnection(String mainzellisteURI,
			String mainzellisteApiKey, CloseableHttpClient httpClient)
			throws URISyntaxException {
		/*
		 * Several methods call .resolve on this.mainzellisteURI, in order for
		 * this to work correctly, the URI has to end with "/".
		 */
		if (!mainzellisteURI.endsWith("/"))
			mainzellisteURI += "/";
		this.mainzellisteURI = new URI(mainzellisteURI);
		this.mainzellisteApiKey = mainzellisteApiKey;
		this.httpClient = httpClient;
	}

	/**
	 * Get the URL of this Mainzelliste instance.
	 * 
	 */
	public URI getMainzellisteURI() {
		return mainzellisteURI;
	}

	/**
	 * Create a new session on the Mainzelliste instance represented by this
	 * object.
	 * 
	 * @see Session
	 * @throws MainzellisteNetworkException
	 *             If a network error occurs while making the request.
	 */
	public Session createSession() throws MainzellisteNetworkException {
		MainzellisteResponse response = this.doRequest(RequestMethod.POST,
				"sessions", null);
		int responseCode = response.getStatusCode();
		if (responseCode != 201) {
			throw MainzellisteNetworkException.fromResponse(response);
		}
		JSONObject sessionData = response.getDataJSON();
		String sessionId;
		try {
			sessionId = sessionData.getString("sessionId");
		} catch (JSONException e) {
			// If we are here, Mainzelliste has responded with a correct status
			// code but with illegal data, which is a fatal error.
			throw new Error("Request to create session returned illegal data",
					e);
		}
		return new Session(sessionId, this);
	}

	/**
	 * Restore a session from a server. First, it is verified that a session
	 * with the given session id exists on the Mainzelliste instance and all
	 * temp ids (i.e. "readPatients" tokens that allow for reading a single
	 * patient) are retreived by a GET request and added to the session cache.
	 * 
	 * This method is useful to restore sessions if session data in the calling
	 * application is serialized. Neither {@link Session} nor
	 * {@link MainzellisteConnection} objects can be serialized due to being
	 * bound to a CloseableHttpClient instance.
	 * 
	 * @param sessionId
	 *            Id of the session to read.
	 * @return A session object representing the requested session, with the
	 *         mapping of permanent to temporary identifiers restored.
	 * @throws MainzellisteNetworkException
	 *             If a network error occured while making the request.
	 * @throws InvalidSessionException
	 *             If the session does not exist anymore on the Mainzelliste
	 *             instance.
	 */
	public Session readSession(String sessionId)
			throws MainzellisteNetworkException, InvalidSessionException {
		// Read tokens from session, also check if session exists
		MainzellisteResponse response = this.doRequest(RequestMethod.GET,
				"sessions/" + sessionId + "/tokens/", null);
		if (response.getStatusCode() == 404) {
			throw new InvalidSessionException();
		}
		Session s = new Session(sessionId, this);
		// No Content -> no tokens
		if (response.getStatusCode() == 204)
			return s;

		if (response.getStatusCode() == 200) {
			try {
				// Read tokens as JSON and delegate to Session#setTokens for
				// parsing
				JSONArray tokensJSON = new JSONArray(response.getData());
				s.setTokens(tokensJSON);
				return s;
			} catch (JSONException e) {
				throw new Error(
						"Request to read session tokens returned illegal data",
						e);
			}
		} else { // Illegal status code
			throw MainzellisteNetworkException.fromResponse(response);
		}
	}

	/** Definition of HTTP methods */
	public static enum RequestMethod {
		GET, POST, PUT, DELETE;
	}

	/**
	 * Utility method to make requests to this Mainzelliste instance.
	 * 
	 * @param method
	 *            The http method to use (GET, POST, PUT, DELETE).
	 * @param path
	 *            The resource path, either absolute or relative to the instance
	 *            URI.
	 * @param data
	 *            The data to transmit in JSON format.
	 * @return The response represented as an instance of
	 *         {@link MainzellisteResponse}.
	 * @throws MainzellisteNetworkException
	 *             If a network error occurs while making the request.
	 */
	MainzellisteResponse doRequest(RequestMethod method, String path,
			String data) throws MainzellisteNetworkException {
		HttpUriRequest request;
		URI absoluteUri = mainzellisteURI.resolve(path);
		switch (method) {
		case GET:
			request = new HttpGet(absoluteUri);
			break;
		case POST:
			HttpPost postRequest = new HttpPost(absoluteUri);
			if (data != null) {
				postRequest.setHeader("Content-Type", "application/json");
				try {
					postRequest.setEntity(new StringEntity(data.toString()));
				} catch (Throwable t) {
					throw new MainzellisteNetworkException(
							"Error while performing a " + method
									+ " request to " + absoluteUri, t);
				}
			}
			request = postRequest;
			break;
		case PUT:
			HttpPut putRequest = new HttpPut(absoluteUri);
			if (data != null) {
				putRequest.setHeader("Content-Type", "application/json");
				try {
					putRequest.setEntity(new StringEntity(data.toString()));
				} catch (Throwable t) {
					throw new MainzellisteNetworkException(
							"Error while performing a " + method
									+ " request to " + absoluteUri, t);
				}
			}
			request = putRequest;
			break;

		case DELETE:
			request = new HttpDelete(absoluteUri);
			break;
		default:
			throw new Error("doRequest called with illegal method " + method);
		}

		request.setHeader("mainzellisteApiKey", mainzellisteApiKey);
		request.setHeader("mainzellisteApiVersion", mainzellisteApiVersion);
		request.setHeader("Accepts", "application/json");

		try {
			return new MainzellisteResponse(httpClient.execute(request));
		} catch (Throwable t) {
			throw new MainzellisteNetworkException("Error while performing a "
					+ method + " request to " + absoluteUri, t);
		}
	}
}