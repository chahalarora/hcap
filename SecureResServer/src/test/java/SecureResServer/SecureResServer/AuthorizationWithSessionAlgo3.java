package SecureResServer.SecureResServer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ExceptionList.ExceptionList;

import junit.framework.TestCase;

public class AuthorizationWithSessionAlgo3 
{
	String userID = "testUser";
	HashMap<String, Object> payloadMap = new HashMap<String, Object>();
	
	byte[] payload;
	
	@Before
	public void InitTest()
	{
		createDemoSession();
		
		HashMap<String, Object> capability = new HashMap<String, Object>();
		capability.put("vId", "127.0.0.1:8080");
		capability.put("certType", true);
		capability.put("sessID", 1);
		
		ArrayList<Long> stPerm = new ArrayList<Long>();
		stPerm.add((long) 1);
		capability.put("stPerm", stPerm);
		
		HashMap<Long, String> transPerm = new HashMap<Long, String>();
		transPerm.put((long) 2, null);
		transPerm.put((long) 3, null);
		capability.put("transPerm", transPerm);
		
		capability.put("tCreated", System.currentTimeMillis());
		
		HashMap<String, Object> defMap = new HashMap<String, Object>(); 
		capability.put("nameDefs", defMap);
		
		String toHashString = HashFunctionality.getString(capability);
		capability.put("hash", HashFunctionality.SHA256hash(toHashString.concat(HCAPResourceServer.serverSharedKey).concat(userID)));
		
		payloadMap.put("ticket", capability);
		
		
		if(HCAPResourceServer.isCBOR)
		{
			cborConverter cCon = new cborConverter();
			payload = cCon.convertToCBOR(payloadMap);
		}
		else
		{
			jsonConverter jCon = new jsonConverter();
			payload = jCon.convertToJSON(payloadMap);
		}
	}
	
	public void createDemoSession()
	{
		ExceptionList ex = new ExceptionList(userID, 0, System.currentTimeMillis(), null);
		HCAPResourceServer.lisMap.put((long) 1, ex);
	}
	
	@Test
	public void userIDDifferent()
	{
		String uID = "differentID";
		HCAPAuthorize auth = new HCAPAuthorize(uID, payload, 2);
		
		assertFalse(auth.evaluateRequest());
	}
	
	@Test
	public void userIDSame()
	{
		HCAPAuthorize auth = new HCAPAuthorize(userID, payload, 2);
		assertTrue(auth.evaluateRequest());
	}
	
	@After
	public void destroyTest()
	{
		
	}
	

}
