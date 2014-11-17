package de.pseudonymisierung.mainzelliste.client;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AddPatientToken extends Token {

	private LinkedList<String> idTypes = new LinkedList<String>();
	private URL callback = null;
	private String redirect = null;
	private Map<String, String> fields = new HashMap<String, String>();

	public AddPatientToken addIdType(String idType) {
		idTypes.add(idType);
		return this;
	}
	
	public AddPatientToken addField(String fieldName, String value) {
		this.fields.put(fieldName, value);
		return this;
	}
	
	public AddPatientToken callback(URL callback) {
		this.callback = callback;
		return this;
	}

	public AddPatientToken redirect(String redirect) {
		this.redirect = redirect;
		return this;
	}
	
	@Override
	public JSONObject toJSON() {
		try {
			JSONObject token = new JSONObject();
			token.put("type", "addPatient");
			JSONObject data = new JSONObject();
			data.put("callback", this.callback);
			data.put("redirect", this.redirect);
			
			if (this.idTypes.size() > 0) {
				JSONArray idTypes = new JSONArray();
				for (String thisIdType : this.idTypes) {
					idTypes.put(thisIdType);
				}
				data.put("idTypes", idTypes);
			}
			
			token.put("data", data);
			return token;
		} catch (JSONException e) {
			throw new Error(e);
		}
	}

}