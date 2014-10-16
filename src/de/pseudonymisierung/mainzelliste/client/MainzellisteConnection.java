package de.pseudonymisierung.mainzelliste.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;

/**
 * Manages connections to one Mainzelliste instance, authenticated by a specific api key.
 * Provides a public method to create new Sessions and package methods providing
 * access to the Mainzelliste instance to be used by session objects.  
 *  
 */
public class MainzellisteConnection {

	private static final String mainzellisteApiVersion = "2.0";
	private final String mainzellisteApiKey;
	private final URI mainzellisteURI;
	
	private CloseableHttpClient httpClient;

	/**
	 * Initialize connection to Mainzelliste with default Http client.
	 * 
	 * @param mainzellisteURI Base URL of the Mainzelliste instance. 
	 * @param mainzellisteApiKey Api key to authenticate against the Mainzelliste instance.
	 * @throws URISyntaxException 
	 */
	public MainzellisteConnection(String mainzellisteURI, String mainzellisteApiKey) throws URISyntaxException {
		this(mainzellisteURI, mainzellisteApiKey, HttpClientBuilder.create().build());
	}

	/**
	 * Initialize connection to Mainzelliste with a provided Http client. This
	 * constructor should be used if special properties have to be set for Http connections
	 * (e.g. a proxy server).
	 *   
	 * @param mainzellisteURI Base URI of the Mainzelliste instance. 
	 * @param mainzellisteApiKey Api key to authenticate against the Mainzelliste instance.
	 * @param httpClient A HttpClient instance.
	 * @throws URISyntaxException 
	 */
	public MainzellisteConnection(String mainzellisteURI, String mainzellisteApiKey, CloseableHttpClient httpClient) throws URISyntaxException {
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

	public Session createSession() throws MainzellisteNetworkException {
		MainzellisteResponse response = this.doRequest(RequestMethod.POST, "sessions", null);
		int responseCode = response.getStatusCode(); 
		if (responseCode != 201) {
			throw  MainzellisteNetworkException.fromResponse(response);
		}
		JSONObject sessionData = response.getDataJSON();
		String sessionId;
		try {
			sessionId = sessionData.getString("sessionId");
		} catch (JSONException e) {
			// If we are here, Mainzelliste has responded with a correct status 
			// code but with illegal data, which is a fatal error.
			throw new Error("Request to create session returned illegal data", e);
		}	
		return new Session(sessionId, this);
	}

	public static enum RequestMethod {
		GET, POST, PUT, DELETE;
	}

	MainzellisteResponse doRequest(RequestMethod method, String path, String data) throws MainzellisteNetworkException {
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
					throw new MainzellisteNetworkException("Error while performing a " + method + " request to " + absoluteUri, t); 
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
					throw new MainzellisteNetworkException("Error while performing a " + method + " request to " + absoluteUri, t); 
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
			throw new MainzellisteNetworkException("Error while performing a " + method + " request to " + absoluteUri, t); 
		}
	}
	
	JSONObject getData(CloseableHttpResponse response) throws MainzellisteNetworkException {
		HttpEntity responseEntity;
		responseEntity = response.getEntity();
		String resultString;
		JSONObject result;
		try {
			resultString = EntityUtils.toString(responseEntity);
			if (resultString.equals(""))
				result = new JSONObject();
			else
				result = new JSONObject(resultString);
		} catch (Throwable t) {
			throw new MainzellisteNetworkException("An error occured while reading response from Mainzelliste.", t);
		}
			
		return result;		
	}	
	
	public static void main(String args[]) throws URISyntaxException, MainzellisteNetworkException {
		MainzellisteConnection con = new MainzellisteConnection("https://patientenliste.de/borg", "mdatborg");
		Session mySession = con.createSession();
		List<String> fieldsToShow = Arrays.asList("vorname", "nachname");
		List<String> idsToShow = Arrays.asList("pid");
		ID patientToShow = new ID("intid", "1");
		String tempId = mySession.getTempId(patientToShow, fieldsToShow, idsToShow);
		System.out.println(tempId);
	}
}