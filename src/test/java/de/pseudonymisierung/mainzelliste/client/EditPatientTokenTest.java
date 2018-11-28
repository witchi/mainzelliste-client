package de.pseudonymisierung.mainzelliste.client;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test behavior of class EditPatientToken. The tests construct instances and
 * check that the JSON representation reflects the set properties.
 */
public class EditPatientTokenTest {

	private ID patientToEdit = new ID("pid", "ABCD1234");

	/**
	 * Construction of EditPatientToken should generate a token denoting the
	 * patient to edit but no further data items.
	 */
	@Test
	public void testEditPatientToken() {
		EditPatientToken t = new EditPatientToken(patientToEdit);
		JSONObject tokenJson = t.toJSON();
		try {
			assertTrue("Token does not contain data", tokenJson.has("data"));
			assertEquals("Token data has wrong type", JSONObject.class, tokenJson.get("data").getClass());
			JSONObject tokenData = tokenJson.getJSONObject("data");
			assertTrue("Token does not contain patient ID: " + tokenJson.toString(2), tokenData.has("patientId"));
			JSONObject patientId = tokenData.getJSONObject("patientId");
			assertEquals("Patient ID in token has wrong type", patientToEdit.getIdType(),
					patientId.getString("idType"));
			assertEquals("Patient ID in token has wrong ID string", patientToEdit.getIdString(),
					patientId.getString("idString"));
			// Check that nothing else than patient ID is defined in the JSON
			// output
			assertTrue("Token has unexpected data items: " + tokenJson.toString(), tokenData.length() == 1);
		} catch (JSONException e) {
			fail("Exception while handling JSON object: " + e);
		}
	}

	/**
	 * Test setting and resetting editable fields.
	 */
	@Test
	public void testSetFieldsToEdit() {
		EditPatientToken t = new EditPatientToken(patientToEdit);
		List<String> fieldNames = Arrays.asList("Vorname", "Nachname");
		t.setFieldsToEdit(fieldNames);
		try {
			JSONObject tokenJson = t.toJSON();
			JSONObject tokenData = tokenJson.getJSONObject("data");
			assertTrue("Token does not contain field definitions: " + tokenJson.toString(2), tokenData.has("fields"));
			assertEquals("Field list is not an array: " + tokenJson.toString(2), JSONArray.class,
					tokenData.get("fields").getClass());
			JSONArray fields = tokenData.getJSONArray("fields");
			HashSet<String> fieldsBuffer = new HashSet<>(fieldNames);
			for (int i = 0; i < fieldNames.size(); i++) {
				String thisField = fields.getString(i);
				assertTrue("Unexpected field " + thisField + " in output", fieldsBuffer.contains(thisField));
				fieldsBuffer.remove(thisField);
			}
			assertTrue("Fields added but not in output: " + fieldsBuffer, fieldsBuffer.isEmpty());

			t.setFieldsToEdit(null);
			assertFalse("Output still contains fields after removing them: " + t.toJSON().toString(2),
					t.toJSON().getJSONObject("data").has("fields"));

		} catch (JSONException e) {
			fail("Exception while handling JSON object: " + e);
		}
	}

	/**
	 * Test setting and resetting editable fields.
	 */
	@Test
	public void testSetIdsToEdit() {
		EditPatientToken t = new EditPatientToken(patientToEdit);
		List<String> idTypes = Arrays.asList("labId", "extId");
		t.setIdsToEdit(idTypes);
		try {
			JSONObject tokenJson = t.toJSON();
			JSONObject tokenData = tokenJson.getJSONObject("data");
			assertTrue("Token does not contain ID definitions: " + tokenJson.toString(2), tokenData.has("ids"));
			assertEquals("ID list is not an array: " + tokenJson.toString(2), JSONArray.class,
					tokenData.get("ids").getClass());
			JSONArray ids = tokenData.getJSONArray("ids");
			HashSet<String> idsBuffer = new HashSet<>(idTypes);
			for (int i = 0; i < idTypes.size(); i++) {
				String thisId = ids.getString(i);
				assertTrue("Unexpected ID " + thisId + " in output", idsBuffer.contains(thisId));
				idsBuffer.remove(thisId);
			}
			assertTrue("IDs added but not in output: " + idsBuffer, idsBuffer.isEmpty());

			t.setIdsToEdit(null);
			assertFalse("Output still contains IDs after removing them: " + t.toJSON().toString(2),
					t.toJSON().getJSONObject("data").has("ids"));

		} catch (JSONException e) {
			e.printStackTrace();
			fail("Exception while handling JSON object: " + e);
		}
	}

	/**
	 * Test setting and unsetting a redirect URL.
	 */
	@Test
	public void testRedirect() {
		try {
			URL redirectUrl = new URL("http://redirect1.example.org");
			String redirectString = "http://redirect2.example.org";
			EditPatientToken t = new EditPatientToken(patientToEdit);
			t.redirect(redirectUrl);
			checkRedirect(t, redirectUrl.toString());
			t.redirect((URL) null);
			assertFalse("Token still contains redirect after resetting: " + t.toJSON().toString(2),
					t.toJSON().getJSONObject("data").has("redirect"));
			t.redirect(redirectString);
			checkRedirect(t, redirectString);
			t.redirect((String) null);
			assertFalse("Token still contains redirect after resetting: " + t.toJSON().toString(2),
					t.toJSON().getJSONObject("data").has("redirect"));
		} catch (MalformedURLException e) {
			throw new Error(e);
		} catch (JSONException e) {
			fail("Exception while handling JSON object: " + e);
		}
	}

	private void checkRedirect(Token t, String expected) throws JSONException {
		JSONObject tokenData = t.toJSON().getJSONObject("data");
		assertTrue("Token does not contain any redirect: " + t.toJSON().toString(2), tokenData.has("redirect"));
		assertEquals("Data item for redirect has wrong type", String.class, tokenData.get("redirect").getClass());
		assertEquals("Unexpected value for redirect", expected, tokenData.get("redirect"));
	}

	/**
	 * Setters should modify and return the object on which they are called.
	 */
	public void testMethodChaining() {
		fail("Not implemented yet");
	}
}
