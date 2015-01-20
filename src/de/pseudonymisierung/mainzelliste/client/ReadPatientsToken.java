/*
 * Copyright (C) 2015 Working Group on Joint Research, University Medical Center Mainz
 * Contact: info@osse-register.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it 
 * with Jersey (https://jersey.java.net) (or a modified version of that 
 * library), containing parts covered by the terms of the General Public 
 * License, version 2.0, the licensors of this Program grant you additional 
 * permission to convey the resulting work.
 */
package de.pseudonymisierung.mainzelliste.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a token of type "readPatients", which authorizes to retreive IDAT
 * and/or identifers of one or multiple patients. Used for implementing
 * temporary identifiers.
 */
public class ReadPatientsToken extends Token {

	/**
	 * Permanent identifiers of patients whose data should be retreived.
	 */
	private LinkedList<ID> searchIds = new LinkedList<ID>();;
	/**
	 * List of names of IDAT fields that should appear in the result.
	 */
	private Set<String> resultFields = new HashSet<String>();;
	/**
	 * List of types of identifiers that should appear in the result.
	 */
	private Set<String> resultIds = new HashSet<String>();;

	/**
	 * Add a patient to the list of patients for which data should be retreived.
	 * 
	 * @param id
	 *            Permanent identifier of a patient.
	 * @return The modified token object.
	 */
	public ReadPatientsToken addSearchId(ID id) {
		this.searchIds.add(id);
		return this;
	}

	/**
	 * Get the list of fields that can be retrieved with this token.
	 * 
	 * @return Set of field names. An empty set if no result fields have been
	 *         defined.
	 */
	public Set<String> getResultFields() {
		return resultFields;
	}

	/**
	 * Set the list of IDAT fields that can be retrieved with this token.
	 * 
	 * @param resultFields
	 *            A collection of field names. Provide an empty collection if no
	 *            result fields are desired.
	 * @return The modified token object.
	 */
	public ReadPatientsToken setResultFields(Collection<String> resultFields) {
		this.resultFields = new HashSet<String>(resultFields);
		return this;
	}

	/**
	 * Add a field to the list of IDAT fields that should be retrieved.
	 * 
	 * @param fieldName
	 *            Name of a field.
	 * @return The modified token object.
	 */
	public ReadPatientsToken addResultField(String fieldName) {
		this.resultFields.add(fieldName);
		return this;
	}

	/**
	 * Get the list of ids that can be retrieved with this token.
	 * 
	 * @return Set of id type names. An empty list if none are defined.
	 */
	public Set<String> getResultIds() {
		return resultIds;
	}

	/**
	 * Set the list of id types to include in the result.
	 * 
	 * @param resultIds
	 *            Collection of id type names. Provide an empty collection if no
	 *            result ids are desired.
	 */
	public void setResultIds(Collection<String> resultIds) {
		this.resultIds = new HashSet<String>(resultIds);
	}

	/**
	 * Add an id type to appear in the result.
	 * 
	 * @param idType
	 *            An id type.
	 * @return The modified token object.
	 */
	public ReadPatientsToken addResultId(String idType) {
		this.resultIds.add(idType);
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