package de.pseudonymisierung.mainzelliste.client;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a patient identifier. A patient identifier consists of the 
 * identifying string and of a type. For one patient, multiple IDs with 
 * different types can be used to identify the patient within different
 * domains or namespaces.
 */
public class ID {

	private String idType;
	private String idString;
	private JSONObject json = null;
	/**
	 * Get the id type (in the sense of domain or namespace) of this identifier. The id type should match one of the id types configured in the referenced Mainzelliste instance.
	 */
	public String getIdType() {
		// TODO - implement ID.getIdType
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the identifier string of this patient identifier.
	 */
	public String getIdString() {
		// TODO - implement ID.getIdString
		throw new UnsupportedOperationException();
	}

	public JSONObject toJSON() {
		// As ID is immutable, the JSONObject is created only once and cached for later use
		if (this.json == null) {
			this.json = new JSONObject();
			try {
				this.json.put("idString", this.idString);
				this.json.put("idType", this.idType);
			} catch (JSONException e) {
				throw new Error(e);
			}
		}
		return this.json;
	}
	/**
	 * 
	 * @param idType
	 * @param idString
	 */
	public ID(String idType, String idString) {
		this.idType = idType;
		this.idString = idString;
	}

}