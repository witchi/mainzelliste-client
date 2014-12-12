package de.pseudonymisierung.mainzelliste.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Token that allows reading specific IDAT fields or IDs of a patient. Used to
 * implement Temp IDs.
 */
public class ReadPatientsToken extends Token {

	/** IDs of the patients to read. */
	private LinkedList<ID> searchIds;
	/** IDAT fields that can be read with this token */
	private Set<String> resultFields;
	/** Types of IDs that can be read with this token */
	private Set<String> resultIds;

	/** Create an instance. */
	public ReadPatientsToken() {
		this.searchIds = new LinkedList<ID>();
		this.resultFields = new HashSet<String>();
		this.resultIds = new HashSet<String>();
	}

	/**
	 * Add a patient to the list of patients that can be read with this token.
	 * 
	 * @param id
	 *            An ID of the patient to add.
	 * @return The updated object.
	 */
	public ReadPatientsToken addSearchId(ID id) {
		this.searchIds.add(id);
		return this;
	}

	/**
	 * Add a field to the list of fields that appear in the result.
	 * 
	 * @param fieldName
	 *            Name of a field, must match a field definition on the
	 *            Mainzelliste.
	 * @return The updated object.
	 */
	public ReadPatientsToken addResultField(String fieldName) {
		this.resultFields.add(fieldName);
		return this;
	}

	/**
	 * Add an ID type to the list of ID types that appear in the result.
	 * 
	 * @param idType
	 *            Name of ID type. Must match an ID type definition on the
	 *            Mainzelliste.
	 * @return The updated object.
	 */
	public ReadPatientsToken addResultId(String idType) {
		this.resultIds.add(idType);
		return this;
	}

	/**
	 * Get the list of fields that can be read with this token.
	 * 
	 * @return List of field names.
	 */
	public Set<String> getResultFields() {
		return resultFields;
	}

	/**
	 * Set the fields that can be read with this token.
	 * 
	 * @param resultFields
	 *            List of field names, each of which must match a field
	 *            definition on the Mainzelliste.
	 * @return The updated object.
	 */
	public ReadPatientsToken setResultFields(Collection<String> resultFields) {
		this.resultFields = new HashSet<String>(resultFields);
		return this;
	}

	/**
	 * Get the list of ID types that can be read with this token.
	 * 
	 * @return List of ID types.
	 */
	public Set<String> getResultIds() {
		return resultIds;
	}

	/**
	 * Set the list of ID types that can be read with this token.
	 * 
	 * @param resultIds
	 *            List of ID types, each of which must match a ID type
	 *            definition on the Mainzelliste.
	 * @return The updated object.
	 */
	public ReadPatientsToken setResultIds(Collection<String> resultIds) {
		this.resultIds = new HashSet<String>(resultIds);
		return this;
	}

	@Override
	public JSONObject toJSON() {
		try {
			JSONObject result = new JSONObject();
			result.put("type", "readPatients");
			JSONObject data = new JSONObject();
			JSONArray dataSearchIds = new JSONArray();
			for (ID id : searchIds)
				dataSearchIds.put(id.toJSON());
			data.put("searchIds", dataSearchIds);
			data.put("resultFields", this.resultFields);
			data.put("resultIds", this.resultIds);
			result.put("data", data);
			return result;
		} catch (JSONException e) {
			throw new Error(e);
		}
	}
}