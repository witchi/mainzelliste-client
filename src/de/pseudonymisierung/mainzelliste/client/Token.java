package de.pseudonymisierung.mainzelliste.client;

import org.codehaus.jettison.json.JSONObject;

/**
 * A token represents the authorization for a specific request to a Mainzelliste
 * instance, for example "create a new patient" or "get name of patient xy". It
 * is requested by the server that has access to the Mainzelliste and is usually
 * handed to other client applications (e.g. a user's web browser).
 * 
 * This class is an abstract definition of a token, specific token types are
 * represented by its subclasses.
 */
public abstract class Token {

	/** Unique identifier of the token. */
	private String tokenId = null;

	/**
	 * Get the token identifier. The identifier is created upon registering the
	 * token on the Mainzelliste.
	 * 
	 * @return The token identifier. May be null if a token has not been
	 *         registered on a Mainzelliste.
	 */
	public String getTokenId() {
		return this.tokenId;
	}

	/**
	 * Get JSON representation of this token for the purpose of making a token
	 * request to the Mainzelliste. Subclasses have to provide a specific
	 * implementation that conforms to the format specified by the Mainzelliste
	 * API.
	 * 
	 * @return A JSON representation of this token.
	 */
	public abstract JSONObject toJSON();

}