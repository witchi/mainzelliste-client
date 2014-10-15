package de.pseudonymisierung.mainzelliste.client;

import org.codehaus.jettison.json.JSONObject;

public abstract class Token {

	private String tokenId;

	public String getTokenId() {
		return this.tokenId;
	}

	public abstract JSONObject toJSON();

}