package de.pseudonymisierung.mainzelliste.client;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AddPatientTokenTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        // Set some values for all parameters and check that JSON result is
        // correct.
        AddPatientToken t = new AddPatientToken();
        Map<String, String> testFields = new HashMap<String, String>();
        testFields.put("Vorname", "Karl");
        testFields.put("Nachname", "Müller");
        for (String key : testFields.keySet())
            t.addField(key, testFields.get(key));
        String redirect = "https://mdat.example.org/newPatient?pid={pid}";
        t.redirect(redirect);
        String callback = "https://mdat.example.org/newPatientCallback";
        try {
            t.callback(new URL(callback));
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
        String idTypes[] = { "pid", "labid" };
        for (String idType : idTypes)
            t.addIdType(idType);

        JSONObject tJSON = t.toJSON();

        try {
            // check type
            assertEquals("Wrong token type", "addPatient", tJSON.getString("type"));
            // check fields
            JSONObject dataJSON = tJSON.getJSONObject("data");
            JSONObject fieldsJSON = dataJSON.getJSONObject("fields");
            for (String fieldName : testFields.keySet()) {
                assertTrue("Field " + fieldName + "not found in JSON output", fieldsJSON.has(fieldName));
                assertEquals("Wrong value of field " + fieldName + " in JSON output", testFields.get(fieldName),
                        fieldsJSON.getString(fieldName));
            }
            // check that no extra fields appear
            Iterator<?> it = fieldsJSON.keys();
            while (it.hasNext()) {
                String fieldName = it.next().toString();
                assertTrue("Additional field " + fieldName + " found in JSON output",
                        testFields.keySet().contains(fieldName));
            }

            // Check ID types
            JSONArray idTypesJSON = dataJSON.getJSONArray("idTypes");
            Set<String> setIdTypesJSON = new HashSet<String>();
            for (int i = 0; i < idTypesJSON.length(); i++)
                setIdTypesJSON.add(idTypesJSON.getString(i));
            assertEquals("The requested id types differ in JSON output ", new HashSet<String>(Arrays.asList(idTypes)),
                    setIdTypesJSON);

            // Check callback and redirect
            assertEquals("Redirect differs in JSON output", redirect, dataJSON.getString("redirect"));
            assertEquals("Callback differs in JSON output", callback, dataJSON.getString("callback"));
        } catch (JSONException e) {
            fail("Exception while reading from JSON output: " + e.getMessage());
        }

    }

    /**
     * Test functionality to set externally generated ID in addPatient token.
     */
    @Test
    public void testExternalId() {
        try {
            String externalIdType = "extId";
            String externalIdString = "valueOfExtId";
            AddPatientToken t = new AddPatientToken();
            JSONObject tokenJson = t.toJSON();
            JSONObject dataJson = tokenJson.getJSONObject("data");
            assertFalse("Token has external IDs although none have been defined.",
                    tokenJson.has("ids") && dataJson.getJSONArray("ids").length() == 0);
            t.addExternalId(externalIdType, externalIdString);
            tokenJson = t.toJSON();
            dataJson = tokenJson.getJSONObject("data");
            assertTrue("Token does not contain added external ID", dataJson.has("ids"));
            JSONObject externalIds = dataJson.getJSONObject("ids");
            assertEquals("Number of external ids differs", 1, externalIds.length());
            assertTrue("No external ID of expected type found", externalIds.has(externalIdType));
            assertEquals("Value of external ID differs", externalIdString, externalIds.getString(externalIdType));
            // TODO test with multiple external IDs
        } catch (JSONException e) {
            e.printStackTrace();
            fail("Caught JSONException");
        }
    }

}
