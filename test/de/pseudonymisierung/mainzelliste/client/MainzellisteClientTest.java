package de.pseudonymisierung.mainzelliste.client;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainzellisteClientTest {

	private final String mainzellisteURL = "https://patientenliste.de/borg/";
	private final String mainzellisteApiKey = "mdatborg";
	private MainzellisteConnection con;
	private MainzellisteConnection con2;
	private final String testIds[] = {"1", "4", "5", "6", "10"};
	private final String defaultIdType = "intid";
	private final String defaultFields[] = {"vorname", "nachname"}; 
	
	@Before
	public void setUp() throws Exception {
		con = new MainzellisteConnection(mainzellisteURL, mainzellisteApiKey);
		con2 = new MainzellisteConnection(mainzellisteURL, mainzellisteApiKey);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws MainzellisteNetworkException, InvalidSessionException {
		// Create a session and some temp ids
		Session s = con.createSession();
		HashMap<String, ID> ids = new HashMap<String, ID>();
		for (String thisIdString : testIds) {
			ID thisId = new ID(defaultIdType, thisIdString);
			ids.put(s.getTempId(thisId), thisId);
		}
		
		Session s2 = con2.readSession(s.getSessionId());		
		assertEquals( "Restored session contains different temp ids", s.getTempIds(), s2.getTempIds());
		
		for (String tempId : s.getTempIds()) {
			ID id = s.getId(tempId);
			assertEquals("Got different id for same temp id", ids.get(tempId), id);
			assertEquals("Got different id for same temp id in restored session", id, s2.getId(tempId));
			
		}
	}

}
