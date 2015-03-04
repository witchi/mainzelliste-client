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

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;

/**
 * Encapsulates the response to an HTTP request to a Mainzelliste instance.
 * Constructed by
 * {@link MainzellisteConnection#doRequest(de.pseudonymisierung.mainzelliste.client.MainzellisteConnection.RequestMethod, String, String)}
 * MainzellisteConnection#doRequest()}
 */
class MainzellisteResponse {

    /**
     * Returned HTTP status code.
     */
    private final int statusCode;

    /**
     * Returned entity.
     */
    private final String data;

    /**
     * Create an instance from a HTTP response.
     * 
     * @param response
     *            The HTTP response.
     * @throws MainzellisteNetworkException
     *             if an IO error occurs while reading the response entity.
     */
    MainzellisteResponse(CloseableHttpResponse response) throws MainzellisteNetworkException {
        this.statusCode = response.getStatusLine().getStatusCode();
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null)
                data = EntityUtils.toString(response.getEntity());
            else
                data = "";
        } catch (IOException e) {
            throw new MainzellisteNetworkException("IO error while reading response from Mainzelliste", e);
        }
    }

    /**
     * Get the HTTP status code of this response.
     * 
     * @return The HTTP status code of this response.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the returned entity.
     * 
     * @return The returned entity. If the response does not contain an entity,
     *         an empty String.
     */
    public String getData() {
        return data;
    }

    /**
     * Return response entity as a JSON object.
     * 
     * @return The returned entity.
     * @throws MainzellisteNetworkException
     *             if the response entity cannot be parsed to a JSON object.
     */
    public JSONObject getDataJSON() throws MainzellisteNetworkException {
        try {
            return new JSONObject(this.data);
        } catch (JSONException e) {
            throw new MainzellisteNetworkException("Error while parsing response from Mainzelliste", e);
        }
    }
}
