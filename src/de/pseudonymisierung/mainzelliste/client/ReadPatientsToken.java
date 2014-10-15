package de.pseudonymisierung.mainzelliste.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ReadPatientsToken extends Token {

	private LinkedList<ID> searchIds;
	private Set<String> resultFields;
	private Set<String> resultIds;
	
	public ReadPatientsToken() {
		this.searchIds = new LinkedList<ID>();
		this.resultFields = new HashSet<String>();
		this.resultIds = new HashSet<String>();
	}
	
	public ReadPatientsToken addSearchId(ID id) {
		this.searchIds.add(id);
		return this;
	}
	
	public ReadPatientsToken addResultField(String fieldName) {
		this.resultFields.add(fieldName);
		return this;
	}
	
	public ReadPatientsToken addResultId(String idType) {
		this.resultIds.add(idType);
		return this;
	}
	
	/**
	 * @return the resultFields
	 */
	public Set<String> getResultFields() {
		return resultFields;
	}

	/**
	 * @param resultFields the resultFields to set
	 */
	public void setResultFields(Collection<String> resultFields) {
		this.resultFields = new HashSet<String>(resultFields);
	}

	/**
	 * @return the resultIds
	 */
	public Set<String> getResultIds() {
		return resultIds;
	}

	/**
	 * @param resultIds the resultIds to set
	 */
	public void setResultIds(Collection<String> resultIds) {
		this.resultIds = new HashSet<String>(resultIds);
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

	public static void main(String args[]) {
		ReadPatientsToken t = new ReadPatientsToken();
		t.addSearchId(new ID("intid", "123"))
			.addResultField("name")
			.addResultField("geburtsjahr")
			.addResultId("pid");
		System.out.println(t.toJSON().toString());		
	}
}