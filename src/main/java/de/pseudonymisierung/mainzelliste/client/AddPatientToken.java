/*
 * Copyright (C) 2015 Working Group on Joint Research, University Medical Center
 * Mainz Contact: info@osse-register.de
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

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a token of type "addPatient". This token authorizes to create one
 * new patient on the Mainzelliste.
 */
public class AddPatientToken extends Token {

    /**
     * Names of id types that are created when using this token.
     */
    private LinkedList<String> idTypes = new LinkedList<String>();
    /**
     * Predefined fields. Map keys are field names, values the corresponding
     * field values.
     */
    private Map<String, String> fields = new HashMap<String, String>();
    /**
     * Callback URL called by Mainzelliste after creating the patient.
     */
    private URL callback = null;
    /**
     * URL (template string) to which to redirect the user after creating the
     * patient.
     */
    private String redirect = null;

    /**
     * Add an id type to the list of ids that should be created for the new
     * patient.
     * 
     * @param idType
     *            Name of an id type.
     * @return The modified token object.
     */
    public AddPatientToken addIdType(String idType) {
        idTypes.add(idType);
        return this;
    }

    /**
     * Add a predefined field. Can be used to include fields that should not be
     * entered by the user in the request to create a patient.
     * 
     * @param fieldName
     *            Name of the field.
     * @param value
     *            Value of the field.
     * @return The modified token object.
     */
    public AddPatientToken addField(String fieldName, String value) {
        this.fields.put(fieldName, value);
        return this;
    }

    /**
     * Set the URL to which Mainzelliste makes a callback request after creation
     * of the patient.
     * 
     * @param callback
     *            An URL or null if no callback is desired.
     * @return The modified token object.
     */
    public AddPatientToken callback(URL callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Set a URL to which the user should be redirected after creating the new
     * patient. The URL can include template parameters in curly braces.
     * Parameters can be any of the following:
     * <ul>
     * <li>The name of an id type. These parameters will be replaced with the
     * values of the respective created identifiers.
     * <li>The special parameter "tokenId". This will be replaced by the
     * identifier of the addPatient-Token used to execute the request.
     * 
     * @param redirect
     *            The URL template for the redirect or null if no redirect is
     *            desired.
     * @return The modified token object.
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

            if (this.fields.size() > 0) {
                JSONObject fieldsJSON = new JSONObject();
                for (String fieldName : fields.keySet()) {
                    fieldsJSON.put(fieldName, fields.get(fieldName));
                }
                data.put("fields", fields);
            }

            token.put("data", data);
            return token;
        } catch (JSONException e) {
            throw new Error(e);
        }
    }

}