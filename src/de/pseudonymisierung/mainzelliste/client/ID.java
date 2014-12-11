package de.pseudonymisierung.mainzelliste.client;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a patient identifier. A patient identifier consists of the
 * identifying string and of a type. For one patient, multiple IDs with
 * different types can be used to identify the patient within different domains
 * or namespaces.
 */
public class ID {

	/** ID type (e.g. domain in which ID is valid). */
	private final String idType;
	/** Value of the ID. */
	private final String idString;
	/**
	 * JSON representation of this object. Used for caching the result of
	 * {@link ID#toJSON()} in case of multiple invocations.
	 */
	private JSONObject json = null;
	/**
	 * Hash code of this object (for implementation of {@link Object#hashCode())
	 */
	private final int hashCode;

	/**
	 * Get the ID type (in the sense of domain or namespace) of this identifier.
	 * The ID type should match one of the ID types configured in the referenced
	 * Mainzelliste instance.
	 */
	public String getIdType() {
		return idType;
	}

	/**
	 * Get the identifier string of this patient identifier.
	 */
	public String getIdString() {
		return idString;
	}

	/**
	 * Get a JSON representation of this object.
	 */
	public JSONObject toJSON() {
		// As ID is immutable, the JSONObject is created only once and cached
		// for later use
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
	 * Create a patient identifier.
	 * 
	 * @param idType
	 *            Type (aka domain / namespace) of the identifier.
	 * @param idString
	 *            The identifier string.
	 */
	public ID(String idType, String idString) {
		if (idType == null)
			throw new NullPointerException("Cannot create ID with idType null");
		if (idString == null)
			throw new NullPointerException(
					"Cannot create ID with idString null");
		this.idType = idType;
		this.idString = idString;
		// Compute hash code only once, as this object is immutable
		this.hashCode = (idType + idString).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ID))
			return false;
		ID idToCompare = (ID) obj;
		return (this.idString.equals(idToCompare.getIdString()) && this.idType
				.equals(idToCompare.getIdType()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.hashCode;
	}

}