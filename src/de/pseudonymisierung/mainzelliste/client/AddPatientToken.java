package de.pseudonymisierung.mainzelliste.client;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a token of type "addPatient". This allows creation of a patient by
 * submitting IDAT to the Mainzelliste.
 */
public class AddPatientToken extends Token {

	/** The id types that are created when using this token. */
	private LinkedList<String> idTypes = new LinkedList<String>();
	/** URL of the callback request. */
	private URL callback = null;
	/** URL to which the user is redirected after creating the patient. */
	private String redirect = null;
	/**
	 * Field values that the Mainzelliste should store in addition to the fields
	 * provided by the token user.
	 */
	private Map<String, String> fields = new HashMap<String, String>();

	/**
	 * Add an id type to the set of id types. Of this token. IDs of all defined
	 * types are created (or existing ones returned if the patient still exists)
	 * upon using the token.
	 * 
	 * @param idType
	 *            Name of an id type. Must match on of the id types defined on
	 *            the Mainzelliste.
	 * @return The updated object.
	 */
	public AddPatientToken addIdType(String idType) {
		idTypes.add(idType);
		return this;
	}

	/**
	 * Add a pre-defined field. Pre-defined fields are stored in addition to the
	 * fields that the user submits upon using the token.
	 * 
	 * @param fieldName
	 *            Field name. Must match one of the fields defined on the
	 *            Mainzelliste.
	 * @param value
	 *            Field value. Must match the format defined on the
	 *            Mainzelliste.
	 * @return The updated object.
	 */
	public AddPatientToken addField(String fieldName, String value) {
		this.fields.put(fieldName, value);
		return this;
	}

	/**
	 * Add a callback URL to which the token id and the generated pseudonyms are
	 * transmitted (see Mainzelliste API for details).
	 * 
	 * @param callback
	 *            Callback URL.
	 * @return The updated object.
	 */
	public AddPatientToken callback(URL callback) {
		this.callback = callback;
		return this;
	}

	/**
	 * Add a redirect URL to which the user
	 * 
	 * @param redirect
	 *            The redirect URL. Can include parameters of the form
	 *            "{parameter}" that must match id types; also, "{tokenId}" can
	 *            be used to include the token id in the URL.
	 * @return The updated object.
	 */
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