package de.pseudonymisierung.mainzelliste.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Token that authorizes to edit IDAT fields of a patient.
 */
public class EditPatientToken extends Token {

	/** ID of the patient that can be edited. */
	private ID patientId;
	/** Redirect URL */
	private URL redirect;
	/** Names of fields that can be edited (null means "all fields"). */
	private Set<String> fieldsToEdit;

	/**
	 * Create an instance. As an "editPatient" token is always related to a
	 * specific patient, this is the only allowed constructor.
	 * 
	 * @param patientId
	 *            ID of the patient that can be edited with this token.
	 */
	public EditPatientToken(ID patientId) {
		this.patientId = patientId;
		fieldsToEdit = new HashSet<String>();
	}

	/**
	 * Set a URL to which the user is redirected after using the token.
	 * 
	 * @param url
	 *            The URL to redirect to. Typically a web page on the MDAT
	 *            server.
	 * @return The updated object.
	 * @throws MalformedURLException
	 *             If url is not a valid URL.
	 */
	public EditPatientToken redirect(String url) throws MalformedURLException {
		this.redirect = new URL(url);
		return this;
	}

	/**
	 * Set a URL to which the user is redirected after using the token.
	 * 
	 * @param url
	 *            The URL to redirect to. Typically a web page on the MDAT
	 *            server.
	 * @return The updated object.
	 */
	public EditPatientToken redirect(URL url) {
		this.redirect = url;
		return this;
	}

	/**
	 * Set the fields that can be edited with this token.
	 * 
	 * @param fieldNames
	 *            A list of field names that match the field definitions on the
	 *            Mainzelliste or null, which means that all fields can be
	 *            edited.
	 * @return The updated object.
	 */
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