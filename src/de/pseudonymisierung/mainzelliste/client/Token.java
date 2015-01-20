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
	 * Get the token identifier.
	 * 
	 * @return The identifier of this token.
	 */
	public String getTokenId() {
		return this.tokenId;
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