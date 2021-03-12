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

import org.codehaus.jettison.json.JSONObject;

/**
 * Abstract representation of an authorizations token. A token authorizes to
 * execute a specified action on a Mainzelliste instance. Typically, tokens are
 * handed to a third party, which can then execute the respective action
 * authorized by the token identifier alone. Depending on the token type,
 * additional payload data can be specified (see subclasses for details).
 * 
 * @see Session#getToken(Token) for a general method to get Tokens from the
 *      Mainzelliste.
 */
public abstract class Token {

    /**
     * Identifier of the token, unique for the respective Mainzelliste instance.
     */
    private String tokenId;

    /**
     * AuditTrail information related to this token
     */
    private AuditTrailLog auditTrailLog;

    /**
     * Get the token identifier.
     * 
     * @return The identifier of this token.
     */
    public String getTokenId() {
        return this.tokenId;
    }

    /**
     * Get the AuditTrail information of this token.
     *
     * @return The AuditTrail information related to this token.
     */
    public AuditTrailLog getAuditTrailLog() {
      return auditTrailLog;
    }

    /**
     * Add the AuditTrail information to this token.
     *
     * @param auditTrailLog, contains necessary information for AuditTrail
     */
    public void setAuditTrailLog(AuditTrailLog auditTrailLog) {
      this.auditTrailLog = auditTrailLog;
    }

    /**
     * Get a JSON representation of this token in the format understood by
     * Mainzelliste. This does not include the token identifier, which is
     * assigned by Mainzelliste upon registering the token.
     * 
     * @return The JSON representation of this token.
     */
    public abstract JSONObject toJSON();

}
