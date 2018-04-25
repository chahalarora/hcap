package SecureResServer.SecureResServer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.junit.Before;
import org.junit.Test;

import ExceptionList.ExceptionList;

public class testExceptionList 
{
	
	String userID = "testUser";
	HashMap<String, Object> payloadMap = new HashMap<String, Object>();
	
	byte[] payload;
	
	@Before
	public void InitTest()
	{
		//nameDefs={n0=[[1, 2], {3=n1, 12=n2}], n1=[[4, 5], {6=n3}], n2=[[13], {14=n4}], n3=[[7], {8=n5}], n4=[[15], {}], n5=[[9], {10=n6}], n6=[[11], {}]}
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
		
		capability.put("tCreated", 11111111);
		
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
}