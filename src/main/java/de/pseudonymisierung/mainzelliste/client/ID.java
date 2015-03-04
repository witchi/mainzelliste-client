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
     * Hash code of this object (for implementation of {@link Object#hashCode()}
     * ).
     */
    private final int hashCode;

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
            throw new NullPointerException("Cannot create ID with idString null");
        this.idType = idType;
        this.idString = idString;
        // Compute hash code only once, as this object is immutable
        this.hashCode = (idType + idString).hashCode();
    }

    /**
     * Get the ID type (in the sense of domain or namespace) of this identifier.
     * The ID type should match one of the ID types configured in the referenced
     * Mainzelliste instance.
     * 
     * @return The type of this identifier.
     */
    public String getIdType() {
        return idType;
    }

    /**
     * Get the identifier string of this patient identifier.
     * 
     * @return The value of this identifier.
     */
    public String getIdString() {
        return idString;
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
        return (this.idString.equals(idToCompare.getIdString()) && this.idType.equals(idToCompare.getIdType()));
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

    /**
     * Get a JSON representation of this object.
     * 
     * @return A JSON representation of this id as defined in the Mainzelliste
     *         API.
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
}