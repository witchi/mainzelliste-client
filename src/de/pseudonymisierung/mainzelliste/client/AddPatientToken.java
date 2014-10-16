package de.pseudonymisierung.mainzelliste.client;

import java.net.URL;
import java.util.LinkedList;

import org.codehaus.jettison.json.JSONObject;

public class AddPatientToken extends Token {

	private LinkedList<String> idTypes;
	private URL callback;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}