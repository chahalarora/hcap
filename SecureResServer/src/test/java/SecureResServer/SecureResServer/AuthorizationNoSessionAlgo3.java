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

public class AuthorizationNoSessionAlgo3 
{
	
	String userID = "testUser";
	HashMap<String, Object> payloadMap = new HashMap<String, Object>();
	
	byte[] payload;
	
	@Before
	public void InitTest()
	{
		
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
	
	@Test
	public void validateByConfirmingWithAS()
	{
		HCAPResourceServer.authServerAddress = "coaps://" + "127.0.0.1:5683";
		//setting up the resource server client to send request
		HCAPResourceServer.resToAuth = new CoapClient(HCAPResourceServer.authServerAddress); 				//address of Authorization Server
		buildDTLSConnectorForUpdate();
		HCAPResourceServer.resToAuth.setEndpoint(new CoapEndpoint(HCAPResourceServer.updateConnector, NetworkConfig.getStandard()));
		//timeout set to 100 seconds
		HCAPResourceServer.resToAuth.setTimeout(1000000);
		
		HCAPAuthorize auth = new HCAPAuthorize(userID, payload, 2);
		assertTrue(auth.evaluateRequest());
	}
	
	private DTLSConnector buildDTLSConnectorForUpdate()
	{
		String trustStoreLocation = null;
		String trustStorePassword = null;;
		String keyStoreLocation = null;
		String keyStorePassword = null;;
		String propFileLocation = "/home/lakshya/Desktop/multiResServerCode/DTLScodeMultiResServer/PropertiesFiles/ResServer.properties";
		try
		{
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties prop = new Properties();
		    prop.load(in);
		    trustStoreLocation = prop.getProperty("trustStoreLocation");
		    trustStorePassword = prop.getProperty("trustStorePassword");
		    keyStoreLocation = prop.getProperty("keyStoreLocation");
		    keyStorePassword = prop.getProperty("keyStorePassword");
		   
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		try
		{
			//load key store
			KeyStore keyStore = KeyStore.getInstance("JKS");
			InputStream in = new FileInputStream(keyStoreLocation);
			keyStore.load(in, keyStorePassword.toCharArray());
			in.close();

			//load trust store
			KeyStore trustStore = KeyStore.getInstance("JKS");
			in = new FileInputStream(trustStoreLocation);
			trustStore.load(in, trustStorePassword.toCharArray());
			in.close();

			//You can load multiple certificates if needed
			Certificate[] trustedCertificates = new Certificate[1];
			trustedCertificates[0] = trustStore.getCertificate("root");
			
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0));
			builder.setPskStore(new StaticPskStore("Client_identity", "secretPSK".getBytes()));
			builder.setIdentity((PrivateKey)keyStore.getKey("client", keyStorePassword.toCharArray()), keyStore.getCertificateChain("client"), false);
			builder.setTrustStore(trustedCertificates);
			builder.setClientAuthenticationRequired(true);
			
			HCAPResourceServer.updateConnector = new DTLSConnector(builder.build());	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return HCAPResourceServer.updateConnector;
	}
}
