package de.pseudonymisierung.mainzelliste.client;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AddPatientToken extends Token {

	private LinkedList<String> idTypes = new LinkedList<String>();
	private URL callback = null;
	private Map<String, String> fields = new HashMap<String, String>();

	public AddPatientToken addIdType(String idType) {
		idTypes.add(idType);
		return this;
	}
	
	public AddPatientToken callback(URL callback) {
		this.callback = callback;
		return this;
	}
	
	@Override
	public JSONObject toJSON() {
		try {
			JSONObject token = new JSONObject();
			token.put("type", "addPatient");
			JSONObject data = new JSONObject();
			data.put("callback", this.callback);
			
			if (this.idTypes != null) {
				// TODO
			}
			
			token.put("data", data);
			return token;
		} catch (JSONException e) {
			throw new Error(e);
		}
	}

}