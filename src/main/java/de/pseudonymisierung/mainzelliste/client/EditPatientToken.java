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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Represents a token of type "editPatient", which allows for changing a
 * patient's IDAT.
 */
public class EditPatientToken extends Token {

    /**
     * A permanent identifier of the patient that can be edited with this token.
     */
    private ID patientId;
    /**
     * The fields that can be edited by using this token.
     */
    private Set<String> fieldsToEdit;
    /**
     * The external IDs that can be edited by using this token.
     */
    private Set<String> idsToEdit;
    /**
     * A URL to redirect the user to after the edit operation.
     */
    private URL redirect;

    /**
     * Create a token for editing the patient identified by patientId.
     * 
     * @param patientId
     *            Permanent identifier of a patient.
     */
    public EditPatientToken(ID patientId) {
        this.patientId = patientId;
        fieldsToEdit = new HashSet<String>();
    }

    /**
     * Set the fields that can be edited by using this token.
     * 
     * @param fieldNames
     *            Collection of field names or null if no fields should be
     *            editable (the default).
     * @return The modified token object.
     */
    public EditPatientToken setFieldsToEdit(Collection<String> fieldNames) {
        this.fieldsToEdit = new HashSet<String>(fieldNames);
        return this;
    }

    /**
     * Set the external IDs that can be edited by using this token.
     * 
     * @param idTypes
     *            Collection of id types or null if no ids should be
     *            editable (the default).
     * @return The modified token object.
     */
    public EditPatientToken setIdsToEdit(Collection<String> idTypes) {
        this.idsToEdit = new HashSet<String>(idTypes);
        return this;
    }

    /**
     * Set a URL to redirect the user to after the edit operation.
     * 
     * @param url
     *            The redirect URL or null if no redirect is desired.
     * @return The modified token object.
     * @throws MalformedURLException
     *             if url is not a syntactically valid URL.
     */
    public EditPatientToken redirect(String url) throws MalformedURLException {
        this.redirect = new URL(url);
        return this;
    }

    /**
     * Set a URL to redirect the user to after the edit operation.
     * 
     * @param url
     *            The redirect url or null if no redirect is desired.
     * @return The modified token object.
     */
    public EditPatientToken redirect(URL url) {
        this.redirect = url;
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