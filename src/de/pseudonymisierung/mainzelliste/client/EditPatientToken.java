package de.pseudonymisierung.mainzelliste.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class EditPatientToken extends Token {

	private ID patientId;
	private URL redirect;
	private Set<String> fieldsToEdit;

	public EditPatientToken(ID patientId) {
		this.patientId = patientId;
		fieldsToEdit = new HashSet<String>();
	}

	public EditPatientToken setRedirect(String url)
			throws MalformedURLException {
		this.redirect = new URL(url);
		return this;
	}

	public EditPatientToken setRedirect(URL url) {
		this.redirect = url;
		return this;
	}

	public EditPatientToken setFieldsToEdit(Collection<String> fieldNames) {
		this.fieldsToEdit = new HashSet<String>(fieldNames);
		return this;
	}

	@Override
	public JSONObject toJSON() {
		try {
			JSONObject result = new JSONObject();
			result.put("type", "editPatient");
			JSONObject data = new JSONObject();
			data.put("patientId", this.patientId.toJSON());
			if (this.redirect != null)
				data.put("redirect", redirect.toString());
			if (this.fieldsToEdit.size() > 0) {
				JSONArray fieldsToEditJSON = new JSONArray();
				for (String s : fieldsToEdit) {
					fieldsToEditJSON.put(s);
				}
				data.put("fields", fieldsToEditJSON);
			}
			result.put("data", data);
			return result;
		} catch (JSONException e) {
			throw new Error(e);
		}
	}

}